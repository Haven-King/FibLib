package dev.hephaestus.fiblib.mixin.packets.chunkdata;

import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.IdList;
import net.minecraft.world.chunk.BiMapPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unchecked")
@Mixin(BiMapPalette.class)
public class MixinBiMapPalette<T> implements Fixable {
    ServerPlayerEntity player;
    @Redirect(method = "toPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IdList;getRawId(Ljava/lang/Object;)I"))
    public int toPacketRedir(IdList<T> idList, T object) {
        return idList.getRawId((T) BlockFibRegistry.getBlockState((BlockState) object, this.player));
    }

    @Redirect(method = "getPacketSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IdList;getRawId(Ljava/lang/Object;)I"))
    public int packetSizeRedir(IdList<T> idList, T object) {
        return idList.getRawId((T) BlockFibRegistry.getBlockState((BlockState) object, this.player));
    }

    @Override
    public void fix(ServerPlayerEntity player) {
        this.player = player;
    }
}
