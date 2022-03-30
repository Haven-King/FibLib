package dev.hephaestus.fiblib.forge.network;

import dev.hephaestus.fiblib.FibLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ServerPacketHandler {
    private static ServerPacketHandler instance;

    private static final String PROTOCOL_VERSION = "1";

    private static int PacketId = 1;

    private SimpleChannel channel;

    @SubscribeEvent
    public static void onCommonSetupEvent(FMLCommonSetupEvent event) {
        FibLib.debug("Setting up packer handler");
        instance = new ServerPacketHandler();
    }

    public static ServerPacketHandler getInstance() {
        return instance;
    }

    private ServerPacketHandler() {
        FibLib.debug("Registering Channel");
        this.channel = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(FibLib.MOD_ID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
        createReloadPacket();
    }

    private void createReloadPacket() {
        FibLib.debug("Registering ReloadPacket message");
        channel.registerMessage(PacketId++, ReloadPacket.class, ReloadPacket::encode, ReloadPacket::decode, ClientPacketHandler::handleReloadPacket);
    }

    public void sendReloadPacket(ServerPlayer player) {
        FibLib.debug("Sending ReloadPacket to %s", player.getName().getString());
        channel.send(PacketDistributor.PLAYER.with(() -> player), new ReloadPacket());
    }
}
