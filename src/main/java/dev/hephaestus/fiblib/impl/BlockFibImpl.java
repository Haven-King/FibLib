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
    private final boolean modifiesDrops;
    private final ImmutableList<BlockState> inputs;

    public BlockFibImpl(Block inputBlock, BlockState outputState, boolean lenient, boolean modifiesDrops) {
        this.inputBlock = inputBlock;
        this.outputState = outputState;
        this.inputs = inputBlock.getStateManager().getStates();
        this.lenient = lenient;
        this.modifiesDrops = modifiesDrops;
    }

    public BlockFibImpl(Block inputBlock, Block outputBlock, boolean lenient, boolean modifiesDrops) {
        this(inputBlock, outputBlock.getDefaultState(), lenient, modifiesDrops);
    }

    @Override
    public final boolean isLenient() {
        return this.lenient;
    }

    @Override
    public final boolean modifiesDrops() {
        return this.modifiesDrops;
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

        public Conditional(Block inputBlock, Block outputBlock, boolean lenient, boolean modifiesDrops, Predicate<@Nullable ServerPlayerEntity> condition) {
            this(inputBlock, outputBlock.getDefaultState(), lenient, modifiesDrops, condition);
        }

        public Conditional(Block inputBlock, BlockState outputState, boolean lenient, boolean modifiesDrops, Predicate<@Nullable ServerPlayerEntity> condition) {
            super(inputBlock, outputState, modifiesDrops, lenient);
            this.condition = condition;
        }

        @Override
        public BlockState getOutput(BlockState inputState, @Nullable ServerPlayerEntity playerEntity) {
            return this.condition.test(playerEntity) ? super.getOutput(inputState, playerEntity) : inputState;
        }
    }
}
