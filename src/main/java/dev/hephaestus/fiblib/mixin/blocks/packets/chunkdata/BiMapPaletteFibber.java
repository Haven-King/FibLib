package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.blocks.Fixable;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.IdList;
import net.minecraft.world.chunk.BiMapPalette;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unchecked")
@Mixin(BiMapPalette.class)
public class BiMapPaletteFibber<T> implements Fixable {
    ServerPlayerEntity player;
    @Redirect(method = "toPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IdList;getRawId(Ljava/lang/Object;)I"))
    public int toPacketRedir(IdList<T> idList, T object) {
        return idList.getRawId((T) FibLib.Blocks.get((BlockState) object, this.player, false));
    }

    @Redirect(method = "getPacketSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IdList;getRawId(Ljava/lang/Object;)I"))
    public int packetSizeRedir(IdList<T> idList, T object) {
        return idList.getRawId((T) FibLib.Blocks.get((BlockState) object, this.player, false));
    }

    @Override
    public void fix(WorldChunk chunk, int includedSectionsMask, ServerPlayerEntity player) {
        this.player = player;
    }
}
