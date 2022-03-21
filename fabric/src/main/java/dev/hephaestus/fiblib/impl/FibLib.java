package dev.hephaestus.fiblib.impl;

import dev.hephaestus.fiblib.mixin.ChunkReloader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings("unused")
public class FibLib {
    public static final boolean DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();

    public static final ThreadLocal<ServerPlayer> PLAYER = new ThreadLocal<>();

    public static void resendChunks(ServerPlayer player) {
        if (FabricLoader.getInstance().isModLoaded("immersive_portals")) {
            ImmersivePortalsChunkManager.resendChunks(player);
        } else {
            ChunkMap TACS = player.getLevel().getChunkSource().chunkMap;
            ((ChunkReloader) TACS).reloadChunks(player, true);
        }
    }
}
