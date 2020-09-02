package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.blocks.Fixable;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkDeltaUpdateS2CPacket.class)
public class ChunkDeltaFibber implements Fixable {
    @Shadow private BlockState[] blockStates;

    @Override
    public void fix(WorldChunk chunk, int includedSectionsMask, ServerPlayerEntity player) {
        for (int i = 0; i < this.blockStates.length; ++i) {
            this.blockStates[i] = FibLib.Blocks.get(this.blockStates[i], player, false);
        }
    }
}
