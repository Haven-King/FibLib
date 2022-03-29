package dev.hephaestus.fiblib.fabric.mixin.level.chunk;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.fabric.Fixable;
import dev.hephaestus.fiblib.impl.LookupImpl;
import net.minecraft.core.IdMapper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.GlobalPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unchecked")
@Mixin(GlobalPalette.class)
public class MixinGlobalPalette<T> implements Fixable {
    @Redirect(method = "idFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/IdMapper;getId(Ljava/lang/Object;)I"))
    public int getIndexRedir(IdMapper<T> idList, T object) {
        FibLib.debug("Fixing GlobalPalette block %s before writing for %s", ((BlockState) object).getBlock().getName().getString(), player.getName().getString());
        return idList.getId((T) LookupImpl.findState((BlockState) object, this.player));
    }

    private Player player;

    @Override
    public void fix(Player player) {
        this.player = player;
    }
}
