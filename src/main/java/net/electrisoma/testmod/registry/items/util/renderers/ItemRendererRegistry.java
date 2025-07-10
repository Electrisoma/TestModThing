package net.electrisoma.testmod.registry.items.util.renderers;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ItemRendererRegistry {
    private static final Map<Item, BlockEntityWithoutLevelRenderer> RENDERER_MAP = new IdentityHashMap<>();

    public static void register(Item item, Supplier<? extends BlockEntityWithoutLevelRenderer> bewlrFactory) {
        RENDERER_MAP.put(item, bewlrFactory.get());
    }

    public static BlockEntityWithoutLevelRenderer get(Item item) {
        return RENDERER_MAP.get(item);
    }

    public static Map<Item, BlockEntityWithoutLevelRenderer> build() {
        return RENDERER_MAP;
    }
}
