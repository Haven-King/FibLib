package dev.hephaestus.fiblib.api;

import com.google.common.collect.ImmutableCollection;
import dev.hephaestus.fiblib.impl.BlockFibImpl;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

public interface BlockFib {
    /**
     * Whether or not this BlockFib is lenient.
     * <p>
     * If a fib is lenient, it will not be applied when either a {@link ClientboundBlockBreakAckPacket} or a
     * {@link ClientboundBlockUpdatePacket} is sent to the client. This means if the block is updated, or the player right
     * clicks or begins mining the block, it will be revealed. This can be useful for block fibs that need to hide
     * blocks at long ranges (namely: anti x-ray).
     */
    boolean isLenient();

    /**
     * Whether this BlockFib modifies the drops of fibbed states.
     *
     * <p>
     * If this method returns {@code true}, and a player mines state A fibbed as state B,
     * the loot of state B will be dropped instead of the default state A loot.
     *
     * @return {@code true} if this BlockFib should modify the drops of fibbed states
     */
    boolean modifiesDrops();

    /**
     * Gets the list of states this block fib applies to.
     * <p>
     * This list should not change throughout a fibs life.
     */
    ImmutableCollection<BlockState> getInputs();

    /**
     * Gets the result of this fib when applied to the given state for the given player.
     * <p>
     * BlockFibs are expected to return the origin input state if they don't wish to modify it.
     *
     * @param inputState   the state to potentially fib
     * @param playerEntity the player the state will be sent to
     * @return the new fibbed state, or the original state
     */
    BlockState getOutput(BlockState inputState, Player playerEntity);

    static Builder builder(Block inputBlock, Block outputBlock) {
        return new Builder(inputBlock, outputBlock);
    }

    @SuppressWarnings("unused")
    static Builder builder(BlockState inputState, BlockState outputState) {
        return new Builder(inputState, outputState);
    }

    @SuppressWarnings("unused")
    static Builder builder(Block inputBlock, BlockState outputState) {
        return new Builder(inputBlock, outputState);
    }

    @SuppressWarnings("unused")
    static Builder builder(BlockState inputState, Block outputBlock) {
        return new Builder(inputState, outputBlock);
    }

    @SuppressWarnings("unused")
    class Builder {
        private final Function<@Nullable Predicate<Player>, BlockFib> constructor;
        private Predicate<Player> condition = null;
        private boolean lenient = false;
        private boolean modifiesDrops = false;

        public Builder(Block inputBlock, Block outputBlock) {
            this.constructor = (condition) -> condition == null
                    ? new BlockFibImpl(inputBlock, outputBlock, lenient, modifiesDrops)
                    : new BlockFibImpl.Conditional(inputBlock, outputBlock, lenient, modifiesDrops, condition);
        }

        public Builder(BlockState inputState, BlockState outputState) {
            this.constructor = (condition) -> condition == null
                    ? new BlockFibImpl(inputState, outputState, lenient, modifiesDrops)
                    : new BlockFibImpl.Conditional(inputState, outputState, lenient, modifiesDrops, condition);
        }

        public Builder(Block inputBlock, BlockState outputState) {
            this.constructor = (condition) -> condition == null
                    ? new BlockFibImpl(inputBlock, outputState, lenient, modifiesDrops)
                    : new BlockFibImpl.Conditional(inputBlock, outputState, lenient, modifiesDrops, condition);
        }

        public Builder(BlockState inputState, Block outputBlock) {
            this.constructor = (condition) -> condition == null
                    ? new BlockFibImpl(inputState, outputBlock.defaultBlockState(), lenient, modifiesDrops)
                    : new BlockFibImpl.Conditional(inputState, outputBlock.defaultBlockState(), lenient, modifiesDrops, condition);
        }

        public Builder withCondition(Predicate<Player> condition) {
            this.condition = condition;
            return this;
        }

        public Builder lenient() {
            this.lenient = true;
            return this;
        }

        public Builder modifiesDrops() {
            this.modifiesDrops = true;
            return this;
        }

        public BlockFib build() {
            return this.constructor.apply(this.condition);
        }
    }
}
