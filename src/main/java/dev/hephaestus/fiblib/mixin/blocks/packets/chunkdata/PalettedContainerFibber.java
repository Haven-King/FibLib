package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import dev.hephaestus.fiblib.blocks.Fixable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PalettedContainer.class)
public class PalettedContainerFibber<T> implements Fixable {
    @Shadow private Palette<T> palette;

    @Override
    public void fix(WorldChunk chunk, int includedSectionsMask, ServerPlayerEntity player) {
        Fixable.fix(palette, player);
    }
}
