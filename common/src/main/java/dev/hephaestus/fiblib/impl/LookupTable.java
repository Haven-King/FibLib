package dev.hephaestus.fiblib.impl;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public interface LookupTable {
    BlockState get(BlockState blockState, ServerPlayer playerEntity);
}
