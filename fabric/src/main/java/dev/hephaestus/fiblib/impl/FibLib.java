package dev.hephaestus.fiblib.impl;

import com.qouteall.immersive_portals.Global;
import com.qouteall.immersive_portals.chunk_loading.DimensionalChunkPos;
import com.qouteall.immersive_portals.ducks.IEThreadedAnvilChunkStorage;
import dev.hephaestus.fiblib.mixin.CDSMAccessor;
import dev.hephaestus.fiblib.mixin.ChunkReloader;
import dev.hephaestus.fiblib.mixin.TACSAccessor;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class FibLib {
    public static final boolean DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment();

    public static final ThreadLocal<ServerPlayerEntity> PLAYER = new ThreadLocal<>();

	public static void resendChunks(ServerPlayerEntity player) {
		ThreadedAnvilChunkStorage TACS = ((TACSAccessor) player.getServerWorld().getChunkManager())
				.getThreadedAnvilChunkStorage();


		if (FabricLoader.getInstance().isModLoaded("immersive_portals")) {
			CDSMAccessor cdsm = (CDSMAccessor) Global.chunkDataSyncManager;
			World world = player.getServerWorld();

			if (world != null) {
				RegistryKey<World> worldRegistryKey = world.getRegistryKey();

				int i = MathHelper.floor(player.getX()) >> 4;
				int j = MathHelper.floor(player.getZ()) >> 4;
				int watchDistance = ((ChunkReloader) TACS).getWatchDistance();

				for (int k = i - watchDistance; k <= i + watchDistance; ++k) {
					for (int l = j - watchDistance; l <= j + watchDistance; ++l) {
						DimensionalChunkPos chunkPos = new DimensionalChunkPos(worldRegistryKey, k, l);
						cdsm.invokeSendChunkDataPacketNow(player, chunkPos, (IEThreadedAnvilChunkStorage) TACS);
					}
				}
			}
		} else {

			((ChunkReloader) TACS).reloadChunks(player, true);
		}
	}
}
