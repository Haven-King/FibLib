package dev.hephaestus.fiblib.fabric;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Platform;
import dev.hephaestus.fiblib.mixin.ChunkReloader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings("unused")
public class PlatformImpl extends Platform {
    public static void resendChunks(ServerPlayer player) {
        if (FabricLoader.getInstance().isModLoaded("immersive_portals")) {
            ImmersivePortalsChunkManager.resendChunks(player);
        } else {
            resendChunksVanilla(player);
        }
    }
}
