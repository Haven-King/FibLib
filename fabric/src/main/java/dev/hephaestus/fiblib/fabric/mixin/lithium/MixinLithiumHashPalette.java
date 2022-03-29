package dev.hephaestus.fiblib.fabric.mixin.lithium;

import dev.hephaestus.fiblib.fabric.Fixable;
import dev.hephaestus.fiblib.impl.LookupImpl;
import me.jellysquid.mods.lithium.common.world.chunk.LithiumHashPalette;
import net.minecraft.core.IdMapper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unchecked")
@Mixin(LithiumHashPalette.class)
public class MixinLithiumHashPalette<T> implements Fixable {
    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/IdMapper;getId(Ljava/lang/Object;)I"))
    public int toPacketRedir(IdMapper<T> idList, T object) {
        return idList.getId((T) LookupImpl.findState((BlockState) object, this.player));
    }

    @Redirect(method = "getSerializedSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/IdMapper;getId(Ljava/lang/Object;)I"))
    public int getPacketSizeRedir(IdMapper<T> idList, T object) {
        return idList.getId((T) LookupImpl.findState((BlockState) object, this.player));
    }

    private Player player;

    @Override
    public void fix(Player player) {
        this.player = player;
    }
}
