package dev.hephaestus.fiblib;

import dev.hephaestus.fiblib.blocks.*;
import dev.hephaestus.fiblib.blocks.fibs.PositionedFib;
import dev.hephaestus.fiblib.blocks.fibs.BlockFib;
import dev.hephaestus.fiblib.blocks.fibs.PlayerFib;
import dev.hephaestus.fiblib.blocks.fibs.StaticFib;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.ChunkComponentCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("unused")
public class FibLib implements ModInitializer {
	public static final String MOD_ID = "fiblib";
	private static final String MOD_NAME = "FibLib";

	private static final Logger LOGGER = LogManager.getLogger();
    public static boolean DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();

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

	@Override
	public void onInitialize() {
		FibLib.Blocks.register(new StaticFib(
				net.minecraft.block.Blocks.GRASS_BLOCK,
				net.minecraft.block.Blocks.OBSIDIAN
		));

		FibLib.Blocks.register(new PlayerFib(net.minecraft.block.Blocks.COAL_ORE, net.minecraft.block.Blocks.GLOWSTONE) {
			@Override
			protected boolean condition(ServerPlayerEntity player) {
				return !player.isCreative();
			}
		});
	}

	public static class Blocks {
		public static final ComponentType<TrackerComponent> TRACKER =
				ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier("fiblib:blocks.tracker"), TrackerComponent.class);

		static {
			ChunkComponentCallback.EVENT.register((chunk, components) -> components.put(TRACKER, new BlockTrackerComponent(chunk)));
		}

		private static final HashMap<BlockState, BlockFib> FIBS = new HashMap<>();
		private static final LookupTable LOOKUPS = new LookupTable();

		static final int rate = 20;
		static int ticks = 0;
		public static void tick() {
			if (ticks % rate == 0) LOOKUPS.update();
			ticks = ticks + 1 % rate;
		}

		public static int getVersion() {return LOOKUPS.getVersion();}

		// API methods
		/**
		 * Use this function to register Fibs that aren't created with scripts.
		 *
		 * @param fib   the fib itself
		 */
		public static void register(BlockFib fib) {
			FIBS.put(fib.getInput(), fib);
			FibLib.log("Registered a BlockFib for %s", fib.getInput().getBlock().getTranslationKey());
		}

		/**
		 * Returns the result of any fibs on a given BlockState
		 *
		 * @param state  the state of the block we're inquiring about. Note that because this is passed to a BlockFib, other
		 *               aspects of the state than the Block may be used in determining the output
		 * @param player the player who we will be fibbing to
		 * @param pos	 position of the block we're interested in
		 * @return the result of the fib. This is what the player will get told the block is
		 */
		public static BlockState get(BlockState state, @Nullable ServerPlayerEntity player, @Nullable BlockPos pos) {
			if (!FIBS.containsKey(state)) return state;

			BlockFib fib = FIBS.get(state);

			if (fib instanceof PositionedFib) 	return LOOKUPS.get((PositionedFib) fib, state, player, pos);
			if (fib instanceof PlayerFib)		return LOOKUPS.get((PlayerFib) fib, state, player);
			if (fib instanceof StaticFib) 		return fib.getOutput(state);

			FibLib.debug("Unknown fib type: %s", fib.getClass().getCanonicalName());
			return state;
		}

		/**
		 * Returns whether or not a fib exists for the given state.
		 *
		 * @param state the state to inquire about
		 * @return the result of the inquiry
		 */
		public static boolean contains(BlockState state) {
			return FIBS.containsKey(state);
		}
	}
}
