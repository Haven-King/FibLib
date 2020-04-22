package dev.hephaestus.fiblib.mixin.blocks;

import com.mojang.authlib.GameProfile;
import dev.hephaestus.fiblib.FibLib;
import io.github.ladysnake.pal.Pal;
import io.github.ladysnake.pal.VanillaAbilities;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void updateThings(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager, CallbackInfo ci) {
        if (FibLib.DEBUG) {
            Pal.getAbilitySource(FibLib.MOD_ID, "debug_flight").grantTo((ServerPlayerEntity)(Object)this, VanillaAbilities.ALLOW_FLYING);
        }
    }
}
