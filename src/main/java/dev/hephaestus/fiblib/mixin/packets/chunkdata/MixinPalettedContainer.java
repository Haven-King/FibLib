package dev.hephaestus.fiblib.mixin.packets.chunkdata;

import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PalettedContainer.class)
public class MixinPalettedContainer<T> implements Fixable {

    @Shadow private volatile PalettedContainer.Data<T> data;

    @Override
    public void fix(ServerPlayerEntity player) {
        ((Fixable) data.palette()).fix(player);
    }
}
