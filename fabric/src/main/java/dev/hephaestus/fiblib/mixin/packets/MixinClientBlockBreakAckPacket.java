package dev.hephaestus.fiblib.mixin.packets;

import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.impl.FibLog;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientboundBlockBreakAckPacket.class)
public class MixinClientBlockBreakAckPacket implements Fixable {
    @Shadow
    private BlockState state;

    @Override
    public void fix(ServerPlayer player) {
        FibLog.debug("Fixing block %s for %s", this.state.getBlock().getName().getString(), player.getName().getString());
        this.state = BlockFibRegistry.getBlockStateLenient(this.state, player);
    }
}
