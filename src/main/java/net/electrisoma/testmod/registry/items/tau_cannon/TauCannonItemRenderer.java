package net.electrisoma.testmod.registry.items.tau_cannon;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.electrisoma.testmod.TestMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TauCannonItemRenderer extends BlockEntityWithoutLevelRenderer {
    protected static final PartialModel DRUM = PartialModel.of(TestMod.path("item/tau_cannon/drum"));

    public TauCannonItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet entityModelSet) {
        super(dispatcher, entityModelSet);
        System.out.println("TauCannonItemRenderer constructor called");
    }

    public static void renderItem(ItemStack stack, ItemDisplayContext context, PoseStack ms,
                                  MultiBufferSource buffer, int light, int overlay) {
        float time = AnimationTickHolder.getRenderTime();
        float angle = (time * 10f) % 360f;

        ms.pushPose();
        ms.translate(0.5, 0.5, 0.5);
        ms.scale(1.5f, 1.5f, 1.5f);

        BakedModel model = DRUM.get();
        if (model != null) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            itemRenderer.render(stack, ItemDisplayContext.GUI, false, ms, buffer, light, overlay, model);
        }

        ms.popPose();
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context,
                             PoseStack ms, MultiBufferSource buffer,
                             int light, int overlay) {
        System.out.println("TauCannonItemRenderer.renderByItem called");
        renderItem(stack, context, ms, buffer, light, overlay);
    }
}