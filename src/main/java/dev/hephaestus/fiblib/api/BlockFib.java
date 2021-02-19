package dev.hephaestus.fiblib.api;

import dev.hephaestus.fiblib.impl.BlockFibImpl;
import dev.hephaestus.fiblib.impl.BlockStateFib;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

public interface BlockFib {
    Iterable<BlockState> getInputs();
    BlockState getOutput(BlockState inputState, @Nullable ServerPlayerEntity playerEntity);

    /**
     * Registers a {@link BlockFib} that persists through datpack reloads and cannot be replaced.
     *
     * @param blockFib the block fib to register
     */
    static void register(BlockFib blockFib) {
        BlockFibRegistry.register(blockFib);
    }

    /**
     * Registers a {@link BlockFib} dynamically as part of a datapack.
     *
     * Use this method if you want your fib to be removed from the registry on datapack reload or if you want your fib
     * to be able to be overridden by using the same ID.
     *
     * @param id the unique identifier to register this fib under
     * @param blockFib the block fib to register
     */
    static void register(Identifier id, BlockFib blockFib) {
        BlockFibRegistry.register(id, blockFib);
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
