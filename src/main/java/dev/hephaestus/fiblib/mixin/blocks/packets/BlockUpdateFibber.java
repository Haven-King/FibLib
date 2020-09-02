package dev.hephaestus.fiblib.mixin.blocks.packets;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.blocks.Fixable;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockUpdateS2CPacket.class)
public class BlockUpdateFibber implements Fixable {
    @Shadow private BlockState state;

    @Override
    public void fix(WorldChunk chunk, int includedSectionsMask, ServerPlayerEntity player) {
        this.state = FibLib.Blocks.get(this.state, player, true);
    }
}
