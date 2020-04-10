package dev.hephaestus.fiblib;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.PersistentState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class FibLib extends PersistentState {
	public static final String MOD_ID = "fiblib";
	private static final String MOD_NAME = "FibLib";

	private static final Logger LOGGER = LogManager.getLogger();
	private static final String SAVE_KEY = "fiblib";
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

	private static FibLib getInstance(ServerPlayerEntity player) {
		return getInstance(player.getServerWorld());
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
	public void putWithInstance(Block block, BlockPos pos) {
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
	public static void update(ServerWorld world) {
		int i = 0;
		for (Long l : Iterables.concat(FibLib.getInstance(world).blocks.values())) {
			world.getChunkManager().markForUpdate(BlockPos.fromLong(l));
			++i;
		}
		FibLib.log("Updated %d blocks", i);
	}

	public static void register(ServerWorld world, Block block, BlockFib fib) {
		FibLib.getInstance(world).fibs.put(block, fib);
	}

	public static BlockState get(BlockState state, ServerPlayerEntity player) {
		try {
			return FibLib.getInstance(player).getWithInstance(state, player);
		} catch(NullPointerException e) {
			return state;
		}
	}

	public static void put(ServerWorld world, BlockPos pos) {
		FibLib.getInstance(world).putWithInstance(world.getBlockState(pos).getBlock(), pos);
	}

	public static void remove(ServerWorld world, BlockPos pos) {
		FibLib.getInstance(world).removeWithInstance(world, pos);
	}
}
