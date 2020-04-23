package dev.hephaestus.fiblib;

import dev.hephaestus.fiblib.blocks.BlockFib;
import dev.hephaestus.fiblib.blocks.ChunkTracker;
import dev.hephaestus.fiblib.blocks.FibScriptLoader;
import dev.hephaestus.fiblib.blocks.ScriptedBlockFib;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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

	@SuppressWarnings("unused")
	public static void debug(String msg) {
		debug("%s", msg);
	}

	public static void debug(String format, Object... args) {
		if (DEBUG) LOGGER.info(String.format("[%s] %s", MOD_NAME, String.format(format, args)));
	}

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new FibScriptLoader());
	}

	public static class Blocks {
		/*   Fuck everything about this ----------------------------------------------------------------------------------------- */
		/**/ private static final HashMap<BlockState, BlockState> LOOKUPS_1 = new HashMap<>();
		/**/ private static final HashMap<Pair<BlockState, ServerPlayerEntity>, BlockState> LOOKUPS_2 = new HashMap<>();
		/**/ private static final HashMap<Triple<BlockState, ServerPlayerEntity, BlockPos>, BlockState> LOOKUPS_3 = new HashMap<>();
		/* ---------------------------------------------------------------------------------------------------------------------- */

		private static final HashMap<BlockState, BlockFib> FIBS = new HashMap<>();
		private static int VERSION = 0;

		static final long rate = 20;
		static int ticks = 0;
		public static void tick() {
			if (ticks % rate == 0) {
				IntList updates = new IntArrayList();

				for (Map.Entry<Triple<BlockState, ServerPlayerEntity, BlockPos>, BlockState> entry : LOOKUPS_3.entrySet()) {
					BlockState oldState = entry.getValue();

					BlockState input = entry.getKey().getLeft();
					ServerPlayerEntity player = entry.getKey().getMiddle();
					BlockPos pos = entry.getKey().getRight();

					BlockState newState = FIBS.get(input).get(input, player, pos);

					if (newState != oldState) {
						LOOKUPS_3.put(entry.getKey(), newState);
						updates.add(Block.STATE_IDS.getId(input));
					}
				}

				for (Map.Entry<Pair<BlockState, ServerPlayerEntity>, BlockState> entry : LOOKUPS_2.entrySet()) {
					BlockState oldState = entry.getValue();

					BlockState input = entry.getKey().getLeft();
					ServerPlayerEntity player = entry.getKey().getRight();

					BlockState newState = FIBS.get(input).get(input, player, null);

					if (newState != oldState) {
						LOOKUPS_2.put(entry.getKey(), newState);
						updates.add(Block.STATE_IDS.getId(input));
					}
				}

				if (updates.size() > 0) {
					FibLib.debug("Updating %s", updates.toString());
					VERSION++;
				}

				ticks = 0;
			}

			ticks++;
		}

		public static int getVersion() {return VERSION;}

		// API methods
		/**
		 * Use this function to register Fibs that aren't created with scripts.
		 *
		 * @param state the block to be fibbed
		 * @param fib   the fib itself. Can be a lambda expression for simpler fibs, or an implementation of BlockFib for
		 *              fibs that need some more complex processing
		 */
		public static void register(BlockState state, ScriptedBlockFib fib) {
			FIBS.put(state, fib);
			FibLib.log("Registered a BlockFib for %s", state.getBlock().getTranslationKey());
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

			Object key;
			HashMap lookups;

			if (player != null && pos != null) {
				key = new ImmutableTriple<>(state, player, pos);
				ChunkTracker.inject(player.getServerWorld().getChunk(pos)).track(state, pos);
				lookups = LOOKUPS_3;
			} else if (player != null) {
				key = new Pair<>(state, player);
				lookups = LOOKUPS_2;
			} else {
				key = state;
				lookups = LOOKUPS_1;
			}

			if (!lookups.containsKey(key))
				lookups.put(key, FIBS.get(state).get(state, player, pos));

			return (BlockState) lookups.getOrDefault(state, state);
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
