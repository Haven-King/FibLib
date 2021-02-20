package dev.hephaestus.fiblib.impl;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import dev.hephaestus.fiblib.api.BlockFib;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BlockFibImpl implements BlockFib {
    private final Block inputBlock;
    private final BlockState outputState;
    private final boolean lenient;
    private final ImmutableList<BlockState> inputs;

    public BlockFibImpl(Block inputBlock, BlockState outputState, boolean lenient) {
        this.inputBlock = inputBlock;
        this.outputState = outputState;
        this.inputs = inputBlock.getStateManager().getStates();
        this.lenient = lenient;
    }

    public BlockFibImpl(Block inputBlock, Block outputBlock, boolean lenient) {
        this(inputBlock, outputBlock.getDefaultState(), lenient);
    }

    @Override
    public final boolean isLenient() {
        return this.lenient;
    }

    @Override
    public final ImmutableCollection<BlockState> getInputs() {
        return this.inputs;
    }

    @Override
    public BlockState getOutput(BlockState inputState, @Nullable ServerPlayerEntity playerEntity) {
        return inputState.isOf(this.inputBlock) ? this.outputState : inputState;
    }

    public static class Conditional extends BlockFibImpl {
        private final Predicate<@Nullable ServerPlayerEntity> condition;

        public Conditional(Block inputBlock, Block outputBlock, boolean lenient, Predicate<@Nullable ServerPlayerEntity> condition) {
            this(inputBlock, outputBlock.getDefaultState(), lenient, condition);
        }

        public Conditional(Block inputBlock, BlockState outputState, boolean lenient, Predicate<@Nullable ServerPlayerEntity> condition) {
            super(inputBlock, outputState, lenient);
            this.condition = condition;
        }

        @Override
        public BlockState getOutput(BlockState inputState, @Nullable ServerPlayerEntity playerEntity) {
            return this.condition.test(playerEntity) ? super.getOutput(inputState, playerEntity) : inputState;
        }
    }
}
