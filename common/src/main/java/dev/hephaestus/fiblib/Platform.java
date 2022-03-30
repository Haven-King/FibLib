package dev.hephaestus.fiblib;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Contains platform-specific functionalities
 */
public class Platform {
    private static final ThreadLocal<Player> currentPlayer = new ThreadLocal<>();

    @ExpectPlatform
    public static void resendChunks(ServerPlayer player) { throw new AssertionError("Unimplemented in platform"); }

    public static Player getCurrentPlayer() {
        return currentPlayer.get();
    }

    public static void setCurrentPlayer(Player player) {
        currentPlayer.set(player);
    }
}