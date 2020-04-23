package dev.hephaestus.fiblib.blocks;

import dev.hephaestus.fiblib.FibLib;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RayTraceContext;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

// I am blatantly copying much of this from LibCD. Yay MIT!
public class ScriptedBlockFib implements BlockFib {
    private final ScriptEngine engine;
    private final String scriptText;
    private final Identifier id;
    private BlockState input;

    public ScriptedBlockFib(ScriptEngine engine, String scriptText, Identifier id) {
        this.engine = engine;
        this.scriptText = scriptText;
        this.id = id;
    }

    public BlockState output(BlockState state, ServerPlayerEntity player, BlockPos pos) {
        ScriptContext ctx = engine.getContext();
        ctx.setAttribute("fiblib", new Data(state, player, pos), ScriptContext.ENGINE_SCOPE);

        try {
            engine.eval(scriptText);
            return (BlockState)((Invocable)engine).invokeFunction("getOutput");
        } catch (ScriptException | NoSuchMethodException e) {
            FibLib.log("Error getting output from Script %s: %s", this.id.toString(), e.getMessage());
            return state;
        }
    }

    /**
     * Gets the input. Only called on registering the script. Don't call this.
     * @return The BlockState of the block we're fibbing.
     */
    public BlockState input() {
        if (input != null) return input;

        ScriptContext ctx = engine.getContext();
        ctx.setAttribute("fiblib", new Data(), ScriptContext.ENGINE_SCOPE);

        try {
            engine.eval(scriptText);
            input = (BlockState)((Invocable)engine).invokeFunction("getInput");
        } catch (ScriptException | NoSuchMethodException e) {
            FibLib.log("Error getting input from Script %s: %s", this.id.toString(), e.getMessage());
        }

        return input;
    }

    @Override
    public BlockState get(BlockState state, ServerPlayerEntity player, BlockPos pos) {
        BlockState out = output(state, player, pos);
        return out == null ? state : out;
    }

    public static class Data {
        public static final boolean debug = FibLib.DEBUG;
        public final ServerPlayerEntity player;
        public final BlockState inState;
        public final BlockPos blockPos;

        private static Vec3d getLooking(ServerPlayerEntity player) {
            float f = -MathHelper.sin(player.yaw * 0.017453292F) * MathHelper.cos(player.pitch * 0.017453292F);
            float g = -MathHelper.sin(player.pitch * 0.017453292F);
            float h = MathHelper.cos(player.yaw * 0.017453292F) * MathHelper.cos(player.pitch * 0.017453292F);

            return new Vec3d(f,g,h);
        }

        public boolean isLookingAt(int range) {
            if (player == null || blockPos == null) return false;

            BlockHitResult result = player.world.rayTrace(new RayTraceContext(
                    player.getCameraPosVec(1.0f),
                    player.getCameraPosVec(1.0f).add(getLooking(player).multiply(range)),
                    RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.NONE, player
            ));

            return result != null && blockPos.equals(result.getBlockPos());
        }


        protected Data(BlockState inState, ServerPlayerEntity player, BlockPos pos) {
            this.player = player;
            this.inState = inState;
            this.blockPos = pos;
        }

        protected Data() {
            this.player = null;
            this.inState = null;
            this.blockPos = null;
        }

        public BlockState getDefaultBlockstate(String id) {
            return Registry.BLOCK.get(new Identifier(id)).getDefaultState();
        }

        public void log(String msg) {
            FibLib.log(msg);
        }
    }
}