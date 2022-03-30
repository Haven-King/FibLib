package dev.hephaestus.fiblib.forge;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.impl.LifecycleHooks;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(FibLib.MOD_ID)
public class FibLibForge {
    public FibLibForge() {
        FibLib.debug("Loaded");
        LifecycleHooks.handleInit();
    }
}
