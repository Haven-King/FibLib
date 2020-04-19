package dev.hephaestus.fiblib.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;

public interface BlockFib {
    BlockFib DEFAULT = (state, player) -> state;
    BlockState get(BlockState state, ServerPlayerEntity player);
}
