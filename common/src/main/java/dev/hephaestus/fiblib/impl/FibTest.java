package dev.hephaestus.fiblib.impl;

import dev.hephaestus.fiblib.api.BlockFib;
import dev.hephaestus.fiblib.api.BlockFibRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class FibTest {
    public static void fibIronOre() {
        ResourceLocation ironOre = new ResourceLocation("minecraft:iron_ore");
        ResourceLocation stone = new ResourceLocation("minecraft:stone");
        BlockFib fib = BlockFib.builder(Registry.BLOCK.get(ironOre), Registry.BLOCK.get(stone)).modifiesDrops().build();

        ResourceLocation fibName = new ResourceLocation("fiblib:test_iron_ore");
        BlockFibRegistry.register(fibName, fib);
    }

    public static void fibCoalOre() {
        ResourceLocation coalOre = new ResourceLocation("minecraft:coal_ore");
        ResourceLocation stone = new ResourceLocation("minecraft:stone");
        BlockFib fib = BlockFib.builder(Registry.BLOCK.get(coalOre), Registry.BLOCK.get(stone)).modifiesDrops()
                .withCondition(player -> player.isCreative())
                .build();

        ResourceLocation fibName = new ResourceLocation("fiblib:test_coal_ore");
        BlockFibRegistry.register(fibName, fib);
    }
}
