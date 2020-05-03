package dev.hephaestus.fiblib.blocks.fibs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class PlayerFib extends BlockFib {
    // These two are the ones you should use
    public PlayerFib(BlockState input, BlockState output) {
        super(input, output);
    }

    public PlayerFib(Block input, Block output) {
        super(input, output);
    }

    public BlockState getOutput(BlockState inputState, ServerPlayerEntity player) {
        if (player == null) return inputState;
        return condition(player) ? super.getOutput(inputState) : inputState;
    }

    protected abstract boolean condition(ServerPlayerEntity player);
}
