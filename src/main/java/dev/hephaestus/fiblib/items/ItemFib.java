package dev.hephaestus.fiblib.items;

import net.minecraft.item.ItemStack;

/**
 * Defines an itemFib. This class will convert the serverside item into a clientside one.
 */
public abstract class ItemFib {
    public abstract ItemStack getOutput(ItemStack input);
}
