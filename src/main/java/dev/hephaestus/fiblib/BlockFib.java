package dev.hephaestus.fiblib;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;

public interface BlockFib {
    BlockFib DEFAULT = (state, player) -> state;

    /**
     * This is the function to override when making a custom Fib.
     * @param state the state we will be looking at when deciding what to tell the player
     * @param player the player we are fibbing to
     * @return the BlockState that we will tell the player exists here
     */
    BlockState get(BlockState state, ServerPlayerEntity player);
}
