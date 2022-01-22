package dev.hephaestus.fiblib.mixin.packets.chunkdata;

import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.impl.Fixable;
import me.jellysquid.mods.lithium.common.world.chunk.LithiumHashPalette;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.IndexedIterable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unchecked")
@Mixin(LithiumHashPalette.class)
public class MixinLithiumHashPalette<T> implements Fixable {
    @Redirect(method = "writePacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IndexedIterable;getRawId(Ljava/lang/Object;)I"))
    public int toPacketRedir(IndexedIterable<T> idList, T object) {
        return idList.getRawId((T) BlockFibRegistry.getBlockState((BlockState) object, this.player));
    }

    @Redirect(method = "getPacketSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IndexedIterable;getRawId(Ljava/lang/Object;)I"))
    public int getPacketSizeRedir(IndexedIterable<T> idList, T object) {
        return idList.getRawId((T) BlockFibRegistry.getBlockState((BlockState) object, this.player));
    }

    private ServerPlayerEntity player;

    @Override
    public void fix(ServerPlayerEntity player) {
        this.player = player;
    }
}
