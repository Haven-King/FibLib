package dev.hephaestus.fiblib.blocks.fibs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public abstract class BlockFib {
    private final BlockState input;
    private final BlockState output;


    public BlockFib(BlockState input, BlockState output) {
        this.input = input;
        this.output = output;
    }

    public BlockFib(Block input, Block output) {
        this.input = input.getDefaultState();
        this.output = output.getDefaultState();
    }


    public BlockState getOutput(BlockState inputState) {
        return inputState == this.input ? this.output : inputState;
    }

    public BlockState getInput() {
        return this.input;
    }
}

