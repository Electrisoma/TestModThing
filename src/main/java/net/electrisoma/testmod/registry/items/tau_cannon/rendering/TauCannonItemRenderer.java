package net.electrisoma.testmod.registry.items.tau_cannon.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.electrisoma.testmod.client.render.TestPartials;
import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonItem;
import net.electrisoma.testmod.registry.items.util.renderers.AbstractItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class TauCannonItemRenderer extends AbstractItemRenderer {
    private static final Map<Integer, Float> angleMap = new HashMap<>();
    public static final Map<Integer, Float> speedMap = new HashMap<>();
    private static final Map<Integer, Long> lastTimeMap = new HashMap<>();
    private static final Map<Integer, Long> releaseTimeMap = new HashMap<>();
    private static final Map<Integer, Float> releaseChargeMap = new HashMap<>();
    private static final long RELEASE_ANIM_DURATION_MS = 200;

    @Override
    protected PartialModel getMainModel() {
        return TestPartials.TAU_CANNON_ITEM;
    }

    private PartialModel getDrumModel() {
        return TestPartials.TAU_CANNON_DRUM;
    }

    @Override
    protected void applyTransform(ItemDisplayContext ctx, PoseStack poseStack, ItemStack stack) {
        switch (ctx) {
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> {
                poseStack.translate(-0.125, 0.01125, -0.3334);
                poseStack.scale(1.25f, 1.25f, 1.25f);
            }
            case FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> {
                float offset = getReleaseOffset(stack);
                poseStack.translate(-0.05, 0.25, 0.125 + offset);
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
            renderDrum(poseStack, bufferSource, light, overlay);
            poseStack.popPose();
            return;
        }

        float angle = updateRotation(stack);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.53125);
        poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
        poseStack.translate(-0.5, -0.5, -0.53125);
        renderDrum(poseStack, bufferSource, light, overlay);
        poseStack.popPose();
    }

    private void renderDrum(PoseStack poseStack, MultiBufferSource bufferSource,
                            int light, int overlay) {
        var drumBuf = CachedBuffers.partial(getDrumModel(), Blocks.AIR.defaultBlockState());
        drumBuf.light(light).overlay(overlay).renderInto(poseStack, bufferSource.getBuffer(RenderType.solid()));
    }

    private float updateRotation(ItemStack stack) {
        int key = System.identityHashCode(stack);
        long now = System.currentTimeMillis();
        long last = lastTimeMap.getOrDefault(key, now);
        float dt = (now - last) / 1000f;
        lastTimeMap.put(key, now);

        float angle = angleMap.getOrDefault(key, 0f);
        float velocity = speedMap.getOrDefault(key, 0f);

        float maxSpeed = 800f;            // degrees/sec
        float torqueInput = 8000f;        // charging input force
        float inertia = 30f;              // rotation resistance
        float frictionTorque = 80f;       // angular drag when not charging
        float damping = 1.2f;             // exponential velocity decay

        Minecraft mc = Minecraft.getInstance();
        boolean isCharging = mc.player != null
                && mc.player.isUsingItem()
                && mc.player.getUseItem() == stack;

        // torque
        float angularAccel = 0f;
        if (isCharging) {
            int charge = stack.getUseDuration(mc.player) - mc.player.getUseItemRemainingTicks();
            float ramp = Math.min(charge, TauCannonItem.MAX_CHARGE_TICKS) / (float) TauCannonItem.MAX_CHARGE_TICKS;
            ramp = 1 - (float) Math.pow(1 - ramp, 2); // ease-in curve

            float appliedTorque = ramp * torqueInput; // torque to charge
            angularAccel += appliedTorque / inertia;
        } else {
            float friction = (velocity > 0) ? -frictionTorque : 0f; // friction torque when not charging
            angularAccel += friction / inertia;

            velocity *= (float) Math.exp(-damping * dt); // exponential damping
        }

        // acceleration
        velocity += angularAccel * dt;
        velocity = Mth.clamp(velocity, 0f, maxSpeed);

        angle = (angle + velocity * dt) % 360f; // change angle *shrug*

        // wobble
        float wobble = (float) Math.sin(now / 50.0) * (velocity / maxSpeed) * 2f;
        float finalAngle = angle + wobble;

        speedMap.put(key, velocity);
        angleMap.put(key, angle);

        return finalAngle;
    }

    public static void startReleaseAnimation(ItemStack stack) {
        int key = System.identityHashCode(stack);
        releaseTimeMap.put(key, System.currentTimeMillis());

        Minecraft mc = Minecraft.getInstance();
        float chargePercent = 0f;

        if (mc.player != null && mc.player.getUseItem() == stack) {
            int charge = stack.getUseDuration(mc.player) - mc.player.getUseItemRemainingTicks();
            chargePercent = Math.min(charge, TauCannonItem.MAX_CHARGE_TICKS) / (float) TauCannonItem.MAX_CHARGE_TICKS;
        }

        releaseChargeMap.put(key, chargePercent);

        float spinKick = chargePercent * 600f; // kick on fire
        float currentSpeed = speedMap.getOrDefault(key, 0f);
        speedMap.put(key, currentSpeed + spinKick);
    }

    private float getReleaseOffset(ItemStack stack) {
        int key = System.identityHashCode(stack);
        long now = System.currentTimeMillis();

        if (!releaseTimeMap.containsKey(key))
            return 0f;

        long start = releaseTimeMap.get(key);
        long elapsed = now - start;

        if (elapsed > RELEASE_ANIM_DURATION_MS) {
            releaseTimeMap.remove(key);
            releaseChargeMap.remove(key);
            return 0f;
        }

        float progress = 1f - (elapsed / (float) RELEASE_ANIM_DURATION_MS);
        float eased = easeOut(progress);

        float chargeScale = releaseChargeMap.getOrDefault(key, 0f);
        float maxOffset = 0.1f;
        return eased * chargeScale * maxOffset;
    }

    private float easeOut(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }
}