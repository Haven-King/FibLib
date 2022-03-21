package dev.hephaestus.fiblib.mixin.packets.chunkdata;

import dev.hephaestus.fiblib.impl.FibLog;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PalettedContainer.class)
public class MixinPalettedContainer<T> implements Fixable {
    @Shadow
    private Palette<T> palette;

    @Override
    public void fix(ServerPlayer player) {
        FibLog.debug("Fixing Palette %s for %s", palette.toString(), player.getName().getString());
        ((Fixable) palette).fix(player);
    }
}
