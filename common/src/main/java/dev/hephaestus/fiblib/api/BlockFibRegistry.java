package dev.hephaestus.fiblib.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.hephaestus.fiblib.FibLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public final class BlockFibRegistry {
    /**
     * Currently registered fibs
     */
    private static final Multimap<BlockState, BlockFib> BLOCK_FIBS = HashMultimap.create();

    /**
     * Permanently fibs
     */
    private static final Multimap<BlockState, BlockFib> STATIC_BLOCK_FIBS = HashMultimap.create();

    /**
     * Temporary fibs
     */
    private static final Map<ResourceLocation, BlockFib> DYNAMIC_BLOCK_FIBS = new HashMap<>();

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
        FibLib.log("Registering fib %s", id.toString());

        if (DYNAMIC_BLOCK_FIBS.containsKey(id)) {
            BlockFib old = DYNAMIC_BLOCK_FIBS.get(id);

            old.getInputs().forEach(state -> BLOCK_FIBS.get(state).removeIf(fib -> fib == old));
        }

        blockFib.getInputs().forEach(state -> BLOCK_FIBS.put(state, blockFib));
        DYNAMIC_BLOCK_FIBS.put(id, blockFib);
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
    public static Iterable<BlockFib> getAllForState(BlockState inputState) {
        return BLOCK_FIBS.get(inputState);
    }

    /**
     * Gets all block fibs registered.
     *
     * @return a (potentially empty) iterable of block fibs
     */
    public static Collection<Map.Entry<BlockState, BlockFib>> getAll() {
        return BLOCK_FIBS.entries();
    }

    /**
     * Gets the currently registered {@link BlockFib} for the given id.
     *
     * @param id the unique identifier to query
     * @return the registered block fib, if it exists
     */
    public static @Nullable BlockFib getDynamicById(ResourceLocation id) {
        return DYNAMIC_BLOCK_FIBS.get(id);
    }

    @ApiStatus.Internal
    public static void resetDynamicFibs() {
        FibLib.log("Resetting fib registry");
        BLOCK_FIBS.clear();
        BLOCK_FIBS.putAll(STATIC_BLOCK_FIBS);
    }

}
