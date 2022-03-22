package dev.hephaestus.fiblib.mixin.level.chunk;

import dev.hephaestus.fiblib.impl.FibLog;
import dev.hephaestus.fiblib.impl.Fixable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelChunkSection.class)
public class MixinChunkSection implements Fixable {
    @Mutable
    @Final
    @Shadow
    private PalettedContainer<BlockState> states;

    @Override
    public void fix(ServerPlayer player) {
        FibLog.debug("Fixing %s blocks for %s in chunk section", states.getSerializedSize(), player.getName().getString(), this.toString());
        ((Fixable) states).fix(player);
    }
}
