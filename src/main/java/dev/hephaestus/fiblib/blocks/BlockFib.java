package dev.hephaestus.fiblib.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BlockFib implements Predicate<ServerPlayerEntity> {
    private final BlockState input;
    private final BlockState output;
    private final boolean soft;
    private final Predicate<ServerPlayerEntity> predicate;

    public BlockFib(BlockState input, BlockState output, @Nullable Predicate<ServerPlayerEntity> predicate, boolean soft) {
        this.input = input;
        this.output = output;
        this.predicate = predicate == null ? this::condition : predicate;
        this.soft = soft;
    }

    public BlockFib(Block input, Block output, @Nullable Predicate<ServerPlayerEntity> predicate, boolean soft) {
        this(input.getDefaultState(), output.getDefaultState(), predicate, soft);
    }

    public BlockFib(BlockState input, BlockState output, boolean soft) {
        this(input, output, null, soft);
    }

    public BlockFib(Block input, Block output, boolean soft) {
        this(input, output, null, soft);
    }

    public BlockFib(BlockState input, BlockState output, @Nullable Predicate<ServerPlayerEntity> predicate) {
        this(input, output, predicate, false);
    }

    public BlockFib(Block input, Block output, @Nullable Predicate<ServerPlayerEntity> predicate) {
        this(input.getDefaultState(), output.getDefaultState(), predicate, false);
    }

    public BlockFib(BlockState input, BlockState output) {
        this(input, output, null, false);
    }

    public BlockFib(Block input, Block output) {
        this(input, output, null, false);
    }

    public BlockState getOutput(BlockState inputState) {
        return inputState == this.input ? this.output : inputState;
    }

    public BlockState getOutput(BlockState inputState, @Nullable ServerPlayerEntity player) {
        return player != null && inputState == this.input && test(player) ? getOutput(inputState) : inputState;
    }

    public BlockState getInput() {
        return this.input;
    }

    protected boolean condition(ServerPlayerEntity player) { return true;}

    /**
     * @return does not fib on block update (i.e. player right click)
     */
	public boolean isSoft() {
	    return this.soft;
    }

    @Override
    public final boolean test(ServerPlayerEntity playerEntity) {
        return this.predicate.test(playerEntity);
    }
}
