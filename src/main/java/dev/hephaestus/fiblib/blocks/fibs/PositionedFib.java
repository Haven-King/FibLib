package dev.hephaestus.fiblib.blocks.fibs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public abstract class PositionedFib extends BlockFib {
    public PositionedFib(BlockState input, BlockState output) {
        super(input, output);
    }

    public PositionedFib(Block input, Block output) {
        super(input, output);
    }

    public BlockState getOutput(BlockState inputState, ServerPlayerEntity player, BlockPos pos) {
        if (pos == null || player == null) return inputState;
        return condition(player, pos) ? super.getOutput(inputState) : inputState;
    }

    protected abstract boolean condition(ServerPlayerEntity player, BlockPos pos);
}
