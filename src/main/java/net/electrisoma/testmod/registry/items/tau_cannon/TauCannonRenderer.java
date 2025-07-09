package net.electrisoma.testmod.registry.items.tau_cannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.electrisoma.testmod.TestMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class TauCannonRenderer {

    protected static final PartialModel DRUM = PartialModel.of(TestMod.path("item/tau_cannon/drum"));

    public static void renderItem(ItemStack stack, PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        assert mc.level != null;
        float angle = mc.level.getGameTime() % 360;

        poseStack.pushPose();

        poseStack.mulPose(new Matrix4f().rotateY((float) Math.toRadians(angle)));

        BakedModel bakedModel = DRUM.get();

        if (bakedModel != null) {
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.solid());

            mc.getBlockRenderer().getModelRenderer().renderModel(
                    poseStack.last(),
                    buffer,
                    null,
                    bakedModel,
                    1f, 1f, 1f,
                    light,
                    overlay
            );
        }

        poseStack.popPose();
    }

}
