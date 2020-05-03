package dev.hephaestus.fiblib.blocks;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.blocks.fibs.PlayerFib;
import dev.hephaestus.fiblib.blocks.fibs.PositionedFib;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.HashMap;

public class LookupTable {
    private int updates = 0;
    private int version = 0;

    private final HashMap<ImmutablePair<PlayerFib, ServerPlayerEntity>, BlockState>
            playerLookupTable = new HashMap<>();

    private final HashMap<ImmutableTriple<PositionedFib, ServerPlayerEntity, BlockPos>, BlockState>
            positionLookupTable = new HashMap<>();

    private void put(ImmutablePair<PlayerFib, ServerPlayerEntity> key, BlockState input) {
        BlockState newState = key.left.getOutput(input, key.right);
        BlockState oldState = playerLookupTable.put(key, newState);
    }

    private void put(ImmutableTriple<PositionedFib, ServerPlayerEntity, BlockPos> key, BlockState input) {
        BlockState newState = key.left.getOutput(input, key.middle, key.right);
        BlockState oldState = positionLookupTable.put(key, newState);

        if (newState != oldState)
            key.middle.getServerWorld().getChunkManager().markForUpdate(key.right);
    }

    public BlockState get(PlayerFib fib, BlockState input, ServerPlayerEntity player) {
        if (input != fib.getInput()) return input;

        ImmutablePair<PlayerFib, ServerPlayerEntity> key = new ImmutablePair<>(fib, player);
        if (!playerLookupTable.containsKey(key)) {
            put(key, input);
        }

        return playerLookupTable.get(key);
    }

    public BlockState get(PositionedFib fib, BlockState input, ServerPlayerEntity player, BlockPos pos) {
        if (input != fib.getInput()) return input;

        ImmutableTriple<PositionedFib, ServerPlayerEntity, BlockPos> key = new ImmutableTriple<>(fib, player, pos);
        if (!positionLookupTable.containsKey(key)) {
            put(key, input);
        }

        return positionLookupTable.get(key);
    }

    public int getVersion() { return version; }

    public void update() {
        for (ImmutablePair<PlayerFib, ServerPlayerEntity> key :  playerLookupTable.keySet()) {
            BlockState newState = key.left.getOutput(key.left.getInput(), key.right);
            BlockState oldState = playerLookupTable.get(key);

            if (newState != oldState) {
                playerLookupTable.put(key, newState);
                FibLib.debug("Updating %s. Now: %s", oldState.getBlock().getTranslationKey(), newState.getBlock().getTranslationKey());
                ++updates;
            }
        }

        for (ImmutableTriple<PositionedFib, ServerPlayerEntity, BlockPos> key :  positionLookupTable.keySet()) {
            BlockState newState = key.left.getOutput(key.left.getInput(), key.middle, key.right);
            BlockState oldState = positionLookupTable.get(key);

            if (newState != oldState) {
                positionLookupTable.put(key, newState);
                FibLib.debug("Updating %s. Now: %s", oldState.getBlock().getTranslationKey(), newState.getBlock().getTranslationKey());
                ++updates;
            }
        }

        if (updates > 0) {
            FibLib.debug("Updating %d items. New version: %d", updates, ++version);
            updates = 0;
        }
    }

}
