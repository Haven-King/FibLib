package dev.hephaestus.fiblib.blocks;

import dev.hephaestus.fiblib.FibLib;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

// Again, thanks LibCD
public class FibScriptLoader implements SimpleResourceReloadListener {
    public static final Map<BlockState, ScriptedBlockFib> FIBS = new HashMap<>();
    public static final ScriptEngineManager SCRIPT_MANAGER = new ScriptEngineManager();

    @Override
    public CompletableFuture load(ResourceManager resourceManager, Profiler profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Collection<Identifier> resources = resourceManager.findResources("fibbers", name -> true);
            for (Identifier file: resources) {
                int localPath = file.getPath().indexOf('/') + 1;
                Identifier script = new Identifier(file.getNamespace(), file.getPath().substring(localPath));
                try {
                    Resource r = resourceManager.getResource(file);
                    String scriptText = IOUtils.toString(r.getInputStream(), StandardCharsets.UTF_8);
                    String extension = script.getPath().substring(script.getPath().lastIndexOf('.') + 1);
                    ScriptEngine engine = SCRIPT_MANAGER.getEngineByExtension(extension);
                    if (engine == null) {
                        FibLib.log("Engine for fibber not found: %s", script.toString());
                        continue;
                    }
                    ScriptedBlockFib bridge = new ScriptedBlockFib(engine, scriptText, script);
                    FIBS.put(bridge.input(), bridge);
                } catch (IOException e) {
                    FibLib.log("Couldn't access file %s: ", file.toString(), e.getMessage());
                }
            }

            FibLib.log("Loaded %d scripts", FIBS.size());

            return FIBS;
        });
    }

    @Override
    public CompletableFuture<Void> apply(Object o, ResourceManager resourceManager, Profiler profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            for (Map.Entry<BlockState, ScriptedBlockFib> e : FIBS.entrySet()) {
                FibLib.Blocks.register(e.getKey(), e.getValue());
                FibLib.Blocks.update();
            }
        });
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(FibLib.MOD_ID, "fibber_loader");
    }
}
