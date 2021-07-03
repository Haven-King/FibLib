package dev.hephaestus.fiblib.mixin.packets.chunkdata;

import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkDeltaUpdateS2CPacket.class)
public class MixinChunkDeltaUpdateS2CPacket implements Fixable {
    @Final @Shadow private BlockState[] blockStates;

    @Override
    public void fix(ServerPlayerEntity player) {
        for (int i = 0; i < this.blockStates.length; ++i) {
            this.blockStates[i] = BlockFibRegistry.getBlockState(this.blockStates[i], player);
        }
    }
}
