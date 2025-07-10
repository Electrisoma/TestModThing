package net.electrisoma.testmod.registry.items.util.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractItemBEWLR extends BlockEntityWithoutLevelRenderer {
    private AbstractItemRenderer renderer;

    public AbstractItemBEWLR() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    protected abstract AbstractItemRenderer createRenderer();

    private AbstractItemRenderer getRenderer() {
        if (renderer == null)
            renderer = createRenderer();
        return renderer;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context,
                             PoseStack poseStack, MultiBufferSource buffer,
                             int light, int overlay) {
        getRenderer().render(stack, context, poseStack, buffer, light, overlay);
    }
}
