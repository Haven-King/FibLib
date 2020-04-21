package dev.hephaestus.fiblib;

import dev.hephaestus.fiblib.blocks.BlockFib;
import dev.hephaestus.fiblib.blocks.FibScriptLoader;
import dev.hephaestus.fiblib.blocks.ScriptedBlockFib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FibLib implements ModInitializer {
	public static final String MOD_ID = "fiblib";
	private static final String MOD_NAME = "FibLib";

	public static int COUNTER = 0;

	private static final Logger LOGGER = LogManager.getLogger();
	private static final String SAVE_KEY = "fiblib";
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

	private static MinecraftServer getServer() {
		Object game = FabricLoader.getInstance().getGameInstance();
		if (game instanceof MinecraftServer) {
			return (MinecraftServer) game;
		} else {
			return null;
		}
	}

	public static class Blocks implements Tickable {
		private static final CompositeMap<BlockPos> BLOCKS = new CompositeMap<>(DimensionType.class, ChunkPos.class, BlockState.class);
		private static final CompositeMap<BlockState> LOOKUPS = new CompositeMap<>(BlockState.class, ServerPlayerEntity.class);
		private static final HashMap<BlockState, BlockFib> FIBS = new HashMap<>();

		@Override
		public void tick() {
			MinecraftServer server = getServer();

			if (server != null) {
				Set<Map.Entry<CompositeMap.Key, BlockState>> set = LOOKUPS.entrySet();
				ArrayList<BlockState> updates = new ArrayList<>();
				for (Map.Entry<CompositeMap.Key, BlockState> entry : set) {
					BlockState oldState = entry.getValue();
					BlockState newState = FIBS.get((BlockState) entry.getKey().get(0)).get((BlockState) entry.getKey().get(0), (ServerPlayerEntity) entry.getKey().get(1));
					if (newState != oldState) {
						LOOKUPS.put(newState, entry.getKey());
						updates.add(newState);
					}
				}

				for (BlockState update : updates) {
					FibLib.debug("Should update: ", update.getBlock().getTranslationKey());
				}
			}
		}

		public static void track(DimensionType dimensionType, ChunkPos chunkPos, BlockState state, BlockPos pos) {
			try {
				BLOCKS.put(pos, dimensionType, chunkPos, state);
			} catch (InvalidObjectException e) {
				FibLib.debug("Failed to track block: ", e.getMessage());
			}
		}

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
