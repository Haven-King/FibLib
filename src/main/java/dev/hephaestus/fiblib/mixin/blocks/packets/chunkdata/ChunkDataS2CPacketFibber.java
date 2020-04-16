package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import com.google.common.collect.Lists;
import dev.hephaestus.fiblib.Fibber;
import dev.hephaestus.fiblib.blocks.ChunkDataFibber;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(ChunkDataS2CPacket.class)
public abstract class ChunkDataS2CPacketFibber implements ChunkDataFibber {
    @Shadow private int chunkX;
    @Shadow private int chunkZ;
    @Shadow private int verticalStripBitmask;
    @Shadow private CompoundTag heightmaps;
    @Shadow private BiomeArray biomeArray;
    @Shadow private byte[] data;
    @Shadow private List<CompoundTag> blockEntities;
    @Shadow private boolean isFullChunk;

    @Shadow protected abstract int getDataSize(WorldChunk chunk, int includedSectionsMark);
    @Shadow public abstract int writeData(PacketByteBuf packetByteBuf, WorldChunk chunk, int includedSectionsMask);
    @Shadow private ByteBuf getWriteBuffer() {return null;}
    @Shadow public abstract boolean isFullChunk();

    private ServerPlayerEntity player;

    // We are abusing the empty constructor of ChunkDataS2CPacket HARD here. Basically just constructing it here instead
    @Override
    public void fix(WorldChunk chunk, int includedSectionsMask, ServerPlayerEntity player) {
        this.player = player;

        ChunkPos chunkPos = chunk.getPos();
        this.chunkX = chunkPos.x;
        this.chunkZ = chunkPos.z;
        this.isFullChunk = includedSectionsMask == 65535;
        this.heightmaps = new CompoundTag();
        Iterator<Map.Entry<Heightmap.Type, Heightmap>> heightmaps = chunk.getHeightmaps().iterator();

        Map.Entry<Heightmap.Type, Heightmap> heightmap;
        while(heightmaps.hasNext()) {
            heightmap = heightmaps.next();
            if ((heightmap.getKey()).shouldSendToClient()) {
                this.heightmaps.put((heightmap.getKey()).getName(), new LongArrayTag((heightmap.getValue()).asLongArray()));
            }
        }

        if (this.isFullChunk && chunk.getBiomeArray() != null) {
            this.biomeArray = chunk.getBiomeArray().copy();
        }

        this.data = new byte[this.getDataSize(chunk, includedSectionsMask)];
        this.verticalStripBitmask = this.writeData(new PacketByteBuf(this.getWriteBuffer()), chunk, includedSectionsMask);


        this.blockEntities = Lists.newArrayList();
        Iterator<Map.Entry<BlockPos, BlockEntity>> blockEntities = chunk.getBlockEntities().entrySet().iterator();
        Map.Entry<BlockPos, BlockEntity> blockEntity;

        while(true) {
            int i;
            do {
                if (!blockEntities.hasNext()) {
                    return;
                }

                blockEntity = blockEntities.next();
                BlockPos blockPos = blockEntity.getKey();
                i = blockPos.getY() >> 4;
            } while(!this.isFullChunk() && (includedSectionsMask & 1 << i) == 0);

            CompoundTag compoundTag = blockEntity.getValue().toInitialChunkDataTag();
            this.blockEntities.add(compoundTag);
        }
    }

    @Inject(method = "getDataSize", at = @At(value = "HEAD"))
    public void fixDataSize(WorldChunk chunk, int includedSectionsMark, CallbackInfoReturnable<Integer> cir) {
        for (ChunkSection chunkSection : chunk.getSectionArray())
            Fibber.fix(chunkSection, player);
    }
}
