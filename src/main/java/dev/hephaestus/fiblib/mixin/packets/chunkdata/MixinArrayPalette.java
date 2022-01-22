package dev.hephaestus.fiblib.mixin.packets.chunkdata;

import dev.hephaestus.fiblib.impl.Fixable;
import dev.hephaestus.fiblib.api.BlockFibRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.world.chunk.ArrayPalette;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unchecked")
@Mixin(ArrayPalette.class)
public class MixinArrayPalette<T> implements Fixable {
    @Mutable @Final @Shadow private final IdList<T> idList;
    public MixinArrayPalette(IdList<T> idList) {
        this.idList = idList;
    }

    @Redirect(method = "writePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IndexedIterable;getRawId(Ljava/lang/Object;)I"))
    public int toPacketRedir(IndexedIterable<T> instance, T object) {
        return idList.getRawId((T) BlockFibRegistry.getBlockState((BlockState) object, this.player));
    }

    @Redirect(method = "getPacketSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IndexedIterable;getRawId(Ljava/lang/Object;)I"))
    public int getPacketSizeRedir(IndexedIterable<T> instance, T object) {
        return idList.getRawId((T) BlockFibRegistry.getBlockState((BlockState) object, this.player));
    }

    private ServerPlayerEntity player;

    @Override
    public void fix(ServerPlayerEntity player) {
        this.player = player;
    }
}