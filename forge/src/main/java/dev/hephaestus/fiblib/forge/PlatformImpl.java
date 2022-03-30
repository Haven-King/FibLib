package dev.hephaestus.fiblib.forge;

import dev.hephaestus.fiblib.Platform;
import net.minecraft.server.level.ServerPlayer;

public class PlatformImpl extends Platform {
    public static void resendChunks(ServerPlayer player) {
        resendChunksVanilla(player);
    }
}
