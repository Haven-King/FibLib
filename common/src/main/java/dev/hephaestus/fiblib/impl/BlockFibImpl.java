package dev.hephaestus.fiblib.impl;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import dev.hephaestus.fiblib.api.BlockFib;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class BlockFibImpl implements BlockFib {
    private final Block inputBlock;
    private final ImmutableList<BlockState> inputs;
    @Nullable
    private final BlockState inputState;

    private final BlockState outputState;

    private final boolean lenient;
    private final boolean modifiesDrops;

    public BlockFibImpl(Block inputBlock, BlockState outputState, boolean lenient, boolean modifiesDrops) {
        this.inputBlock = inputBlock;
        this.inputs = inputBlock.getStateDefinition().getPossibleStates();
        this.inputState = null;

        this.outputState = outputState;
        this.lenient = lenient;
        this.modifiesDrops = modifiesDrops;
    }

    public BlockFibImpl(Block inputBlock, Block outputBlock, boolean lenient, boolean modifiesDrops) {
        this(inputBlock, outputBlock.defaultBlockState(), lenient, modifiesDrops);
    }

    public BlockFibImpl(BlockState inputState, BlockState outputState, boolean lenient, boolean modifiesDrops) {
        this.inputBlock = inputState.getBlock();
        this.inputs = ImmutableList.of(inputState);
        this.inputState = inputState;

        this.outputState = outputState;
        this.lenient = lenient;
        this.modifiesDrops = modifiesDrops;
    }

    @SuppressWarnings("unused")
    public BlockFibImpl(BlockState inputState, Block outputBlock, boolean lenient, boolean modifiesDrops) {
        this(inputState, outputBlock.defaultBlockState(), lenient, modifiesDrops);
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

    public BlockState getOutput(BlockState inputState) {
        if (this.inputState != null) {
            return inputState == this.inputState ? outputState : inputState;
        }

        return inputState.is(this.inputBlock) ? outputState : inputState;
    }

    @Override
    public BlockState getOutput(BlockState inputState, @Nullable Player playerEntity) {
        return this.getOutput(inputState);
    }

    public static class Conditional extends BlockFibImpl {
        private final Predicate<@Nullable Player> condition;

        public Conditional(Block inputBlock, BlockState outputState, boolean lenient, boolean modifiesDrops, Predicate<@Nullable Player> condition) {
            super(inputBlock, outputState, lenient, modifiesDrops);
            this.condition = condition;
        }

        public Conditional(Block inputBlock, Block outputBlock, boolean lenient, boolean modifiesDrops, Predicate<@Nullable Player> condition) {
            this(inputBlock, outputBlock.defaultBlockState(), lenient, modifiesDrops, condition);
        }

        public Conditional(BlockState inputState, BlockState outputState, boolean lenient, boolean modifiesDrops, Predicate<@Nullable Player> condition) {
            super(inputState, outputState, lenient, modifiesDrops);
            this.condition = condition;
        }

        @SuppressWarnings("unused")
        public Conditional(BlockState inputState, Block outputBlock, boolean lenient, boolean modifiesDrops, Predicate<@Nullable Player> condition) {
            this(inputState, outputBlock.defaultBlockState(), lenient, modifiesDrops, condition);
        }

        @Override
        public BlockState getOutput(BlockState inputState, @Nullable Player playerEntity) {
            return this.condition.test(playerEntity) ? super.getOutput(inputState) : inputState;
        }
    }
}
