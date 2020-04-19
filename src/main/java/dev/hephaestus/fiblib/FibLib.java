package dev.hephaestus.fiblib;

import com.google.common.collect.Iterables;
import dev.hephaestus.fiblib.blocks.BlockFib;
import dev.hephaestus.fiblib.blocks.FibScriptLoader;
import dev.hephaestus.fiblib.blocks.ScriptedBlockFib;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

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

	public static class Blocks extends PersistentState {
		private static final String SAVE_KEY = FibLib.SAVE_KEY + "_blocks";

		private static final HashMap<DimensionType, HashMap<BlockState, LongSet>> BLOCKS = new HashMap<>();
		private static final HashMap<BlockState, BlockFib> FIBS = new HashMap<>();

		private static final CompositeMap<BlockState> LOOKUPS = new CompositeMap<>();

		private Blocks() {
			super(SAVE_KEY);
			this.markDirty();
		}

		private static FibLib.Blocks getInstance() {
			ServerWorld world = Objects.requireNonNull(getServer()).getWorld(DimensionType.OVERWORLD);

			return world.getPersistentStateManager().getOrCreate(Blocks::new, SAVE_KEY);
		}

		@Internal
		@Override
		public void fromTag(CompoundTag tag) {
			BLOCKS.clear();

			CompoundTag fibTag = tag.getCompound(SAVE_KEY);

			for (String d : fibTag.getKeys()) {
				DimensionType dim = Registry.DIMENSION_TYPE.get(Integer.parseInt(d));
				BLOCKS.put(dim, new HashMap<>());
				for (String k : fibTag.getCompound(d).getKeys()) {
					BLOCKS.get(dim).put(Block.getStateFromRawId(Integer.parseInt(k)), new LongOpenHashSet(fibTag.getLongArray(d)));
				}
			}
		}

		@Internal
		@Override
		public CompoundTag toTag(CompoundTag tag) {
			CompoundTag fibTag = new CompoundTag();
			for (Map.Entry<DimensionType, HashMap<BlockState, LongSet>> d : BLOCKS.entrySet()) {
				CompoundTag dimTag = new CompoundTag();

				for (Map.Entry<BlockState, LongSet> e : d.getValue().entrySet()) {
					dimTag.put(Block.getRawIdFromState(e.getKey()) + "", new LongArrayTag(e.getValue()));
				}

				fibTag.put(Registry.DIMENSION_TYPE.getRawId(d.getKey()) + "", dimTag);
			}

			tag.put(SAVE_KEY, fibTag);

			return tag;
		}

		// API methods

		/**
		 * Updates all tracked blocks in a given world. Somewhat expensive, and should probably not really be called. If you
		 * need to update multiple kinds of blocks, see the methods below
		 *
		 */
		private static int s0 = LOOKUPS.entrySet().size();
		private static int s1 = 0;
		private static int s2 = FIBS.size();
		private static int s3 = 0;
		public static void update() {
			MinecraftServer server = getServer();

			if (server != null) {
				Set<Map.Entry<CompositeMap.Key, BlockState>> set = LOOKUPS.entrySet();
				ArrayList<BlockState> updates = new ArrayList<>();
				for (Map.Entry<CompositeMap.Key, BlockState> entry : set) {
					BlockState oldState = entry.getValue();
					BlockState newState = FIBS.get((BlockState)entry.getKey().get(0)).get((BlockState)entry.getKey().get(0), (ServerPlayerEntity)entry.getKey().get(1));
					if (newState != oldState) {
						LOOKUPS.put(newState, entry.getKey());
						updates.add(newState);
					}
				}

				int i = 0;
				for (DimensionType dim : BLOCKS.keySet())
					for (BlockState state : updates) {
						ServerWorld world = server.getWorld(dim);
						if (BLOCKS.get(dim) != null && BLOCKS.get(dim).get(state) != null)
							for (Long pos : BLOCKS.get(dim).get(state)) {
								world.getChunkManager().markForUpdate(BlockPos.fromLong(pos));
								i++;
							}
					}

				if (s0 != LOOKUPS.entrySet().size() || (BLOCKS.get(DimensionType.OVERWORLD) != null && s1 != BLOCKS.get(DimensionType.OVERWORLD).size()) || s2 != FIBS.size()) {
					s0 = LOOKUPS.entrySet().size();
					s1 = BLOCKS.get(DimensionType.OVERWORLD).size();
					s2 = FIBS.size();

					FibLib.debug("%d %d %d", s0, s1, s2);
				}
//
//				if (i > 0) {
//					FibLib.debug("Updated %d blocks. ", i);
//				}
			}
		}

		/**
		 * Updates all of one kind of block.
		 *
		 * @param dimension the dimension to update in
		 * @param state the block type to update
		 */
		@SuppressWarnings("unused")
		public static void update(DimensionType dimension, BlockState state) {
			MinecraftServer server = getServer();
			if (server != null) {
				ServerWorld world = getServer().getWorld(dimension);
				if (BLOCKS.containsKey(dimension) && BLOCKS.get(dimension).containsKey(state)) {
					int i = 0;
					for (Long l : BLOCKS.get(dimension).get(state)) {
						world.getChunkManager().markForUpdate(BlockPos.fromLong(l));
						++i;
					}
					FibLib.log("Updated %d blocks", i);
				}
			}
		}

		/**
		 * Helper function for updating multiple kinds of blocks
		 *
		 * @param dimension the dimension to update in
		 * @param states the blocks to update
		 */
		@SuppressWarnings("unused")
		public static void update(DimensionType dimension, BlockState... states) {
			MinecraftServer server = getServer();
			if (server != null) {
				int i = 0;
				ServerWorld world = server.getWorld(dimension);
				for (BlockState a : states) {
					if (BLOCKS.containsKey(dimension) && BLOCKS.get(dimension).containsKey(a)) {
						for (Long l : BLOCKS.get(dimension).get(a)) {
							world.getChunkManager().markForUpdate(BlockPos.fromLong(l));
							++i;
						}
					}
				}

				FibLib.log("Updated %d blocks", i);
			}
		}

		/**
		 * Helper function for updating multiple kinds of blocks
		 *
		 * @param dimension the dimension to update in
		 * @param states the blocks to update
		 */
		@SuppressWarnings("unused")
		public static void update(DimensionType dimension, Collection<BlockState> states) {
			MinecraftServer server = getServer();
			if (server != null) {
				int i = 0;
				ServerWorld world = server.getWorld(dimension);
				for (BlockState a : states) {
					if (Blocks.BLOCKS.containsKey(dimension) && Blocks.BLOCKS.get(dimension).containsKey(a)) {
						for (Long l : Blocks.BLOCKS.get(dimension).get(a)) {
							world.getChunkManager().markForUpdate(BlockPos.fromLong(l));
							++i;
						}
					}
				}

				if (i != 0)
					FibLib.log("Updated %d blocks", i);
			}
		}


		/**
		 * Use this function to register Fibs when you already have access to a World.
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

			if (!LOOKUPS.containsKey(state, player) && FIBS.containsKey(state))
				LOOKUPS.put(FIBS.get(state).get(state, player), state, player);

			return LOOKUPS.getOrDefault(state, state, player);
		}


		/**
		 * Begins tracking a block for updates. Any time a Fibber that applies to this block checks its conditions, it will
		 * be against this entry. Automatically called on block add,see dev.hephaestus.fiblib.mixin.BlockMixin
		 * @param world the world that this block is in
		 * @param state the block state we care about; note that only the actual Block is used, state info is disregarded
		 * @param pos   the position of the block we are going to keep track of
		 */
		public static void track(ServerWorld world, BlockState state, BlockPos pos) {
			track(world.getDimension().getType(), state, pos);
		}

		/**
		 * This method allows you to register a block to be watched before the world it's in is finished being built.
		 * This is useful for registering blocks to be tracked during worldgen.
		 * @param state the block we care about. used for selective updating
		 * @param pos   the position of the block we are going to keep track of
		 */
		public static void track(DimensionType dimension, BlockState state, BlockPos pos) {
			BLOCKS.putIfAbsent(dimension, new HashMap<>());
			BLOCKS.get(dimension).putIfAbsent(state, new LongOpenHashSet());
			BLOCKS.get(dimension).get(state).add(pos.asLong());
			getInstance().markDirty();
		}

		/**
		 * Removes a block from tracking. Automatically called on block removal, see dev.hephaestus.fiblib.mixin.BlockMixin
		 *
		 * @param world the world that this block is in
		 * @param pos   the position of the block we are going to keep track of
		 */
		public static void stopTracking(ServerWorld world, BlockPos pos) {
			DimensionType dim = world.dimension.getType();
			if (BLOCKS.containsKey(dim) && BLOCKS.get(dim).containsKey(world.getBlockState(pos)))
				BLOCKS.get(dim).get(world.getBlockState(pos)).remove(pos.asLong());

			Blocks instance = getInstance();
			if (getInstance() != null)
				instance.markDirty();
		}

		@SuppressWarnings("unused")
		public static void stopTracking(DimensionType dimension, BlockPos pos) {
			MinecraftServer server = getServer();
			if (server != null)
				stopTracking(server.getWorld(dimension), pos);
		}
	}
}
