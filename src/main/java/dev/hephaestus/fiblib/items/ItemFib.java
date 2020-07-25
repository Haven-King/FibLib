package dev.hephaestus.fiblib.items;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import javax.annotation.Nullable;

/**
 * Defines an itemFib. This class will convert the serverside item into a clientside one.
 */
public interface ItemFib {
    /**
     * Convert an ItemStack to it's clientside version
     * @param input The original ItemStack
     * @param player The player this ItemStack is being sent to
     * @param context The context in which this ItemStack is being used
     * @return
     */
    ItemStack getOutput(ItemStack input, @Nullable ServerPlayerEntity player, @Nullable ItemContext context);
}
