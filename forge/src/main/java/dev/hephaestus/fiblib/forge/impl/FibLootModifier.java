package dev.hephaestus.fiblib.forge.impl;

import com.google.gson.JsonObject;
import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.impl.LookupImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class FibLootModifier extends LootModifier {
    public FibLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> itemStack, LootContext ctx) {
        // Not a block drop, so change nothing
        for (LootContextParam<?> required : LootContextParamSets.BLOCK.getRequired()) {
            if (!ctx.hasParam(required)) {
                return itemStack;
            }
        }

        // Not a player, so change nothing
        Entity entity = ctx.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof Player)) {
            return itemStack;
        }

        // ParamSet check above verified the params exists
        BlockState blockState = ctx.getParamOrNull(LootContextParams.BLOCK_STATE);
        assert blockState != null;


        FibLib.debug("Checking for loot modifications to %s for %s", blockState.getBlock().getName().getString(), entity.getName().getString());
        BlockState newState = LookupImpl.findDropsState(blockState, (Player) entity);

        // No fib, so change nothing
        if (newState == blockState) {
            FibLib.debug("No loot modifications found on %s for %s", blockState.getBlock().getName().getString(), entity.getName().getString());
            return itemStack;
        }

        // Generate Loot Table for block fib and use that instead
        Level level = ctx.getLevel();
        LootContext newContext = new LootContext.Builder(ctx)
                .withRandom(level.random)
                .withParameter(LootContextParams.ORIGIN, ctx.getParamOrNull(LootContextParams.ORIGIN))
                .withParameter(LootContextParams.TOOL, ctx.getParamOrNull(LootContextParams.TOOL))
                .withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, ctx.getParamOrNull(LootContextParams.BLOCK_ENTITY))
                .withParameter(LootContextParams.BLOCK_STATE, newState)
                .create(LootContextParamSets.BLOCK);

        LootTable lootTable = Objects.requireNonNull(level.getServer()).getLootTables().get(newState.getBlock().getLootTable());
        FibLib.debug("Replacing loot on %s to %s for %s", blockState.getBlock().getName().getString(), newState.getBlock().getName().getString(), entity.getName().getString());

        return lootTable.getRandomItems(newContext);
    }

    public static class Serializer extends GlobalLootModifierSerializer<FibLootModifier> {
        @Override
        public FibLootModifier read(ResourceLocation name, JsonObject json, LootItemCondition[] conditionsIn) {
            return new FibLootModifier(conditionsIn);
        }

        @Override
        public JsonObject write(FibLootModifier instance) {
            return makeConditions(instance.conditions);
        }
    }
}
