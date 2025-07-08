package net.electrisoma.testmod.registry.items.util.neoforge;

import com.mojang.blaze3d.vertex.PoseStack;
import net.electrisoma.testmod.client.render.item.CustomRenderedItemModelRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.electrisoma.testmod.registry.items.util.HasCustomRenderer;

import java.util.function.Consumer;

public class HasCustomRendererImpl {
    public static void initializeClient(Consumer<Object> consumer, HasCustomRenderer.RendererProvider provider) {
        BlockEntityWithoutLevelRenderer wrapperRenderer = new BlockEntityWithoutLevelRenderer(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()) {
            @Override
            public void renderByItem(ItemStack stack, ItemDisplayContext context,
                                     PoseStack poseStack, MultiBufferSource buffer,
                                     int light, int overlay) {
                Object renderer = provider.provideRenderer();
                if (renderer instanceof CustomRenderedItemModelRenderer customRenderer) {
                    customRenderer.render(stack, null, null, context, poseStack, buffer, light, overlay);
                }
            }
        };

        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return wrapperRenderer;
            }
        });
    }
}
