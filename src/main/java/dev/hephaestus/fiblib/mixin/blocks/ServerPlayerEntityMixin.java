package dev.hephaestus.fiblib.mixin.blocks;

import com.mojang.authlib.GameProfile;
import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.blocks.Tester;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Shadow
    public ServerWorld getServerWorld() {return null;}

    @Inject(method = "<init>", at = @At("TAIL"))
    public void updateThings(MinecraftServer server, ServerWorld world, GameProfile profile, ServerPlayerInteractionManager interactionManager, CallbackInfo ci) {
        FibLib.Blocks.update(world);
    }

    @Inject(method = "setGameMode", at = @At("TAIL"))
    public void setGamemodeInjection(GameMode gameMode, CallbackInfo ci) {
        if (Tester.DEBUG) FibLib.Blocks.update(this.getServerWorld());
    }
}
