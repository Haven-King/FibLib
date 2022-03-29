package dev.hephaestus.fiblib.forge.network;

import dev.hephaestus.fiblib.FibLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.resource.VanillaResourceType;

import java.util.Optional;
import java.util.function.Supplier;

public class ClientPacketHandler {
    public static void handleReload(ReloadPacket msg) {
        FibLib.log("Reloading client (%s)", msg.getCount());
        ForgeHooksClient.refreshResources(Minecraft.getInstance(), VanillaResourceType.MODELS);
    }

    public static void handleReloadPacket(ReloadPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        LogicalSide sideReceived = context.getDirection().getReceptionSide();

        FibLib.log("Routing ReloadPacket destined for %s", sideReceived);

        if (sideReceived != LogicalSide.CLIENT) {
          FibLib.log("TargetEffectMessageToClient received on wrong side: %s", sideReceived);
          return;
        }

        FibLib.log("ReloadPacket is correctly destined for %s", sideReceived);

        Optional<ClientLevel> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
        if (!clientWorld.isPresent()) {
            FibLib.log("TargetEffectMessageToClient context could not provide a ClientWorld.");
            return;
        }

        FibLib.log("Enqueueing ReloadPacket handler");
        context.enqueueWork(() -> handleReload(msg));
        context.setPacketHandled(true);
    }
}
