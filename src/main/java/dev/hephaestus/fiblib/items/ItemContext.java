package dev.hephaestus.fiblib.items;

public enum ItemContext {
    /**
     * The ItemStack is in an inventory. A chest, the players inventory, etc.
     */
    INVENTORY,
    /**
     * This item is being held as equipment. This could be an armor slot. Or a held item in the off/main hand.
     * This can be used on either an entity or a player.
     */
    EQUIPMENT,
    /**
     * Other usages. This can be in an itemframe, as an item entity, or any other use
     */
    MISC
}
