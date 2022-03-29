package dev.hephaestus.fiblib.fabric.mixin.packets;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.fabric.Fixable;
import dev.hephaestus.fiblib.impl.LookupImpl;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientboundBlockUpdatePacket.class)
public class MixinClientBlockUpdatePacket implements Fixable {
    @Shadow
    private BlockState blockState;

    @Override
    public void fix(Player player) {
        FibLib.debug("Fixing block %s for %s", this.blockState.getBlock().getName().getString(), player.getName().getString());
        this.blockState = LookupImpl.findState(this.blockState, player, false);
    }
}
