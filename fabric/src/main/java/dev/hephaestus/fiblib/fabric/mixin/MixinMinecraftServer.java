package dev.hephaestus.fiblib.fabric.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import dev.hephaestus.fiblib.impl.LifecycleHooks;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerResources;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
public class MixinMinecraftServer {
    @Shadow
    private PlayerList playerList;

    @Shadow
    private ProfilerFiller profiler;


    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(Thread thread, RegistryAccess.RegistryHolder impl, LevelStorageSource.LevelStorageAccess session, WorldData saveProperties, PackRepository resourcePackManager, Proxy proxy, DataFixer dataFixer, ServerResources serverResourceManager, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, GameProfileCache userCache, ChunkProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        LifecycleHooks.handleInit();
    }

    @Inject(method = "tickServer", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onTickServer(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        this.profiler.push("FibLib.Blocks.Tick");

        LifecycleHooks.handleTick(this.playerList);

        this.profiler.pop();
    }

    @Inject(method = "reloadResources", at = @At("HEAD"))
    private void onReloadResources(Collection<String> collection, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        LifecycleHooks.handleReloadResources();
    }
}
