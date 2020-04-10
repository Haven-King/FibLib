package dev.hephaestus.fiblib;

import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;

public class Tester implements ModInitializer {
    public static boolean DEBUG = false;
    public HashMap<Block, Integer> levelRequireToDetect = new HashMap<>();

    @Override
    public void onInitialize() {
        if (DEBUG) {
            levelRequireToDetect.put(Blocks.COAL_ORE, 10);
            levelRequireToDetect.put(Blocks.REDSTONE_ORE, 15);
            levelRequireToDetect.put(Blocks.IRON_ORE, 15);
            levelRequireToDetect.put(Blocks.LAPIS_ORE, 20);
            levelRequireToDetect.put(Blocks.GOLD_ORE, 20);
            levelRequireToDetect.put(Blocks.DIAMOND_ORE, 30);
            levelRequireToDetect.put(Blocks.EMERALD_ORE, 30);

            for (Map.Entry<Block, Integer> e : levelRequireToDetect.entrySet()) {
                FibLib.register(DimensionType.OVERWORLD, e.getKey(), (state, player) ->
                        player.isCreative() ?
                                state :
                                Blocks.STONE.getDefaultState()
                );
            }
        }
    }
}
