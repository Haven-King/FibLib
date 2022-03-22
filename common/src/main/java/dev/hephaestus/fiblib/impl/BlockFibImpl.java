package dev.hephaestus.fiblib.impl;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import dev.hephaestus.fiblib.api.BlockFib;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockFibImpl implements BlockFib {
    private final Block inputBlock;
    private final BlockState outputState;
    private final boolean lenient;
    private final boolean modifiesDrops;
    private final ImmutableList<BlockState> inputs;

    public BlockFibImpl(Block inputBlock, BlockState outputState, boolean lenient, boolean modifiesDrops) {
        this.inputBlock = inputBlock;
        this.outputState = outputState;
        this.inputs = inputBlock.getStateDefinition().getPossibleStates();
        this.lenient = lenient;
        this.modifiesDrops = modifiesDrops;
    }

    public BlockFibImpl(Block inputBlock, Block outputBlock, boolean lenient, boolean modifiesDrops) {
        this(inputBlock, outputBlock.defaultBlockState(), lenient,modifiesDrops);
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
    public BlockState getOutput(BlockState inputState, @Nullable ServerPlayer playerEntity) {
        return inputState.is(this.inputBlock) ? this.outputState : inputState;
    }

    public static class Conditional extends BlockFibImpl {
        private final Predicate<@Nullable ServerPlayer> condition;

        public Conditional(Block inputBlock, Block outputBlock, boolean lenient, boolean modifiesDrops, Predicate<@Nullable ServerPlayer> condition) {
            this(inputBlock, outputBlock.defaultBlockState(), lenient, modifiesDrops, condition);
        }

        public Conditional(Block inputBlock, BlockState outputState, boolean lenient, boolean modifiesDrops, Predicate<@Nullable ServerPlayer> condition) {
            super(inputBlock, outputState, lenient, modifiesDrops);
            this.condition = condition;
        }

        @Override
        public BlockState getOutput(BlockState inputState, @Nullable ServerPlayer playerEntity) {
            return this.condition.test(playerEntity) ? super.getOutput(inputState, playerEntity) : inputState;
        }
    }
}
