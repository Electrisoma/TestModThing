package net.electrisoma.testmod.registry.items.tau_cannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.electrisoma.testmod.client.render.TestPartials;
import net.electrisoma.testmod.registry.items.util.renderers.AbstractItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class TauCannonItemRenderer extends AbstractItemRenderer {
    private static final Map<Integer, Float> angleMap = new HashMap<>();
    private static final Map<Integer, Float> speedMap = new HashMap<>();
    private static final Map<Integer, Long> lastTimeMap = new HashMap<>();

    @Override
    protected PartialModel getMainModel() {
        return TestPartials.TAU_CANNON_ITEM;
    }

    private PartialModel getDrumModel() {
        return TestPartials.TAU_CANNON_DRUM;
    }

    @Override
    protected void applyTransform(ItemDisplayContext ctx, PoseStack poseStack) {
        switch (ctx) {
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> {
                poseStack.translate(-0.125, 0.01125, -0.3334);
                poseStack.scale(1.25f, 1.25f, 1.25f);
            }
            case FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> {
                poseStack.translate(-0.05, 0.25, 0.125);
                poseStack.scale(1.0f, 1.0f, 1.0f);
            }
            case GUI -> {
                poseStack.translate(1.425, -0.1, -0.3);
                poseStack.mulPose(Axis.XP.rotationDegrees(27));
                poseStack.mulPose(Axis.YP.rotationDegrees(-135));
                poseStack.scale(1.4f, 1.4f, 1.4f);
            }
            case FIXED -> poseStack.mulPose(Axis.YP.rotationDegrees(90));
            default -> poseStack.scale(1.0f, 1.0f, 1.0f);
        }
    }

    @Override
    protected void renderOverlay(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack,
                                 MultiBufferSource bufferSource, int light, int overlay) {
        if (ctx == ItemDisplayContext.GUI) {
            poseStack.pushPose();
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.translate(-0.5, -0.5, -0.5);
            renderDrum(poseStack, bufferSource, light, overlay, 0f);
            poseStack.popPose();
            return;
        }

        float angle = updateRotation(stack);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.53125);
        poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
        poseStack.translate(-0.5, -0.5, -0.53125);
        renderDrum(poseStack, bufferSource, light, overlay, angle);
        poseStack.popPose();
    }

    private void renderDrum(PoseStack poseStack, MultiBufferSource bufferSource,
                            int light, int overlay, float angle) {
        var drumBuf = CachedBuffers.partial(getDrumModel(), Blocks.AIR.defaultBlockState());
        drumBuf.light(light).overlay(overlay).renderInto(poseStack, bufferSource.getBuffer(RenderType.cutout()));
    }

    private float updateRotation(ItemStack stack) {
        int key = System.identityHashCode(stack);
        long now = System.currentTimeMillis();
        long last = lastTimeMap.getOrDefault(key, now);
        float speed = speedMap.getOrDefault(key, 0f);
        float angle = angleMap.getOrDefault(key, 0f);
        float dt = (now - last) / 1000f;

        Minecraft mc = Minecraft.getInstance();
        float targetSpeed = 0f;
        boolean spinning = false;

        if (mc.player != null && mc.player.isUsingItem() && mc.player.getUseItem() == stack) {
            int charge = stack.getUseDuration(mc.player) - mc.player.getUseItemRemainingTicks();
            float ramp = Math.min(charge, 40) / 40f;
            targetSpeed = 60f + ramp * 500f;
            spinning = true;
        }

        if (spinning) speed = targetSpeed;
        else speed = Math.max(0f, speed - 200f * dt);

        if (speed > 0f) angle = (angle + speed * dt) % 360f;

        lastTimeMap.put(key, now);
        speedMap.put(key, speed);
        angleMap.put(key, angle);
        return angle;
    }
}
