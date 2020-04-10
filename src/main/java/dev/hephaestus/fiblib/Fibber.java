package dev.hephaestus.fiblib;

import net.minecraft.server.network.ServerPlayerEntity;

public interface Fibber {
    static Fibber fix(Object object, ServerPlayerEntity player) {
        Fibber fixed = ((Fibber) object);
        fixed.fix(player);
        return fixed;
    }

    void fix(ServerPlayerEntity player);
}
