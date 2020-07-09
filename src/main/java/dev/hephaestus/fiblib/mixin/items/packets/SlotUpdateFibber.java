package dev.hephaestus.fiblib.mixin.items.packets;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Fibber;
import dev.hephaestus.fiblib.items.ItemContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ScreenHandlerSlotUpdateS2CPacket.class)
public class SlotUpdateFibber implements Fibber {
    @Shadow private ItemStack stack;

    @Override
    public void fix(ServerPlayerEntity player) {
        stack = FibLib.Items.get(stack, player, ItemContext.INVENTORY);
    }
}
