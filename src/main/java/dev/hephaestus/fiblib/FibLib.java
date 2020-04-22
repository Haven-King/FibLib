package dev.hephaestus.fiblib;

import dev.hephaestus.fiblib.blocks.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InvalidObjectException;
import java.util.*;

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
		private static final CompositeMap<BlockState> LOOKUPS = new CompositeMap<>(BlockState.class, ServerPlayerEntity.class);
		private static final HashMap<BlockState, BlockFib> FIBS = new HashMap<>();
		private static int VERSION = 0;

		public static void tick() {
			Set<Map.Entry<CompositeMap.Key, BlockState>> set = LOOKUPS.entrySet();
			IntList updates = new IntArrayList();
			for (Map.Entry<CompositeMap.Key, BlockState> entry : set) {
				BlockState oldState = entry.getValue();
				BlockState newState = FIBS.get((BlockState) entry.getKey().get(0)).get((BlockState) entry.getKey().get(0), (ServerPlayerEntity) entry.getKey().get(1));
				if (newState != oldState) {
					LOOKUPS.put(newState, entry.getKey());
					updates.add(Block.STATE_IDS.getId((BlockState) entry.getKey().get(0)));
				}
			}

			if (updates.size() > 0) {
				VERSION++;
			}
		}

		public static int getVersion() {return VERSION;}

		// API methods
		/**
		 * Use this function to register Fibs.
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
		 * @return the result of the fib. This is what the player will get told the block is
		 */
		public static BlockState get(BlockState state, ServerPlayerEntity player) {
			if (player == null) return state;

			try {
				if (!LOOKUPS.containsKey(state, player) && FIBS.containsKey(state))
					LOOKUPS.put(FIBS.get(state).get(state, player), state, player);

				return LOOKUPS.getOrDefault(state, state, player);
			} catch (InvalidObjectException e) {
				FibLib.debug("Invalid key: ", e.getMessage());
				return state;
			}
		}

		public static boolean contains(BlockState state) {
			return FIBS.containsKey(state);
		}
	}
}
