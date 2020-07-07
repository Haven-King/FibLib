package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Fibber;
import me.jellysquid.mods.lithium.common.world.chunk.LithiumHashPalette;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.IdList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@SuppressWarnings("unchecked")
@Mixin(LithiumHashPalette.class)
public class LithiumHashPaletteFibber<T> implements Fibber {


	@Redirect(method = "toPacket", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IdList;getId(Ljava/lang/Object;)I"))
	public int toPacketRedir(IdList<T> idList, T object) {
		return idList.getId((T) FibLib.Blocks.get((BlockState) object, this.player, false));
	}

	@Redirect(method = "getPacketSize", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IdList;getId(Ljava/lang/Object;)I"))
	public int getPacketSizeRedir(IdList<T> idList, T object) {
		return idList.getId((T) FibLib.Blocks.get((BlockState) object, this.player, false));
	}

	private ServerPlayerEntity player;

	@Override
	public void fix(ServerPlayerEntity player) {
		this.player = player;
	}
}
