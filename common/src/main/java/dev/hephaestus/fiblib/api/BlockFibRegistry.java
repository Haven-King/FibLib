package dev.hephaestus.fiblib.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.hephaestus.fiblib.impl.FibLog;
import dev.hephaestus.fiblib.impl.LookupTable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockFibRegistry {
    private static final Multimap<BlockState, BlockFib> STATIC_BLOCK_FIBS = HashMultimap.create();
    private static final Multimap<BlockState, BlockFib> BLOCK_FIBS = HashMultimap.create();
    private static final Map<ResourceLocation, BlockFib> DYNAMIC_BLOCK_FIBS = new HashMap<>();

    static {
        FibLog.log("Initialized BlockFibRegistry");
    }

    private BlockFibRegistry() {
    }

    /**
     * Registers a {@link BlockFib} that persists through datapack reloads and cannot be replaced.
     *
     * @param blockFib the block fib to register
     */
    public static void register(BlockFib blockFib) {
        blockFib.getInputs().forEach(state -> STATIC_BLOCK_FIBS.put(state, blockFib));
        blockFib.getInputs().forEach(state -> BLOCK_FIBS.put(state, blockFib));
    }

    /**
     * Registers a {@link BlockFib} dynamically as part of a datapack.
     *
     * Use this method if you want your fib to be removed from the registry on datapack reload or if you want your fib
     * to be able to be overridden by using the same ID.
     *
     * @param id the unique identifier to register this fib under
     * @param blockFib the block fib to register
     */
    public static void register(ResourceLocation id, BlockFib blockFib) {
        if (DYNAMIC_BLOCK_FIBS.containsKey(id)) {
            BlockFib old = DYNAMIC_BLOCK_FIBS.get(id);

            old.getInputs().forEach(state -> BLOCK_FIBS.get(state).removeIf(fib -> fib == old));
        }

        blockFib.getInputs().forEach(state -> BLOCK_FIBS.put(state, blockFib));
        DYNAMIC_BLOCK_FIBS.put(id, blockFib);
    }

    /**
     * Returns the result of any fibs on a given  {@link BlockState} for the given player.
     *
     * @param inputState  the state of the block we're inquiring about. Note that because this is passed to a BlockFib,
     *                    other aspects of the state than the Block may be used in determining the output
     * @param playerEntity the player who we will be fibbing to
     * @return the result of the fib.
     */
    public static BlockState getBlockState(BlockState inputState, ServerPlayer playerEntity) {
        if (playerEntity != null && playerEntity.getServer() != null) {
            return ((LookupTable) playerEntity.getServer()).get(inputState, playerEntity);
        }

        return inputState;
    }

    /**
     * Returns the first fib applied to a given {@link BlockState} for the given player.
     *
     * @param inputState  the state of the block we're inquiring about. Note that because this is passed to a BlockFib,
     *                    other aspects of the state than the Block may be used in determining the output
     * @param playerEntity the player who we will be fibbing to
     * @return the resulting fib.
     */
    public static @Nullable BlockFib getBlockFib(BlockState inputState, ServerPlayer playerEntity) {
        for (BlockFib blockFib : BLOCK_FIBS.get(inputState)) {
            if (blockFib.getOutput(inputState, playerEntity) != inputState) {
                return blockFib;
            }
        }

        return null;
    }

    /**
     * @param inputState the state to inquire about
     * @return whether or not a fib is registered for the given state
     */
    public static boolean contains(BlockState inputState) {
        return BLOCK_FIBS.containsKey(inputState);
    }

    /**
     * Gets all block fibs registered for a given input state.
     *
     * @param inputState the state whose fibs we want
     * @return a (potentially empty) iterable of block fibs
     */
    public static Iterable<BlockFib> getBlockFibs(BlockState inputState) {
        return BLOCK_FIBS.get(inputState);
    }

    /**
     * Gets the currently registered {@link BlockFib} for the given id.
     *
     * @param id the unique identifier to query
     * @return the registered block fib, if it exists
     */
    public static @Nullable BlockFib getBlockFib(ResourceLocation id) {
        return DYNAMIC_BLOCK_FIBS.get(id);
    }

    @ApiStatus.Internal
    public static void reset() {
        FibLog.log("Resetting fib registry");
        BLOCK_FIBS.clear();
        BLOCK_FIBS.putAll(STATIC_BLOCK_FIBS);
    }

    public static BlockState getBlockStateLenient(BlockState state, ServerPlayer player) {
        for (BlockFib fib : BLOCK_FIBS.get(state)) {
            BlockState newState;

            if (!fib.isLenient() && ((newState = fib.getOutput(state, player)) != state)) {
                return newState;
            }
        }

        return state;
    }
}
