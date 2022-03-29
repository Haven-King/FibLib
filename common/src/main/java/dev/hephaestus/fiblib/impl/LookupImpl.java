package dev.hephaestus.fiblib.impl;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.api.BlockFib;
import dev.hephaestus.fiblib.api.BlockFibRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class LookupImpl {

    private static final HashMap<Triple<BlockFib, BlockState, UUID>, BlockState> lookupTable = new HashMap<>();

    @ApiStatus.Internal
    public static Iterator<Triple<BlockFib, BlockState, UUID>> getAllKeys() {
        return lookupTable.keySet().iterator();
    }

    @ApiStatus.Internal
    public static boolean upsertLookup(Triple<BlockFib, BlockState, UUID> key, BlockState expectedState) {
        BlockState savedState = lookupTable.get(key);
        if (expectedState != savedState) {
            lookupTable.put(key, expectedState);
            return true;
        }

        return false;
    }

    /**
     * Returns the first fib applied to a given {@link BlockState} for the given player.
     *
     * @param inputState     the state of the block we're inquiring about. Note that because this is passed to a BlockFib,
     *                       other aspects of the state than the Block may be used in determining the output
     * @param player         the player who we will be fibbing to
     * @param includeLenient Whether to include lenient fibs in the search
     * @return the resulting fib.
     */
    public static @Nullable BlockFib find(BlockState inputState, Player player, boolean includeLenient) {
        for (BlockFib fib : BlockFibRegistry.getAllForState(inputState)) {
            boolean maybeLenient = !fib.isLenient() || includeLenient;
            BlockState newState = fib.getOutput(inputState, player);

            if (maybeLenient && (newState != inputState)) {
                return fib;
            }
        }

        return null;
    }

    public static @Nullable BlockFib find(BlockState inputState, Player player) {
        return find(inputState, player, true);
    }

    /**
     * Returns the appropriate {@link BlockState} for the given player.
     *
     * @param blockState     the state of the block we're inquiring about. Note that because this is passed to a BlockFib,
     *                       other aspects of the state than the Block may be used in determining the output
     * @param player         the player who we will be fibbing to
     * @param includeLenient Whether to include lenient fibs in the search
     * @param cacheForLater  Whether to cache looked up fib state response
     * @return the resulting blockState.
     */
    public static BlockState findState(BlockState blockState, @Nullable Player player, boolean includeLenient, boolean cacheForLater) {
        FibLib.debug("Getting replacement state for %s", blockState.getBlock().getName().getString());

        if (player == null) {
            return blockState;
        }

        @Nullable BlockFib fib = find(blockState, player, includeLenient);

        if (fib == null) return blockState;

        ImmutableTriple<BlockFib, BlockState, UUID> key = ImmutableTriple.of(fib, blockState, player.getUUID());

        if (lookupTable.containsKey(key)) {
            return lookupTable.get(key);
        }

        BlockState newState = fib.getOutput(blockState, player);
        if (!cacheForLater) {
            return newState;
        }

        if (!lookupTable.containsKey(key)) {
            FibLib.log("Saving replacement for %s for faster lookup", blockState.getBlock().getName().getString());

            lookupTable.put(key, key.left.getOutput(blockState, player));
        }

        return lookupTable.get(key);
    }

    public static BlockState findState(BlockState blockState, @Nullable Player player, boolean includeLenient) {
        return findState(blockState, player, includeLenient, true);
    }

    public static BlockState findState(BlockState blockState, @Nullable Player player) {
        return findState(blockState, player, true);
    }

    /**
     * Returns the appropriate {@link BlockState} for the given player.
     *
     * @param blockState     the state of the block we're inquiring about. Note that because this is passed to a BlockFib,
     *                       other aspects of the state than the Block may be used in determining the output
     * @param player         the player who we will be fibbing to
     * @return the resulting blockState.
     */
    public static BlockState findDropsState(BlockState blockState, @Nullable Player player) {
        FibLib.debug("Getting replacement for %s", blockState.getBlock().getName().getString());

        if (player == null) {
            return blockState;
        }

        @Nullable BlockFib fib = find(blockState, player, false);

        if (fib == null || !fib.modifiesDrops()) return blockState;

        return LookupImpl.findState(blockState, player, false, true);
    }
}
