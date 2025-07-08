package net.electrisoma.testmod.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class PartialItemModelRenderer {

    private static final PartialItemModelRenderer INSTANCE = new PartialItemModelRenderer();
    private final RandomSource random = RandomSource.create();

    private ItemStack stack;
    private ItemDisplayContext transformType;
    private PoseStack ms;
    private MultiBufferSource buffer;
    private int overlay;

    public PartialItemModelRenderer() {}

    public static PartialItemModelRenderer of(ItemStack stack, ItemDisplayContext transformType,
                                              PoseStack ms, MultiBufferSource buffer, int overlay) {
        PartialItemModelRenderer instance = INSTANCE;
        instance.stack = stack;
        instance.transformType = transformType;
        instance.ms = ms;
        instance.buffer = buffer;
        instance.overlay = overlay;
        return instance;
    }

    public void render(BakedModel model, int light) {
        render(model, Sheets.translucentCullBlockSheet(), light);
    }

    public void renderSolid(BakedModel model, int light) {
        render(model, Sheets.solidBlockSheet(), light);
    }

    public void render(BakedModel model, RenderType type, int light) {
        if (stack.isEmpty()) return;

        ms.pushPose();
        ms.translate(-0.5D, -0.5D, -0.5D);

        if (!model.isCustomRenderer()) {
            VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(buffer, type, true, stack.hasFoil());
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

            for (Direction direction : Direction.values()) {
                random.setSeed(42L);
                List<BakedQuad> quads = model.getQuads(null, direction, random);
                itemRenderer.renderQuadList(ms, vertexConsumer, quads, stack, light, overlay);
            }

            random.setSeed(42L);
            List<BakedQuad> quads = model.getQuads(null, null, random);
            itemRenderer.renderQuadList(ms, vertexConsumer, quads, stack, light, overlay);

        } else {
            CustomRenderedItemModelRenderer renderer = getCustomRendererForStack(stack);
            if (renderer != null) {
                renderer.renderByItem(stack, transformType, ms, buffer, light, overlay);
            }
        }

        ms.popPose();
    }

    private CustomRenderedItemModelRenderer getCustomRendererForStack(ItemStack stack) {
        return null;
    }
}
