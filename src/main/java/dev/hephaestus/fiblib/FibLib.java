package dev.hephaestus.fiblib;

import dev.hephaestus.fiblib.blocks.BlockFib;
import dev.hephaestus.fiblib.blocks.BlockTracker;
import dev.hephaestus.fiblib.blocks.LookupTable;
import dev.hephaestus.fiblib.items.ItemFib;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.ChunkComponentCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
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
		FibLib.log("Initialized");
	}

	public static class Blocks {
		public static final ComponentType<BlockTracker> TRACKER =
				ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier("fiblib:blocks.tracker"), BlockTracker.class);

		static {
			ChunkComponentCallback.EVENT.register((chunk, components) -> components.put(TRACKER, new BlockTracker(chunk)));
		}

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

	public static class Items {
		private static final Map<Item, ItemFib> FIBS = new HashMap<>();

		/**
		 * Registers an ItemFib for the specified item.
		 *
		 * @param item	item to register for.
		 * @param fib   the fib itself
		 */
		public static void register(Item item, ItemFib fib) {
			FIBS.put(item, fib);
			FibLib.log("Registered an ItemFib for %s", item.getTranslationKey());
		}

		/**
		 * Returns the result of any fibs on a given BlockState
		 *
		 * @param input  the ItemStack block we're inquiring about. Note that because this is passed to a ItemFib, other
		 *               aspects of ItemStack, like nbt, may be used in determining the output
		 * @return the result of the fib. This is what the player will get told the item is.
		 */
		public static ItemStack get(ItemStack input) {
			Item inputItem = input.getItem();
			if (!FIBS.containsKey(inputItem)) return input;

			ItemFib fib = FIBS.get(inputItem);

			return fib.getOutput(input);
		}

		/**
		 * Returns whether or not a fib exists for the given item.
		 *
		 * @param item the item to inquire about
		 * @return true if the item has a fib
		 */
		public static boolean contains(Item item) {
			return FIBS.containsKey(item);
		}
	}
}
