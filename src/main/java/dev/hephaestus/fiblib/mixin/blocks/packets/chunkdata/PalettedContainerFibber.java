package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import dev.hephaestus.fiblib.Fibber;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PalettedContainer.class)
public class PalettedContainerFibber<T> implements Fibber {
    @Shadow private Palette<T> palette;

    @Override
    public void fix(ServerPlayerEntity player) {
        Fibber.fix(palette, player);
    }
}
