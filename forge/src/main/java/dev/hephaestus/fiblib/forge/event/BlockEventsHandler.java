package dev.hephaestus.fiblib.forge.event;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.impl.LookupImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockEventsHandler {
    // Prevent right-clicking on fibbed blocks
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        FibLib.debug("Intercepting RightClick");
        Player player = event.getPlayer();
        BlockState target = event.getWorld().getBlockState(event.getPos());
        // @TODO: Lenient blocks are not revealed here
        BlockState newState = LookupImpl.findState(target, player, false);
        if (newState != target) {
            InteractionResult result = newState.use(player.getCommandSenderWorld(), player, event.getHand(), event.getHitVec());
            event.setUseBlock(result == InteractionResult.SUCCESS ? Event.Result.ALLOW : Event.Result.DENY);
        }
    }

    // Sync break speed calculations with the fibbed block
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        FibLib.debug("Intercepting BreakSpeed");
        Player player = event.getPlayer();
        // @TODO: Lenient blocks are not revealed here
        BlockState newState = LookupImpl.findState(event.getState(), player, false);
        if (newState != event.getState()) {
            event.setNewSpeed(ForgeEventFactory.getBreakSpeed(event.getPlayer(), newState, event.getOriginalSpeed(), event.getPos()));
        }
    }

    // Change EXP dropped from breaking block to that of fibbed block
    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        FibLib.debug("Intercepting BreakBlock");
        Player player = event.getPlayer();
        // @TODO: Lenient blocks are not revealed here
        BlockState newState = LookupImpl.findState(event.getState(), player, false);
        if (newState != event.getState()) {
            int bonusLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, player.getMainHandItem());
            int silkLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, player.getMainHandItem());

            event.setExpToDrop(newState.getExpDrop(player.getCommandSenderWorld(), event.getPos(), bonusLevel, silkLevel));
        }
    }

    // Change harvest check to match fibbed block
    @SubscribeEvent
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        FibLib.debug("Intercepting HarvestCheck");
        Player player = event.getPlayer();
        // @TODO: Lenient blocks are not revealed here
        BlockState newState = LookupImpl.findState(event.getTargetBlock(), player, false);
        if (newState != event.getTargetBlock()) {
            event.setCanHarvest(newState.canHarvestBlock(player.getCommandSenderWorld(), new BlockPos(0, -1, 0), player));
        }
    }
}
