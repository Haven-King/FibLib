package dev.hephaestus.fiblib.impl;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;

public interface LookupTable {
    BlockState get(BlockState blockState, ServerPlayerEntity playerEntity);
}
