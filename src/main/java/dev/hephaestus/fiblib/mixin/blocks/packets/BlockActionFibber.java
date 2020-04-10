package dev.hephaestus.fiblib.mixin.blocks.packets;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Fibber;
import net.minecraft.block.Block;
import net.minecraft.network.packet.s2c.play.BlockActionS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockActionS2CPacket.class)
public class BlockActionFibber<T> implements Fibber {
    private ServerPlayerEntity player;
    @Shadow private Block block;
    @Shadow private BlockPos pos;
    @Override
    public void fix(ServerPlayerEntity player) {
        FibLib.Blocks.track(player.getServerWorld(), block, pos); // We want to start tracking this block if we haven't already
        this.block = FibLib.Blocks.get(block.getDefaultState(), player).getBlock();
        this.player = player;
    }
}