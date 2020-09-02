package dev.hephaestus.fiblib.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;

public class BlockFib {
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

    public BlockState getOutput(BlockState inputState, ServerPlayerEntity player) {
        return player != null && inputState == this.input && condition(player) ? getOutput(inputState) : inputState;
    }


    public BlockState getInput() {
        return this.input;
    }

    protected boolean condition(ServerPlayerEntity player) { return true;}

    /**
     * @return does not fib on block update (i.e. player right click)
     */
	public boolean isSoft() {
	    return false;
    }
}
