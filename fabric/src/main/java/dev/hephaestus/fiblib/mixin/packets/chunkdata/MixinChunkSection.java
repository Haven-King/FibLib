package dev.hephaestus.fiblib.mixin.packets.chunkdata;

import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkSection.class)
public class MixinChunkSection implements Fixable {
    @Mutable @Final @Shadow private PalettedContainer<BlockState> container;

    @Override
    public void fix(ServerPlayerEntity player) {
        ((Fixable) container).fix(player);
    }
}
