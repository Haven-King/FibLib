package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Fibber;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.IdList;
import net.minecraft.world.chunk.IdListPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(IdListPalette.class)
public class IdListPaletteFibber<T> implements Fibber {
    @Redirect(method = "getIndex", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/IdList;getId(Ljava/lang/Object;)I"))
    public int getIndexRedir(IdList<T> idList, T object) {
        return idList.getId((T) FibLib.Blocks.get((BlockState) object, this.player));
    }

    private ServerPlayerEntity player;

    @Override
    public void fix(ServerPlayerEntity player) {
        this.player = player;
    }
}
