package dev.hephaestus.fiblib.fabric;

import dev.hephaestus.fiblib.Platform;
import dev.hephaestus.fiblib.api.BlockFib;
import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.fabric.mixin.ChunkReloader;
import dev.hephaestus.fiblib.FibLib;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Iterator;
import java.util.UUID;

@SuppressWarnings("unused")
public class PlatformImpl extends Platform {
    public static void resendChunks(ServerPlayer player) {
        if (FabricLoader.getInstance().isModLoaded("immersive_portals")) {
            ImmersivePortalsChunkManager.resendChunks(player);
        } else {
            ChunkMap TACS = player.getLevel().getChunkSource().chunkMap;
            ((ChunkReloader) TACS).reloadChunks(player, true);
        }
    }
}
