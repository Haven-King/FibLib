package dev.hephaestus.fiblib.mixin.blocks.packets;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Fibber;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerActionResponseS2CPacket.class)
public class PlayerActionFibber implements Fibber {
    @Shadow private BlockState state;
    @Shadow private BlockPos pos;
    public void fix(ServerPlayerEntity player) {
        FibLib.Blocks.track(player.getServerWorld(), state, pos); // We want to start tracking this block if we haven't already
        this.state = FibLib.Blocks.get(state, player);
    }
}
