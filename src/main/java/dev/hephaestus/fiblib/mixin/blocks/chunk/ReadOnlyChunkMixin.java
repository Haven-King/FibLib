package dev.hephaestus.fiblib.mixin.blocks.chunk;

import dev.hephaestus.fiblib.blocks.BlockTracker;
import net.minecraft.world.chunk.ReadOnlyChunk;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ReadOnlyChunk.class)
public class ReadOnlyChunkMixin implements BlockTracker.Provider {

    @Shadow @Final private WorldChunk wrapped;

    @Override
    public BlockTracker getBlockTracker() {
        return ((BlockTracker.Provider) this.wrapped).getBlockTracker();
    }
}
