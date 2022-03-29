package dev.hephaestus.fiblib.fabric;

import com.qouteall.immersive_portals.Global;
import com.qouteall.immersive_portals.chunk_loading.DimensionalChunkPos;
import com.qouteall.immersive_portals.ducks.IEThreadedAnvilChunkStorage;
import dev.hephaestus.fiblib.fabric.mixin.ChunkReloader;
import dev.hephaestus.fiblib.fabric.mixin.portals.CDSMAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

@SuppressWarnings("unused")
public class ImmersivePortalsChunkManager {
    public static void resendChunks(ServerPlayer player) {
        ChunkMap TACS = player.getLevel().getChunkSource().chunkMap;
        CDSMAccessor cdsm = (CDSMAccessor) Global.chunkDataSyncManager;
        Level world = player.getLevel();

        if (world != null) {
            ResourceKey<Level> worldRegistryKey = world.dimension();

            int i = Mth.floor(player.getX()) >> 4;
            int j = Mth.floor(player.getZ()) >> 4;
            int watchDistance = ((ChunkReloader) TACS).getWatchDistance();

            for (int k = i - watchDistance; k <= i + watchDistance; ++k) {
                for (int l = j - watchDistance; l <= j + watchDistance; ++l) {
                    DimensionalChunkPos chunkPos = new DimensionalChunkPos(worldRegistryKey, k, l);
                    cdsm.invokeSendChunkDataPacketNow(player, chunkPos, (IEThreadedAnvilChunkStorage) TACS);
                }
            }
        }
    }
}
