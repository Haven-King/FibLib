package dev.hephaestus.fiblib.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface ChunkReloader {
    @Invoker("handlePlayerAddedOrRemoved")
    void reloadChunks(ServerPlayerEntity playerEntity, boolean added);
}
