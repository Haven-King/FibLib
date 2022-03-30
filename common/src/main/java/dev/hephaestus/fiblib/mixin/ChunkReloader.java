package dev.hephaestus.fiblib.mixin;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class)
public interface ChunkReloader {
    @Invoker("updatePlayerStatus")
    void reloadChunks(ServerPlayer playerEntity, boolean added);

    @Accessor("viewDistance")
    int getWatchDistance();
}
