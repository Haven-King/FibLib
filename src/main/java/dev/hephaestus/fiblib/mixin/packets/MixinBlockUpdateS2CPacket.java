package dev.hephaestus.fiblib.mixin.packets;

import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockUpdateS2CPacket.class)
public class MixinBlockUpdateS2CPacket implements Fixable {
    @Final @Mutable @Shadow private BlockState state;

    @Override
    public void fix(ServerPlayerEntity player) {
        this.state = BlockFibRegistry.getBlockStateLenient(this.state, player);
    }
}
