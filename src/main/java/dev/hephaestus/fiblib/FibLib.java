package dev.hephaestus.fiblib;

import dev.hephaestus.fiblib.blocks.BlockFib;
import dev.hephaestus.fiblib.blocks.ChunkTracker;
import dev.hephaestus.fiblib.blocks.LookupTable;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

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
		Blocks.register(new BlockFib(net.minecraft.block.Blocks.COAL_ORE, net.minecraft.block.Blocks.GLOWSTONE) {
			@Override
			protected boolean condition(ServerPlayerEntity player) {
				return !player.isCreative();
			}
		});
	}

	public static class Blocks {
		private static final LookupTable LOOKUPS = new LookupTable();

		private static final HashMap<BlockState, BlockFib> FIBS = new HashMap<>();

		public static void tick() {
			LOOKUPS.update();
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
		 * @return the result of the fib. This is what the player will get told the block is
		 */
		public static BlockState get(BlockState state, @Nullable ServerPlayerEntity player) {
			if (!FIBS.containsKey(state)) return state;

			BlockFib fib = FIBS.get(state);

			return LOOKUPS.get(fib, state, player);
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
