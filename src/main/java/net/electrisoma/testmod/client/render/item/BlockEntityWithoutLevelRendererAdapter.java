package net.electrisoma.testmod.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BlockEntityWithoutLevelRendererAdapter extends ItemRenderer {
    private final BlockEntityWithoutLevelRenderer wrappedRenderer;

    public BlockEntityWithoutLevelRendererAdapter(BlockEntityWithoutLevelRenderer wrappedRenderer, ItemColors itemColors) {
        super(
                Minecraft.getInstance(),
                Minecraft.getInstance().getTextureManager(),
                Minecraft.getInstance().getModelManager(),
                itemColors,
                wrappedRenderer
        );
        this.wrappedRenderer = wrappedRenderer;
    }

    @Override
    public void render(ItemStack itemStack, ItemDisplayContext displayContext, boolean leftHand,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int combinedLight, int combinedOverlay, BakedModel model) {
        wrappedRenderer.renderByItem(itemStack, displayContext, poseStack, bufferSource, combinedLight, combinedOverlay);
    }
}
