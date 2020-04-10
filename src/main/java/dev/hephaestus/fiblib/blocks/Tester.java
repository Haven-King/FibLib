package dev.hephaestus.fiblib.blocks;

import dev.hephaestus.fiblib.FibLib;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.dimension.DimensionType;

import java.util.Stack;

public class Tester implements ModInitializer {
    public static boolean DEBUG = true;
    public Stack<Block> blocksToFib = new Stack<>();

    @Override
    public void onInitialize() {
        if (DEBUG) {
            blocksToFib.add(Blocks.COAL_ORE);
            blocksToFib.add(Blocks.REDSTONE_ORE);
            blocksToFib.add(Blocks.IRON_ORE);
            blocksToFib.add(Blocks.LAPIS_ORE);
            blocksToFib.add(Blocks.GOLD_ORE);
            blocksToFib.add(Blocks.DIAMOND_ORE);
            blocksToFib.add(Blocks.EMERALD_ORE);

            while (!blocksToFib.isEmpty()) {
                FibLib.Blocks.register(DimensionType.OVERWORLD, blocksToFib.pop(), (state, player) ->
                        player.isCreative() ?
                                state :
                                Blocks.STONE.getDefaultState()
                );
            }
        }
    }
}
