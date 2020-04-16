package dev.hephaestus.fiblib;

import com.google.common.collect.Iterables;
import dev.hephaestus.fiblib.blocks.BlockFib;
import dev.hephaestus.fiblib.blocks.FibberLoader;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FibLib implements ModInitializer {
	public static final String MOD_ID = "fiblib";
	private static final String MOD_NAME = "FibLib";

	private static final Logger LOGGER = LogManager.getLogger();
	private static final String SAVE_KEY = "fiblib";
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
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new FibberLoader());
	}

	private static MinecraftServer getServer() {
		Object game = FabricLoader.getInstance().getGameInstance();
		if (game instanceof MinecraftServer) {
			return (MinecraftServer) game;
		} else {
			return ((MinecraftClient) game).getServer();
		}
	}

	public static class Blocks extends PersistentState {
		private static final String SAVE_KEY = FibLib.SAVE_KEY + "_blocks";

		private static final HashMap<DimensionType, HashMap<Block, LongSet>> blocks = new HashMap<>();
		private static final HashMap<Block, BlockFib> fibs = new HashMap<>();

		private static final HashMap<String, BlockState> lookups = new HashMap<>();

		// Construction methods
		private Blocks(ServerWorld world) {
			super(SAVE_KEY);
			this.markDirty();
		}

		private static String key(ServerPlayerEntity player, BlockState state) {
			return player.getUuidAsString() + Block.STATE_IDS.getId(state);
		}

		private static FibLib.Blocks getInstance() {
			ServerWorld world = getServer().getWorld(DimensionType.OVERWORLD);

			return world.getPersistentStateManager().getOrCreate(() ->
					new Blocks(world), SAVE_KEY
			);
		}

		@Internal
		@Override
		public void fromTag(CompoundTag tag) {
			blocks.clear();

			CompoundTag fibTag = tag.getCompound(SAVE_KEY);

			for (String d : fibTag.getKeys()) {
				DimensionType dim = Registry.DIMENSION_TYPE.get(Integer.parseInt(d));
				blocks.put(dim, new HashMap<>());
				for (String k : fibTag.getCompound(d).getKeys()) {
					blocks.get(dim).put(Registry.BLOCK.get(new Identifier(d)), new LongOpenHashSet(fibTag.getLongArray(d)));
				}
			}
		}

		@Internal
		@Override
		public CompoundTag toTag(CompoundTag tag) {
			CompoundTag fibTag = new CompoundTag();
			for (Map.Entry<DimensionType, HashMap<Block, LongSet>> d : blocks.entrySet()) {
				CompoundTag dimTag = new CompoundTag();

				for (Map.Entry<Block, LongSet> e : d.getValue().entrySet()) {
					dimTag.put(Registry.BLOCK.getId(e.getKey()).toString(), new LongArrayTag(e.getValue()));
				}

				fibTag.put(Registry.DIMENSION_TYPE.getRawId(d.getKey()) + "", dimTag);
			}

			tag.put(SAVE_KEY, fibTag);

			return tag;
		}

		// Instance methods. These are private to make the API simpler.

		// Because we only actually begin tracking the block if we have a fib that references it, it's safe to call put()
		// whenever and wherever we want.
		private void putWithInstance(DimensionType dimensionType, Block block, BlockPos pos) {
			if (fibs.containsKey(block)) {
				blocks.putIfAbsent(dimensionType, new HashMap<>());
				blocks.get(dimensionType).putIfAbsent(block, new LongOpenHashSet());
				blocks.get(dimensionType).get(block).add(pos.asLong());
			}
		}

		private BlockState getWithInstance(BlockState state, ServerPlayerEntity player) {
			String key = key(player, state);
			if (!lookups.containsKey(key) && fibs.containsKey(state.getBlock()))
				lookups.put(key, fibs.get(state.getBlock()).run(state, player));

			return lookups.get(key);
		}

		private void removeWithInstance(ServerWorld world, BlockPos pos) {
			DimensionType dim = world.dimension.getType();
			if (blocks.containsKey(dim) && blocks.get(dim).containsKey(world.getBlockState(pos).getBlock()))
				blocks.get(dim).get(world.getBlockState(pos).getBlock()).remove(pos.asLong());
		}

		// API methods

		/**
		 * Updates all tracked blocks in a given world. Somewhat expensive, and should probably not really be called. If you
		 * need to update multiple kinds of blocks, see the methods below
		 *
		 * @param world the world to update in
		 */
		public static void update(ServerWorld world) {
			lookups.clear();

			int i = 0;
			for (Long l : Iterables.concat(blocks.get(world.dimension.getType()).values())) {
				world.getChunkManager().markForUpdate(BlockPos.fromLong(l));
				++i;
			}

			FibLib.log("Updated %d blocks in %s", i, world.dimension.getType());
		}

		/**
		 * Updates all of one kind of block.
		 *
		 * @param dimension the dimension to update in
		 * @param block the block type to update
		 */
		public static void update(DimensionType dimension, Block block) {
			lookups.clear();

			ServerWorld world = getServer().getWorld(dimension);
			if (blocks.containsKey(dimension) && blocks.get(dimension).containsKey(block)) {
				int i = 0;
				for (Long l : blocks.get(dimension).get(block)) {
					world.getChunkManager().markForUpdate(BlockPos.fromLong(l));
					++i;
				}
				FibLib.log("Updated %d blocks", i);
			}
		}

		/**
		 * Helper function for updating multiple kinds of blocks
		 *
		 * @param dimension the dimension to update in
		 * @param blocks the blocks to update
		 */
		public static void update(DimensionType dimension, Block... blocks) {
			lookups.clear();

			int i = 0;
			ServerWorld world = getServer().getWorld(dimension);
			for (Block a : blocks) {
				if (Blocks.blocks.containsKey(dimension) && Blocks.blocks.get(dimension).containsKey(a)) {
					for (Long l : Blocks.blocks.get(dimension).get(a)) {
						world.getChunkManager().markForUpdate(BlockPos.fromLong(l));
						++i;
					}
				}
			}

			FibLib.log("Updated %d blocks", i);
		}

		/**
		 * Helper function for updating multiple kinds of blocks
		 *
		 * @param dimension the dimension to update in
		 * @param blocks the blocks to update
		 */
		public static void update(DimensionType dimension, Collection<Block> blocks) {
			lookups.clear();

			int i = 0;
			ServerWorld world = getServer().getWorld(dimension);
			for (Block a : blocks) {
				if (Blocks.blocks.containsKey(dimension) && Blocks.blocks.get(dimension).containsKey(a)) {
					for (Long l : Blocks.blocks.get(dimension).get(a)) {
						world.getChunkManager().markForUpdate(BlockPos.fromLong(l));
						++i;
					}
				}
			}

			FibLib.log("Updated %d blocks", i);
		}


		/**
		 * Use this function to register Fibs when you already have access to a World.
		 *
		 * @param block the block to be fibbed
		 * @param fib   the fib itself. Can be a lambda expression for simpler fibs, or an implementation of BlockFib for
		 *              fibs that need some more complex processing
		 */
		public static void register(Block block, BlockFib fib) {
			fibs.put(block, fib);
			FibLib.log("Registered a BlockFib for %s", block.getTranslationKey());
		}

		/**
		 * Returns the result of any fibs on a given BlockState
		 *
		 * @param state  the state of the block we're inquiring about. Note that because this is passed to a BlockFib, other
		 *               aspects of the state than the Block may be used in determining the output
		 * @param player the player who we will be fibbing to
		 * @return the result of the fib. This is what the player will get told the block is
		 */
		@Internal
		public static BlockState get(BlockState state, ServerPlayerEntity player) {
			String key = key(player, state);
			if (!lookups.containsKey(key) && fibs.containsKey(state.getBlock()))
				lookups.put(key, fibs.get(state.getBlock()).run(state, player));

			return lookups.get(key);
		}


		/**
		 * Begins tracking a block for updates. Any time a Fibber that applies to this block checks its conditions, it will
		 * be against this entry. Automatically called on block add,see dev.hephaestus.fiblib.mixin.BlockMixin
		 * @param world the world that this block is in
		 * @param block the block we care about. used for selective updating
		 * @param pos   the position of the block we are going to keep track of
		 */
		public static void track(ServerWorld world, Block block, BlockPos pos) {
			track(world.dimension.getType(), block, pos);
		}

		/**
		 * @param world the world that this block is in
		 * @param state the block state we care about; note that only the actual Block is used, state info is disregarded
		 * @param pos   the position of the block we are going to keep track of
		 */
		public static void track(ServerWorld world, BlockState state, BlockPos pos) {
			track(world, state.getBlock(), pos);
		}

		/**
		 * This method allows you to register a block to be watched before the world it's in is finished being built.
		 * This is useful for registering blocks to be tracked during worldgen.
		 * @param block the block we care about. used for selective updating
		 * @param pos   the position of the block we are going to keep track of
		 */
		public static void track(DimensionType dimension, Block block, BlockPos pos) {
			blocks.get(dimension).get(block).add(pos.asLong());
		}

		public static void track(DimensionType dimension, BlockState state, BlockPos pos) {
			track(dimension, state.getBlock(), pos);
		}

		public static void clearFibs() {

		}



		/**
		 * Removes a block from tracking. Automatically called on block removal, see dev.hephaestus.fiblib.mixin.BlockMixin
		 *
		 * @param world the world that this block is in
		 * @param pos   the position of the block we are going to keep track of
		 */
		public static void stopTracking(ServerWorld world, BlockPos pos) {
			DimensionType dim = world.dimension.getType();
			if (blocks.containsKey(dim) && blocks.get(dim).containsKey(world.getBlockState(pos).getBlock()))
				blocks.get(dim).get(world.getBlockState(pos).getBlock()).remove(pos.asLong());
		}

		public static void stopTracking(DimensionType dimension, BlockPos pos) {
			stopTracking(getServer().getWorld(dimension), pos);
		}

		public static int numberOfFibs() {
			return fibs.size();
		}
	}
}
