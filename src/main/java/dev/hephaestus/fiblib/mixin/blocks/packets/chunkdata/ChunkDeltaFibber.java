package dev.hephaestus.fiblib.mixin.blocks.packets.chunkdata;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Fibber;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.Field;

@Mixin(ChunkDeltaUpdateS2CPacket.class)
public class ChunkDeltaFibber implements Fibber {
    @Shadow
    private ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord[] records;

    @Override
    public void fix(ServerPlayerEntity player) {
        try {
            Field stateField = ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord.class.getDeclaredField("state");
            stateField.setAccessible(true);
            for (ChunkDeltaUpdateS2CPacket.ChunkDeltaRecord record : this.records) {
                BlockState before = record.getState();
                stateField.set(record, FibLib.Blocks.get(before, player, false));
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
