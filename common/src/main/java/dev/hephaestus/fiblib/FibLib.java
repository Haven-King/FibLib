package dev.hephaestus.fiblib;

import dev.hephaestus.fiblib.api.BlockFib;
import dev.hephaestus.fiblib.api.BlockFibRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class FibLib {
	public static final String MOD_ID = "fiblib";
	private static final String MOD_NAME = "FibLib";

	private static final Logger LOGGER = LogManager.getLogger();
    public static boolean DEBUG = false;

    public static void enableDebug() {
        DEBUG = true;
        log("Debug enabled");
    }

    public static void log(String msg) {
        log("%s", msg);
    }

    public static void log(String format, Object... args) {
        LOGGER.info(String.format("[%s] %s", MOD_NAME, String.format(format, args)));
    }

    public static void debug(String msg) {
        debug("%s", msg);
    }

    public static void debug(String format, Object... args) {
        if (DEBUG) LOGGER.info(String.format("[%s] %s", MOD_NAME, String.format(format, args)));
    }

    public static void fibIronOre() {
        ResourceLocation ironOre = new ResourceLocation("minecraft:iron_ore");
        ResourceLocation stone = new ResourceLocation("minecraft:stone");
        BlockFib fib = BlockFib.builder(Registry.BLOCK.get(ironOre), Registry.BLOCK.get(stone)).modifiesDrops().build();
        BlockFibRegistry.register(fib);
    }

    public static void fibCoalOre() {
        ResourceLocation coalOre = new ResourceLocation("minecraft:coal_ore");
        ResourceLocation stone = new ResourceLocation("minecraft:stone");
        BlockFib fib = BlockFib.builder(Registry.BLOCK.get(coalOre), Registry.BLOCK.get(stone)).lenient()
                .withCondition(player -> !player.isCreative())
                .build();
        BlockFibRegistry.register(fib);
    }
}
