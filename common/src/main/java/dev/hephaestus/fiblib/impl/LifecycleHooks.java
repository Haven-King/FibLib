package dev.hephaestus.fiblib.impl;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Platform;
import dev.hephaestus.fiblib.api.BlockFib;
import dev.hephaestus.fiblib.api.BlockFibRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;

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
        addTestFibs();
    }

    public static void handleTick(PlayerList playerList) {
        Iterator<Triple<BlockFib, BlockState, UUID>> it = LookupImpl.getAllKeys();

        if (!it.hasNext()) {
            FibLib.log("No players for handling server tick event");
        }

        while (it.hasNext()) {
            Triple<BlockFib, BlockState, UUID> key = it.next();
            ServerPlayer player = playerList.getPlayer(key.getRight());
            FibLib.log("Handling server tick event for %s", player.getName().getString());

            if (player.removed || player.hasDisconnected()) {
                FibLib.log("%s was removed from server", player.getName().getString());
                it.remove();
            } else {
                FibLib.log("Checking fib updates for %s", player.getName().getString());
                BlockState newState = key.getLeft().getOutput(key.getMiddle(), player);

                if (LookupImpl.upsertLookup(key, newState)) {
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
