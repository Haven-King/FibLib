package dev.hephaestus.fiblib.fabric.mixin.packets;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.fabric.Fixable;
import dev.hephaestus.fiblib.impl.LookupImpl;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientboundBlockBreakAckPacket.class)
public class MixinClientBlockBreakAckPacket implements Fixable {
    @Shadow
    private BlockState state;

    @Override
    public void fix(Player player) {
        FibLib.debug("Fixing block %s for %s", this.state.getBlock().getName().getString(), player.getName().getString());
        this.state = LookupImpl.findState(this.state, player, false);
    }
}
