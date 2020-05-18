package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Fibber;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.IdList;
import net.minecraft.world.chunk.ArrayPalette;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ArrayPalette.class)
public class ArrayPaletteFibber<T> implements Fibber {
    @Mutable @Final @Shadow private final IdList<T> idList;
    public ArrayPaletteFibber(IdList<T> idList) {
        this.idList = idList;
    }

    @Redirect(method = "toPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IdList;getId(Ljava/lang/Object;)I"))
    public int toPacketRedir(IdList<T> idList, T object) {
        return idList.getId((T) FibLib.Blocks.get((BlockState) object, this.player));
    }

    @Redirect(method = "getPacketSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IdList;getId(Ljava/lang/Object;)I"))
    public int getPacketSizeRedir(IdList<T> idList, T object) {
        return idList.getId((T) FibLib.Blocks.get((BlockState) object, this.player));
    }

    private ServerPlayerEntity player;

    @Override
    public void fix(ServerPlayerEntity player) {
        this.player = player;
    }
}