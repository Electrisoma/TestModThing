package net.electrisoma.testmod.client.render.item;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomRenderedItemModel implements BakedModel {
    private final BakedModel originalModel;

    public CustomRenderedItemModel(BakedModel originalModel) {
        this.originalModel = originalModel;
    }

    @Override
    public boolean isCustomRenderer() {
        return true;
    }


    @Override
    public boolean usesBlockLight() {
        return originalModel.usesBlockLight();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        return List.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return originalModel.isGui3d();
    }

    @Override
    public ItemTransforms getTransforms() {
        return originalModel.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return originalModel.getOverrides();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return originalModel.getParticleIcon();
    }
}
