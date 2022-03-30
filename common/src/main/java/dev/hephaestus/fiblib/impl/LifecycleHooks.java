package dev.hephaestus.fiblib.impl;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Platform;
import dev.hephaestus.fiblib.api.BlockFib;
import dev.hephaestus.fiblib.api.BlockFibRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;

public class LifecycleHooks {
    protected static final Collection<ServerPlayer> updated = new HashSet<>();

    private static void addTestFibs() {
        FibLib.log("Adding test fibs");
        FibLib.fibCoalOre();
        FibLib.fibIronOre();
    }

    public static void handleInit() {
        FibLib.log("Initializing FibLib");
        BlockFibRegistry.resetDynamicFibs();
    }

    public static void handleTick(PlayerList playerList) {
        Iterator<Triple<BlockFib, BlockState, UUID>> it = LookupImpl.getAllKeys();

        while (it.hasNext()) {
            Triple<BlockFib, BlockState, UUID> key = it.next();
            UUID playerId = key.getRight();
            @Nullable ServerPlayer player = playerList.getPlayer(playerId);

            if (player == null || player.removed || player.hasDisconnected()) {
                FibLib.debug("%s was removed from server", player == null ? playerId : player.getName().getString());
                it.remove();
            } else {
                FibLib.debug("Checking fib updates for %s", player.getName().getString());
                BlockState newState = key.getLeft().getOutput(key.getMiddle(), player);

                if (LookupImpl.upsertLookup(key, newState)) {
                    FibLib.debug("Updated fib of %s for %s", newState.getBlock().getName().getString(), player.getName().getString());
                    updated.add(player);
                }
            }
        }

        if (!updated.isEmpty()) {
            for (ServerPlayer player : updated) {
                Platform.resendChunks(player);
            }

            updated.clear();
        }
    }

    public static void handleReloadResources() {
        FibLib.debug("Caught Reload Resources");
        BlockFibRegistry.resetDynamicFibs();
    }
}
