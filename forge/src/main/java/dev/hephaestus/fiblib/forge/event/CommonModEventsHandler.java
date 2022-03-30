package dev.hephaestus.fiblib.forge.event;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.forge.impl.FibLootModifier;
import dev.hephaestus.fiblib.impl.LifecycleHooks;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEventsHandler {
    @SubscribeEvent
    public static void onRegisterLootModifiers(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        FibLib.debug("Registering global loot modifier");
        GlobalLootModifierSerializer<FibLootModifier> modifier = new FibLootModifier.Serializer();
        modifier.setRegistryName(FibLib.MOD_ID, "block_fib");
        event.getRegistry().register(modifier);
    }
}
