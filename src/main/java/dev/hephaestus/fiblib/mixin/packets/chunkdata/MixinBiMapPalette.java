package dev.hephaestus.fiblib.mixin.packets.chunkdata;

import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.world.chunk.BiMapPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unchecked")
@Mixin(BiMapPalette.class)
public class MixinBiMapPalette<T> implements Fixable {
    ServerPlayerEntity player;
    @Redirect(method = "writePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IndexedIterable;getRawId(Ljava/lang/Object;)I"))
    public int toPacketRedir(IndexedIterable<T> idList, T object) {
        if(object instanceof BlockState) {
            return idList.getRawId((T) BlockFibRegistry.getBlockState((BlockState) object, this.player));
        }

        return idList.getRawId(object);
    }

    @Redirect(method = "getPacketSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IndexedIterable;getRawId(Ljava/lang/Object;)I"))
    public int packetSizeRedir(IndexedIterable<T> idList, T object) {
        if(object instanceof BlockState) {
            return idList.getRawId((T) BlockFibRegistry.getBlockState((BlockState) object, this.player));
        }

        return idList.getRawId(object);
    }

    @Override
    public void fix(ServerPlayerEntity player) {
        this.player = player;
    }
}
