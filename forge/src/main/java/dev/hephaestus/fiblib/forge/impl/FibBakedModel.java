package dev.hephaestus.fiblib.forge.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import dev.hephaestus.fiblib.api.BlockFib;
import dev.hephaestus.fiblib.impl.LookupImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class FibBakedModel implements BakedModel {
    private final BlockState originalState;
    private final BakedModel originalModel;
    private final BlockFib blockFib;

    public FibBakedModel(BlockState state, @Nullable BakedModel model, BlockFib fib) {
        blockFib = fib;
        originalState = state;
        originalModel = model == null ? getModelForState(state) : model;
    }

    private BakedModel getModelForState(BlockState state) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
    }

    private BlockState getFibState(@Nullable BlockState passedState) {
        Player player = Minecraft.getInstance().player;

        BlockState givenState = passedState == null || passedState == originalState ? originalState : passedState;

        return LookupImpl.findState(givenState, player);
    }

    private BakedModel getFibModel() {
        return getFibModel(null);
    }

    private BakedModel getFibModel(@Nullable BlockState passedState) {
        BlockState actualState = getFibState(passedState);

        return (actualState == originalState) ? originalModel : getModelForState(actualState);
    }

    // Proxy public methods to the underlying model

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction arg2, Random random) {
        BlockState actualState = getFibState(state);
        return getFibModel(state).getQuads(actualState, arg2, random);
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull IModelData extraData) {
        BlockState actualState = getFibState(state);
        return getFibModel(state).getQuads(actualState, side, rand, extraData);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return getFibModel().useAmbientOcclusion();
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state) {
        BlockState actualState = getFibState(state);
        return getFibModel(state).isAmbientOcclusion(actualState);
    }

    @Override
    public boolean isGui3d() {
        return getFibModel().isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return getFibModel().usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return getFibModel().isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return getFibModel().getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return getFibModel().getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return getFibModel().getOverrides();
    }


    @Override
    public BakedModel getBakedModel() {
        return getFibModel().getBakedModel();
    }

    @Override
    public boolean doesHandlePerspectives() {
        return getFibModel().doesHandlePerspectives();
    }

    @Override
    public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack mat) {
        return getFibModel().handlePerspective(cameraTransformType, mat);
    }

    @NotNull
    @Override
    public IModelData getModelData(@NotNull BlockAndTintGetter world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull IModelData tileData) {
        BlockState actualState = getFibState(state);
        return getFibModel(state).getModelData(world, pos, actualState, tileData);
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@NotNull IModelData data) {
        return getFibModel().getParticleTexture(data);
    }

    @Override
    public boolean isLayered() {
        return getFibModel().isLayered();
    }

    @Override
    public List<Pair<BakedModel, RenderType>> getLayerModels(ItemStack itemStack, boolean fabulous) {
        return getFibModel().getLayerModels(itemStack, fabulous);
    }
}
