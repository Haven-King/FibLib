package dev.hephaestus.fiblib.blocks;

import javax.annotation.Nullable;

import dev.hephaestus.fiblib.FibLib;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

// I am blatantly copying much of this from LibCD. Yay MIT!
public class BlockFib {
    private final ScriptEngine engine;
    private final String scriptText;
    private final Identifier id;
    private boolean hasRun = false;
    private boolean hasErrored = false;
    private BlockState input;

    public BlockFib(ScriptEngine engine, String scriptText, Identifier id) {
        this.engine = engine;
        this.scriptText = scriptText;
        this.id = id;
    }

    public BlockState getDefaultBlockstate(String id) {
        return Registry.BLOCK.get(new Identifier(id)).getDefaultState();
    }

    public void log(String msg) {
        FibLib.log(msg);
    }

    public BlockState run(BlockState state, ServerPlayerEntity player) {
        if (hasErrored) return state;

        ScriptContext ctx = engine.getContext();
        ctx.setAttribute("fiblib", this, ScriptContext.ENGINE_SCOPE);
        ctx.setAttribute("player", player, ScriptContext.ENGINE_SCOPE);

        try {
            engine.eval(scriptText);
        } catch (ScriptException e) {
            if (!hasErrored) {
                hasErrored = true;
                FibLib.log("Failed to execute script %s: ", id.toString(), e.getMessage());
            }
        }

        if (!hasErrored) {
            hasRun = true;
            return (BlockState) engine.getBindings(ScriptContext.ENGINE_SCOPE).get("outState");
        } else {
            return state;
        }
    }

    /**
     * Gets the input. Only called on registering the script. Don't call this.
     * @return The BlockState of the block we're fibbing.
     */
    public BlockState input() {
        try {
            engine.eval(scriptText);
        } catch (ScriptException ignored) {

        }

        return (BlockState) engine.getBindings(ScriptContext.ENGINE_SCOPE).get("inState");
    }
}
