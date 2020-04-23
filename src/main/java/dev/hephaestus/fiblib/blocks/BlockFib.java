package dev.hephaestus.fiblib.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface BlockFib {
    BlockFib DEFAULT = (state, player, pos) -> state;
    BlockState get(BlockState state, ServerPlayerEntity player, BlockPos pos);
}
