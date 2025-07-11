package net.electrisoma.testmod.registry.items.util;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;

public interface ClientRenderableItem {
    BlockEntityWithoutLevelRenderer createRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet models);
}