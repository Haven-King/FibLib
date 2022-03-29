package dev.hephaestus.fiblib.forge.event;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.api.BlockFib;
import dev.hephaestus.fiblib.api.BlockFibRegistry;
import dev.hephaestus.fiblib.forge.FibBakedModel;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventsHandler {
    // Inject BakedModels to override default models
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onModelBake(ModelBakeEvent event) {
        Collection<Map.Entry<BlockState, BlockFib>> fibs = BlockFibRegistry.getAll();
        FibLib.log("Injecting %s BakedModel fibs", fibs.size());

        if (fibs.size() == 0) {
            return;
        }

        Map<ResourceLocation, BakedModel> modelRegistry = event.getModelRegistry();
        for (Map.Entry<BlockState, BlockFib> entry : fibs) {
            BlockState blockState = entry.getKey();
            ModelResourceLocation resourceLocation = BlockModelShaper.stateToModelLocation(blockState);
            BakedModel existingModel = modelRegistry.get(resourceLocation);

            if (existingModel == null) {
                FibLib.log("No model found for %s", blockState.getBlock().getName().getString());
            } else if (existingModel instanceof FibBakedModel) {
                FibLib.log("Already replaced %s", blockState.getBlock().getName().getString());
                return;
            }
            FibLib.log("Registering fib model for %s", blockState.getBlock().getName().getString());
            modelRegistry.put(resourceLocation, new FibBakedModel(blockState, existingModel, entry.getValue()));
        }
        ;
    }
}
