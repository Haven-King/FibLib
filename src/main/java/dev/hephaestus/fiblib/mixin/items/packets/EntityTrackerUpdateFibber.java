package dev.hephaestus.fiblib.mixin.items.packets;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Fibber;
import dev.hephaestus.fiblib.items.ItemContext;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(EntityTrackerUpdateS2CPacket.class)
public class EntityTrackerUpdateFibber implements Fibber {

    @Shadow private List<DataTracker.Entry<?>> trackedValues;

    @Override
    public void fix(ServerPlayerEntity player) {
        for(DataTracker.Entry<?> entry : trackedValues) {
            Object data = entry.get();
            if (data instanceof ItemStack) {
                ItemStack stack = (ItemStack)data;
                DataTracker.Entry<ItemStack> itemEntry = (DataTracker.Entry<ItemStack>)entry;
                itemEntry.set(FibLib.Items.get(stack,player,ItemContext.MISC));
            }
        }
    }
}
