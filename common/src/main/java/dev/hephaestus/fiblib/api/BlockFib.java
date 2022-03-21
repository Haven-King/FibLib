package dev.hephaestus.fiblib.api;

import com.google.common.collect.ImmutableCollection;
import dev.hephaestus.fiblib.impl.BlockFibImpl;
import dev.hephaestus.fiblib.impl.BlockStateFib;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public interface BlockFib {
    /**
     * Whether or not this BlockFib is lenient.
     *
     * If a fib is lenient, it will not be applied when either a {@link PlayerActionResponseS2CPacket} or a
     * {@link BlockUpdateS2CPacket} is sent to the client. This means if the block is updated, or the player right
     * clicks or begins mining the block, it will be revealed. This can be useful for block fibs that need to hide
     * blocks at long ranges (namely: anti x-ray).
     */
    boolean isLenient();

    /**
     * Gets the list of states this block fib applies to.
     *
     * This list should not change throughout a fibs life.
     */
    ImmutableCollection<BlockState> getInputs();

    /**
     * Gets the result of this fib when applied to the given state for the given player.
     *
     * BlockFibs are expected to return the origin input state if they don't wish to modify it.
     *
     * @param inputState the state to potentially fib
     * @param playerEntity the player the state will be sent to
     * @return the new fibbed state, or the original state
     */
    BlockState getOutput(BlockState inputState, ServerPlayerEntity playerEntity);

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
        private final BiFunction<@Nullable Predicate<ServerPlayerEntity>, Boolean, BlockFib> constructor;
        private Predicate<ServerPlayerEntity> condition = null;
        private boolean lenient = false;

        public Builder(Block inputBlock, Block outputBlock) {
            this.constructor = (condition, lenient) -> condition == null
                    ? new BlockFibImpl(inputBlock, outputBlock, lenient)
                    : new BlockFibImpl.Conditional(inputBlock, outputBlock, lenient, condition);
        }

        public Builder(BlockState inputState, BlockState outputState) {
            this.constructor = (condition, lenient) -> condition == null
                    ? new BlockStateFib(inputState, outputState, lenient)
                    : new BlockStateFib.Conditional(inputState, outputState, lenient, condition);
        }

        public Builder(Block inputBlock, BlockState outputState) {
            this.constructor = (condition, lenient) -> condition == null
                    ? new BlockFibImpl(inputBlock, outputState, lenient)
                    : new BlockFibImpl.Conditional(inputBlock, outputState, lenient, condition);
        }

        public Builder(BlockState inputState, Block outputBlock) {
            this.constructor = (condition, lenient) -> condition == null
                    ? new BlockStateFib(inputState, outputBlock.getDefaultState(), lenient)
                    : new BlockStateFib.Conditional(inputState, outputBlock.getDefaultState(), lenient, condition);
        }

        public Builder withCondition(Predicate<ServerPlayerEntity> condition) {
            this.condition = condition;
            return this;
        }

        public Builder lenient() {
            this.lenient = true;
            return this;
        }

        public BlockFib build() {
            return this.constructor.apply(this.condition, this.lenient);
        }
    }
}
