package dev.hephaestus.fiblib;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Blocks;
import net.minecraft.world.dimension.DimensionType;

public class Tester implements ModInitializer {
    static boolean DEBUG = false;
    @Override
    public void onInitialize() {
        if (DEBUG) {
            FibLib.register(DimensionType.OVERWORLD, Blocks.GRASS_BLOCK, (state, player) -> Blocks.STONE.getDefaultState());
        }
    }
}
