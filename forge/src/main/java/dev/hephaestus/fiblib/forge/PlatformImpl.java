package dev.hephaestus.fiblib.forge;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Platform;
import dev.hephaestus.fiblib.forge.network.ServerPacketHandler;
import net.minecraft.server.level.ServerPlayer;

public class PlatformImpl extends Platform {
    public static void resendChunks(ServerPlayer player) {
        FibLib.log("Trigger reload chunk");
        ServerPacketHandler.getInstance().sendReloadPacket(player);
    }
}
