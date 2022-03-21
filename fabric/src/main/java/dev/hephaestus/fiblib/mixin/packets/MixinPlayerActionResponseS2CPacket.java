package dev.hephaestus.fiblib.mixin.packets;

import dev.hephaestus.fiblib.api.BlockFib;
import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerActionResponseS2CPacket.class)
public class MixinPlayerActionResponseS2CPacket implements Fixable {
    @Shadow private BlockState state;

    @Override
    public void fix(ServerPlayerEntity player) {
        this.state = BlockFibRegistry.getBlockStateLenient(this.state, player);
    }
}
