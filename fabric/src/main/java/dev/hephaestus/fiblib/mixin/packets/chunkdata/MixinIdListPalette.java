package dev.hephaestus.fiblib.mixin.packets.chunkdata;

import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.core.IdMapper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.GlobalPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unchecked")
@Mixin(GlobalPalette.class)
public class MixinIdListPalette<T> implements Fixable {
    @Redirect(method = "idFor", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/IdMapper;getId(Ljava/lang/Object;)I"))
    public int getIndexRedir(IdMapper<T> idList, T object) {
        return idList.getId((T) BlockFibRegistry.getBlockState((BlockState) object, this.player));
    }

    private ServerPlayer player;

    @Override
    public void fix(ServerPlayer player) {
        this.player = player;
    }
}