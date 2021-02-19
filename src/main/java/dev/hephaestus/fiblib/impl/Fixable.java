package dev.hephaestus.fiblib.impl;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.WorldChunk;

public interface Fixable {
    void fix(ServerPlayerEntity player);
}
