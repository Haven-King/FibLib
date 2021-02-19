package dev.hephaestus.fiblib.impl;

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
    private final ImmutableList<BlockState> inputs;

    public BlockFibImpl(Block inputBlock, BlockState outputState) {
        this.inputBlock = inputBlock;
        this.outputState = outputState;
        this.inputs = inputBlock.getStateManager().getStates();
    }

    public BlockFibImpl(Block inputBlock, Block outputBlock) {
        this(inputBlock, outputBlock.getDefaultState());
    }

    @Override
    public Iterable<BlockState> getInputs() {
        return this.inputs;
    }

    @Override
    public BlockState getOutput(BlockState inputState, @Nullable ServerPlayerEntity playerEntity) {
        return inputState.isOf(this.inputBlock) ? this.outputState : inputState;
    }

    public static class Conditional extends BlockFibImpl {
        private final Predicate<@Nullable ServerPlayerEntity> condition;

        public Conditional(Block inputBlock, Block outputBlock, Predicate<@Nullable ServerPlayerEntity> condition) {
            this(inputBlock, outputBlock.getDefaultState(), condition);
        }

        public Conditional(Block inputBlock, BlockState outputState, Predicate<@Nullable ServerPlayerEntity> condition) {
            super(inputBlock, outputState);
            this.condition = condition;
        }

        @Override
        public BlockState getOutput(BlockState inputState, @Nullable ServerPlayerEntity playerEntity) {
            return this.condition.test(playerEntity) ? super.getOutput(inputState, playerEntity) : inputState;
        }
    }
}
