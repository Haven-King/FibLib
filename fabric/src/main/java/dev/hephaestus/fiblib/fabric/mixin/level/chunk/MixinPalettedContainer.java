package dev.hephaestus.fiblib.fabric.mixin.level.chunk;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.fabric.Fixable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PalettedContainer.class)
public class MixinPalettedContainer<T> implements Fixable {
    @Shadow
    private Palette<T> palette;

    @Override
    public void fix(Player player) {
        FibLib.debug("Fixing contained Palette %s for %s", palette.toString(), player.getName().getString());
        ((Fixable) palette).fix(player);
    }
}
