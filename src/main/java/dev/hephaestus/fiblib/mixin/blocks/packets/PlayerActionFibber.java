package dev.hephaestus.fiblib.mixin.blocks.packets;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.blocks.Fixable;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerActionResponseS2CPacket.class)
public class PlayerActionFibber implements Fixable {
    @Shadow private BlockState state;

    @Override
    public void fix(WorldChunk chunk, int includedSectionsMask, ServerPlayerEntity player) {
        this.state = FibLib.Blocks.get(state, player, true);
    }
}
