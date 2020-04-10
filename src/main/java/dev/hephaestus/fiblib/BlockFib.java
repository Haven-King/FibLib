package dev.hephaestus.fiblib;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;

public interface BlockFib {
    BlockFib DEFAULT = new BlockFib() {
        @Override
        public BlockState get(BlockState state, ServerPlayerEntity player) {
            return state;
        }
    };

    BlockState get(BlockState state, ServerPlayerEntity player);
}
