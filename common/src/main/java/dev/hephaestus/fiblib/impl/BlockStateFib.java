package dev.hephaestus.fiblib.impl;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import dev.hephaestus.fiblib.api.BlockFib;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateFib implements BlockFib {
    private final BlockState inputState;
    private final BlockState outputState;
    private final ImmutableList<BlockState> inputStates;
    private final boolean lenient;

    public BlockStateFib(BlockState inputState, BlockState outputState, boolean lenient) {
        this.inputState = inputState;
        this.outputState = outputState;
        this.inputStates = ImmutableList.of(inputState);
        this.lenient = lenient;
    }

    @Override
    public final boolean isLenient() {
        return this.lenient;
    }

    @Override
    public final ImmutableCollection<BlockState> getInputs() {
        return this.inputStates;
    }

    @Override
    public BlockState getOutput(BlockState inputState, @Nullable ServerPlayer playerEntity) {
        return inputState == this.inputState ? outputState : inputState;
    }

    public static class Conditional extends BlockStateFib {
        private final Predicate<@Nullable ServerPlayer> condition;

        public Conditional(BlockState inputState, BlockState outputState, boolean lenient, Predicate<@Nullable ServerPlayer> condition) {
            super(inputState, outputState, lenient);
            this.condition = condition;
        }

        @Override
        public BlockState getOutput(BlockState inputState, @Nullable ServerPlayer playerEntity) {
            return this.condition.test(playerEntity) ? super.getOutput(inputState, playerEntity) : inputState;
        }
    }
}
