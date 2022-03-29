package dev.hephaestus.fiblib.fabric.mixin.packets;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.fabric.Fixable;
import dev.hephaestus.fiblib.impl.LookupImpl;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientboundSectionBlocksUpdatePacket.class)
public class MixinClientSectionBlocksUpdatePacket implements Fixable {
    @Shadow
    private BlockState[] states;

    @Override
    public void fix(Player player) {
        FibLib.debug("Fixing %s blocks in chunk section for %s", this.states.length, player.getName().getString());
        for (int i = 0; i < this.states.length; ++i) {
            this.states[i] = LookupImpl.findState(this.states[i], player);
        }
    }
}
