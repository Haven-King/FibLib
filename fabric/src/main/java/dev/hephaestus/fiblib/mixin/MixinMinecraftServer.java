package dev.hephaestus.fiblib.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import dev.hephaestus.fiblib.api.BlockFib;
import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.impl.FibLib;
import dev.hephaestus.fiblib.impl.FibLog;
import dev.hephaestus.fiblib.impl.FibTest;
import dev.hephaestus.fiblib.impl.LookupTable;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.net.Proxy;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer implements LookupTable {
    @Shadow
    private PlayerList playerList;

    @Shadow
    private ProfilerFiller profiler;
    @Unique
    private HashMap<Triple<BlockFib, BlockState, UUID>, BlockState> playerLookupTable;
    @Unique
    private Collection<ServerPlayer> updated;
    @Unique
    private MutableTriple<BlockFib, BlockState, UUID> triple;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initFields(Thread thread, RegistryAccess.RegistryHolder impl, LevelStorageSource.LevelStorageAccess session, WorldData saveProperties, PackRepository resourcePackManager, Proxy proxy, DataFixer dataFixer, ServerResources serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, GameProfileCache userCache, ChunkProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        this.playerLookupTable = new HashMap<>();
        this.updated = new HashSet<>();
        this.triple = new MutableTriple<>();
    }

    @Inject(method = "tickServer", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void tickFibber(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        this.profiler.push("FibLib.Blocks.Tick");

        Iterator<Triple<BlockFib, BlockState, UUID>> it = this.playerLookupTable.keySet().iterator();

        while (it.hasNext()) {
            Triple<BlockFib, BlockState, UUID> key = it.next();
            ServerPlayer player = this.playerList.getPlayer(key.getRight());

            if (player == null || player.removed || player.hasDisconnected()) {
                it.remove();
            } else {
                BlockState newState = key.getLeft().getOutput(key.getMiddle(), player);

                if (newState != playerLookupTable.get(key)) {
                    playerLookupTable.put(key, newState);
                    updated.add(player);
                }
            }
        }

        if (!updated.isEmpty()) {
            for (ServerPlayer player : updated) {
                FibLib.resendChunks(player);
            }

            updated.clear();
        }

        this.profiler.pop();
    }

    @Inject(method = "reloadResources", at = @At("HEAD"))
    private static void resetBlockFibs(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        FibLog.enableDebug();
        BlockFibRegistry.reset();
        FibTest.fibIronOre();
        FibTest.fibCoalOre();
    }

    @Override
    public BlockState get(BlockState blockState, ServerPlayer playerEntity) {
        this.triple.left = BlockFibRegistry.getBlockFib(blockState, playerEntity);

        if (this.triple.left == null) return blockState;

        this.triple.middle = blockState;
        this.triple.right = playerEntity.getUUID();

        if (!this.playerLookupTable.containsKey(this.triple)) {
            ImmutableTriple<BlockFib, BlockState, UUID> key =
                    ImmutableTriple.of(this.triple.left, this.triple.middle, this.triple.right);

            this.playerLookupTable.put(key, key.left.getOutput(blockState, playerEntity));
        }

        return this.playerLookupTable.get(triple);
    }
}
