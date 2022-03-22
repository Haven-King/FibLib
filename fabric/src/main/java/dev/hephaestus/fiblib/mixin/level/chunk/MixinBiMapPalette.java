package dev.hephaestus.fiblib.mixin.level.chunk;

import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.impl.FibLog;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.core.IdMapper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.HashMapPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unchecked")
@Mixin(HashMapPalette.class)
public class MixinBiMapPalette<T> implements Fixable {
    ServerPlayer player;

    @Redirect(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/IdMapper;getId(Ljava/lang/Object;)I"))
    public int toPacketRedir(IdMapper<T> idList, T object) {
        FibLog.debug("Fixing HashMapPalette block %s before writing for %s", ((BlockState) object).getBlock().getName().getString(), player.getName().getString());
        return idList.getId((T) BlockFibRegistry.getBlockState((BlockState) object, this.player));
    }

    @Redirect(method = "getSerializedSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/IdMapper;getId(Ljava/lang/Object;)I"))
    public int packetSizeRedir(IdMapper<T> idList, T object) {
        return idList.getId((T) BlockFibRegistry.getBlockState((BlockState) object, this.player));
    }

    @Override
    public void fix(ServerPlayer player) {
        this.player = player;
    }
}
