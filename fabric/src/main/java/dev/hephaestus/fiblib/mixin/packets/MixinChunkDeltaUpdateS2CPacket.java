package dev.hephaestus.fiblib.mixin.packets;

import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.impl.FibLog;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientboundSectionBlocksUpdatePacket.class)
public class MixinChunkDeltaUpdateS2CPacket implements Fixable {
    @Shadow
    private BlockState[] states;

    @Override
    public void fix(ServerPlayer player) {
        FibLog.debug("Fixing %s blocks in chunk section for %s", this.states.length, player.getName().getString());
        for (int i = 0; i < this.states.length; ++i) {
            this.states[i] = BlockFibRegistry.getBlockState(this.states[i], player);
        }
    }
}
