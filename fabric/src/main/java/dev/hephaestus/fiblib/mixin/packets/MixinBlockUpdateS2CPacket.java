package dev.hephaestus.fiblib.mixin.packets;

import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.impl.FibLog;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientboundBlockUpdatePacket.class)
public class MixinBlockUpdateS2CPacket implements Fixable {
    @Shadow
    private BlockState blockState;

    @Override
    public void fix(ServerPlayer player) {
        FibLog.debug("Fixing block %s for %s", this.blockState.getBlock().getName().getString(), player.getName().getString());
        this.blockState = BlockFibRegistry.getBlockStateLenient(this.blockState, player);
    }
}
