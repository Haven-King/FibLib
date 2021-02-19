package dev.hephaestus.fiblib.api;

import dev.hephaestus.fiblib.impl.BlockFibImpl;
import dev.hephaestus.fiblib.impl.BlockStateFib;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

public interface BlockFib {
    Iterable<BlockState> getInputs();
    BlockState getOutput(BlockState inputState, @Nullable ServerPlayerEntity playerEntity);

    static Builder builder(Block inputBlock, Block outputBlock) {
        return new Builder(inputBlock, outputBlock);
    }

    static Builder builder(BlockState inputState, BlockState outputState) {
        return new Builder(inputState, outputState);
    }

    static Builder builder(Block inputBlock, BlockState outputState) {
        return new Builder(inputBlock, outputState);
    }

    static Builder builder(BlockState inputState, Block outputBlock) {
        return new Builder(inputState, outputBlock);
    }

    class Builder {
        private final Function<@Nullable Predicate<@Nullable ServerPlayerEntity>, BlockFib> constructor;
        private Predicate<@Nullable ServerPlayerEntity> condition = null;

        public Builder(Block inputBlock, Block outputBlock) {
            this.constructor = condition -> condition == null
                    ? new BlockFibImpl(inputBlock, outputBlock)
                    : new BlockFibImpl.Conditional(inputBlock, outputBlock, condition);
        }

        public Builder(BlockState inputState, BlockState outputState) {
            this.constructor = condition -> condition == null
                    ? new BlockStateFib(inputState, outputState)
                    : new BlockStateFib.Conditional(inputState, outputState, condition);
        }

        public Builder(Block inputBlock, BlockState outputState) {
            this.constructor = condition -> condition == null
                    ? new BlockFibImpl(inputBlock, outputState)
                    : new BlockFibImpl.Conditional(inputBlock, outputState, condition);
        }

        public Builder(BlockState inputState, Block outputBlock) {
            this.constructor = condition -> condition == null
                    ? new BlockStateFib(inputState, outputBlock.getDefaultState())
                    : new BlockStateFib.Conditional(inputState, outputBlock.getDefaultState(), condition);
        }

        public Builder withCondition(Predicate<@Nullable ServerPlayerEntity> condition) {
            this.condition = condition;
            return this;
        }

        public BlockFib build() {
            return this.constructor.apply(this.condition);
        }
    }
}
