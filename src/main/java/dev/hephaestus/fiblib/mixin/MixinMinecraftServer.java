package dev.hephaestus.fiblib.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.qouteall.immersive_portals.Global;
import com.qouteall.immersive_portals.chunk_loading.DimensionalChunkPos;
import com.qouteall.immersive_portals.ducks.IEThreadedAnvilChunkStorage;
import dev.hephaestus.fiblib.api.BlockFib;
import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.impl.FibLib;
import dev.hephaestus.fiblib.impl.LookupTable;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;
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
import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer implements LookupTable {
    @Shadow private PlayerManager playerManager;

    @Shadow private Profiler profiler;
    @Unique private HashMap<Triple<BlockFib, BlockState, UUID>, BlockState> playerLookupTable;
    @Unique private Collection<ServerPlayerEntity> updated;
    @Unique private MutableTriple<BlockFib, BlockState, UUID> triple;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initFields(Thread thread, DynamicRegistryManager.Impl impl, LevelStorage.Session session, SaveProperties saveProperties, ResourcePackManager resourcePackManager, Proxy proxy, DataFixer dataFixer, ServerResourceManager serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, UserCache userCache, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        this.playerLookupTable = new HashMap<>();
        this.updated = new HashSet<>();
        this.triple = new MutableTriple<>();
    }

    @Inject(method = "tick", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void tickFibber(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        this.profiler.push("FibLib.Blocks.Tick");

        Iterator<Triple<BlockFib, BlockState, UUID>> it = this.playerLookupTable.keySet().iterator();

        while (it.hasNext()) {
            Triple<BlockFib, BlockState, UUID> key = it.next();
            ServerPlayerEntity player = this.playerManager.getPlayer(key.getRight());

            if (player == null || player.isRemoved() || player.isDisconnected()) {
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
            for (ServerPlayerEntity player : updated) {
                FibLib.resendChunks(player);
            }

            updated.clear();
        }

        this.profiler.pop();
    }

    @Inject(method = "loadDataPacks", at = @At("HEAD"))
    private static void resetBlockFibs(ResourcePackManager resourcePackManager, DataPackSettings dataPackSettings, boolean safeMode, CallbackInfoReturnable<DataPackSettings> cir) {
        BlockFibRegistry.reset();
    }

    @Override
    public BlockState get(BlockState blockState, ServerPlayerEntity playerEntity) {
        this.triple.left = BlockFibRegistry.getBlockFib(blockState, playerEntity);

        if (this.triple.left == null) return blockState;

        this.triple.middle = blockState;
        this.triple.right = playerEntity.getUuid();

        if (!this.playerLookupTable.containsKey(this.triple)) {
            ImmutableTriple<BlockFib, BlockState, UUID> key =
                    ImmutableTriple.of(this.triple.left, this.triple.middle, this.triple.right);

            this.playerLookupTable.put(key, key.left.getOutput(blockState, playerEntity));
        }

        return this.playerLookupTable.get(triple);
    }
}
