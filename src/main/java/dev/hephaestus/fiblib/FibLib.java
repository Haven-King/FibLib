package dev.hephaestus.fiblib;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FibLib extends PersistentState {
	public static final String MOD_ID = "fiblib";
	private static final String MOD_NAME = "FibLib";

	private static final Logger LOGGER = LogManager.getLogger();
	private static final String SAVE_KEY = "fiblib";

	private static final HashMap<DimensionType, ArrayList<Pair<Block, BlockFib>>> PRE_LOAD = new HashMap<>();

	private final HashMap<Block,LongSet> blocks = new HashMap<>();
	private final HashMap<Block, BlockFib> fibs = new HashMap<>();

	static void log(String msg) {
		LOGGER.info(String.format("[%s] %s", MOD_NAME, msg));
	}

	static void log(String format, Object... args) {
		LOGGER.info(String.format("[%s] %s", MOD_NAME, String.format(format, args)));
	}

	// Construction methods
	private FibLib(ServerWorld world) {
		super(SAVE_KEY);

		this.markDirty();
	}

	private static FibLib getInstance(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(() ->
			new FibLib(world), SAVE_KEY
		);
	}

	// Convenience
	private static FibLib getInstance(ServerPlayerEntity player) {
		return getInstance(player.getServerWorld());
	}

	// This registers fibs that were added before the World they deal with was loaded. You should not call this.
	public static void registerPreloadedFibs(ServerWorld world) {
		int i = 0;
		if (PRE_LOAD.get(world.getDimension().getType()) != null) {
			for (Pair<Block, BlockFib> p : PRE_LOAD.get(world.getDimension().getType())) {
				FibLib.register(world, p.getLeft(), p.getRight());
				++i;
			}
		}

		if (i > 0) FibLib.log("Registered %d pre-loaded BlockFib%s", i, i == 1 ? "" : "s");
	}

	@Override
	public void fromTag(CompoundTag tag) {
		blocks.clear();

		CompoundTag fibTag = tag.getCompound(SAVE_KEY);

		for (String k : fibTag.getKeys()) {
			blocks.put(Registry.BLOCK.get(new Identifier(k)), new LongOpenHashSet(fibTag.getLongArray(k)));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		CompoundTag fibTag = new CompoundTag();
		for (Map.Entry<Block, LongSet> e : blocks.entrySet()) {
			fibTag.put(Registry.BLOCK.getId(e.getKey()).toString(), new LongArrayTag(e.getValue()));
		}

		tag.put(SAVE_KEY, fibTag);

		return tag;
	}

	// Instance methods. These are private to make the API simpler.
	private void putWithInstance(Block block, BlockPos pos) {
		if (fibs.containsKey(block)) {
			blocks.putIfAbsent(block, new LongOpenHashSet());
			blocks.get(block).add(pos.asLong());
		}
	}

	private BlockState getWithInstance(BlockState state, ServerPlayerEntity player) {
		return fibs.getOrDefault(state.getBlock(), BlockFib.DEFAULT).get(state, player);
	}

	private void removeWithInstance(ServerWorld world, BlockPos pos) {
		if (blocks.containsKey(world.getBlockState(pos).getBlock()))
			blocks.get(world.getBlockState(pos).getBlock()).remove(pos.asLong());
	}

	// API methods
	// Updates all blocks currently being Fibbed. Somewhat expensive.
	public static void update(ServerWorld world) {
		int i = 0;
		for (Long l : Iterables.concat(FibLib.getInstance(world).blocks.values())) {
			world.getChunkManager().markForUpdate(BlockPos.fromLong(l));
			++i;
		}
		FibLib.log("Updated %d blocks", i);
	}

	// Updates all blocks being fibbed of a certain type. Use this when possible.
	public static void update(ServerWorld world, Block block) {
		FibLib instance = FibLib.getInstance(world);
		if (instance.blocks.containsKey(block)) {
			int i = 0;
			for (Long l : instance.blocks.get(block)) {
				world.getChunkManager().markForUpdate(BlockPos.fromLong(l));
				++i;
			}
			FibLib.log("Updated %d blocks", i);
		}
	}

	// Updates all blocks being fibbed that are in a list of types. Convenience.
	public static void update(ServerWorld world, Block... blocks) {
		FibLib instance = FibLib.getInstance(world);

		int i = 0;
		for (Block a : blocks) {
			if (instance.blocks.containsKey(a)) {
				for (Long l : instance.blocks.get(a)) {
					world.getChunkManager().markForUpdate(BlockPos.fromLong(l));
					++i;
				}
			}
		}

		FibLib.log("Updated %d blocks", i);
	}

	public static void update(ServerWorld world, Collection<Block> blocks) {
		FibLib instance = FibLib.getInstance(world);

		int i = 0;
		for (Block a : blocks) {
			if (instance.blocks.containsKey(a)) {
				for (Long l : instance.blocks.get(a)) {
					world.getChunkManager().markForUpdate(BlockPos.fromLong(l));
					++i;
				}
			}
		}

		FibLib.log("Updated %d blocks", i);
	}

	// Use this method if you have a world already.
	public static void register(ServerWorld world, Block block, BlockFib fib) {
		FibLib.getInstance(world).fibs.put(block, fib);
		FibLib.log("Registered a BlockFib for %s in %s", block.getTranslationKey(), world.getDimension().getType().toString());
	}

	// Use this method if you're registering Fibs before a world is available, i.e., your ModInitializer
	public static void register(DimensionType dimensionType, Block block, BlockFib fib) {
		PRE_LOAD.putIfAbsent(dimensionType, new ArrayList<>());
		PRE_LOAD.get(dimensionType).add(new Pair<>(block, fib));
		FibLib.log("Pre-loaded a BlockFib for %s in %s", block.getTranslationKey(), dimensionType.toString());
	}

	// This is what we use to actually fib things. You should *probably* never need to call this function.
	public static BlockState get(BlockState state, ServerPlayerEntity player) {
		try {
			return FibLib.getInstance(player).getWithInstance(state, player);
		} catch(NullPointerException e) {
			return state;
		}
	}

	// Registers a block to be updated on calls to update(). Automatically called in onBlockAdded(), but can be
	// used if you want to track blocks more specifically.
	public static void put(ServerWorld world, BlockPos pos) {
		FibLib.getInstance(world).putWithInstance(world.getBlockState(pos).getBlock(), pos);
	}

	// Removes a block from being updated on calls to update(). Automatically called in onBlockRemoved(), but can be
	// used if you want to track blocks more specifically.
	public static void remove(ServerWorld world, BlockPos pos) {
		FibLib.getInstance(world).removeWithInstance(world, pos);
	}
}
