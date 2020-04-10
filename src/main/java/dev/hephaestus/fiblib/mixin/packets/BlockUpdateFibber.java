package dev.hephaestus.fiblib.mixin.packets;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Fibber;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockUpdateS2CPacket.class)
public class BlockUpdateFibber implements Fibber {
    @Shadow private BlockState state;
    @Shadow private BlockPos pos;

    @Override
    public void fix(ServerPlayerEntity player) {
        FibLib.put(state.getBlock(), pos); // We want to start tracking this block if we haven't already
        this.state = FibLib.get(state, player);
    }
}
