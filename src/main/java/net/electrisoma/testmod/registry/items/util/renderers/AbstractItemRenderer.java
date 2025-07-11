package net.electrisoma.testmod.registry.items.util.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public abstract class AbstractItemRenderer extends BlockEntityWithoutLevelRenderer {
    public AbstractItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    protected abstract PartialModel getMainModel();

    protected void renderOverlay(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack,
                                 MultiBufferSource bufferSource, int light, int overlay) {}

    protected void applyTransform(ItemDisplayContext ctx, PoseStack poseStack, ItemStack stack) {}

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack,
                             MultiBufferSource bufferSource, int light, int overlay) {
        poseStack.pushPose();

        applyTransform(ctx, poseStack, stack);

        CachedBuffers.partial(getMainModel(), Blocks.AIR.defaultBlockState())
                .light(light)
                .overlay(overlay)
                .renderInto(poseStack, bufferSource.getBuffer(RenderType.cutout()));

        renderOverlay(stack, ctx, poseStack, bufferSource, light, overlay);

        poseStack.popPose();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        super.onResourceManagerReload(resourceManager);
    }
}
