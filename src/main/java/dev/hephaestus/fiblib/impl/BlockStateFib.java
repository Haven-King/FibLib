package dev.hephaestus.fiblib.impl;

import com.google.common.collect.ImmutableList;
import dev.hephaestus.fiblib.api.BlockFib;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

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
    public final Iterable<BlockState> getInputs() {
        return this.inputStates;
    }

    @Override
    public BlockState getOutput(BlockState inputState, @Nullable ServerPlayerEntity playerEntity) {
        return inputState == this.inputState ? outputState : inputState;
    }

    public static class Conditional extends BlockStateFib {
        private final Predicate<@Nullable ServerPlayerEntity> condition;

        public Conditional(BlockState inputState, BlockState outputState, boolean lenient, Predicate<@Nullable ServerPlayerEntity> condition) {
            super(inputState, outputState, lenient);
            this.condition = condition;
        }

        @Override
        public BlockState getOutput(BlockState inputState, @Nullable ServerPlayerEntity playerEntity) {
            return this.condition.test(playerEntity) ? super.getOutput(inputState, playerEntity) : inputState;
        }
    }
}
