package net.electrisoma.testmod.registry.items.util.renderers;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;

public interface ItemRendererRegistrar {
    void register(Item item, BlockEntityWithoutLevelRenderer renderer);
}
