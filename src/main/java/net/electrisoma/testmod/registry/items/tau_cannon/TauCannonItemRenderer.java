package net.electrisoma.testmod.registry.items.tau_cannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class TauCannonItemRenderer {
    private final PartialModel fullCannon;
    private final PartialModel drum;

    private static final Map<Integer, Float> angleMap = new HashMap<>();
    private static final Map<Integer, Float> speedMap = new HashMap<>();
    private static final Map<Integer, Long> lastTimeMap = new HashMap<>();

    public TauCannonItemRenderer(PartialModel fullCannon, PartialModel drum) {
        this.fullCannon = fullCannon;
        this.drum = drum;
    }

    public void render(ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack,
                       MultiBufferSource bufferSource, int light, int overlay) {
        poseStack.pushPose();

        switch (ctx) {
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> {
                poseStack.translate(-0.125, 0.0125, -0.25); // L-R D-U F-B
                poseStack.scale(1.25f, 1.25f, 1.25f);
            }
            case FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> {
                poseStack.translate(-0.05, 0.25, 0.125);
                poseStack.scale(1.0f, 1.0f, 1.0f);
            }
            case GUI -> {
                poseStack.translate(1.425, 0, -0.2);
                poseStack.mulPose(Axis.XP.rotationDegrees(27));
                poseStack.mulPose(Axis.YP.rotationDegrees(-135));
                poseStack.mulPose(Axis.ZP.rotationDegrees(0));
                poseStack.scale(1.4f, 1.4f, 1.4f);
            }
            case FIXED -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(0));
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(0));
            }
            default -> {
                poseStack.scale(1.0f, 1.0f, 1.0f);
            }
        }

        var fullBuf = CachedBuffers.partial(fullCannon, Blocks.AIR.defaultBlockState());
        fullBuf.light(light).overlay(overlay).renderInto(poseStack, bufferSource.getBuffer(RenderType.cutout()));

        if (ctx == ItemDisplayContext.GUI) {
            poseStack.pushPose();
            float xOffset = 0.5f;
            float yOffset = 0.5f;
            float zOffset = 0.53125f;
            poseStack.translate(xOffset, yOffset, zOffset);
            poseStack.translate(-xOffset, -yOffset, -zOffset);

            var drumBuf = CachedBuffers.partial(drum, Blocks.AIR.defaultBlockState());
            drumBuf.light(light).overlay(overlay).renderInto(poseStack, bufferSource.getBuffer(RenderType.cutout()));
            poseStack.popPose();
            poseStack.popPose();
            return;
        }

        int key = System.identityHashCode(stack);
        long currentTime = System.currentTimeMillis();
        long lastTime = lastTimeMap.getOrDefault(key, currentTime);
        float currentSpeed = speedMap.getOrDefault(key, 0f);
        float accumulatedAngle = angleMap.getOrDefault(key, 0f);

        float deltaSeconds = (currentTime - lastTime) / 1000f;

        var player = Minecraft.getInstance().player;

        float baseSpeed = 60f;
        float maxExtraSpeed = 500f;
        float targetSpeed = 0f;
        boolean spinning = false;

        if (player != null) {
            ItemStack usedStack = player.getUseItem();
            if (usedStack == stack && player.isUsingItem()) {
                int maxUseDuration = stack.getUseDuration(player);
                int maxChargeTicks = 40;

                int remaining = player.getUseItemRemainingTicks();
                int chargeTicks = maxUseDuration - remaining;
                chargeTicks = Math.max(0, Math.min(chargeTicks, maxChargeTicks));

                float ramp = (float) chargeTicks / maxChargeTicks;
                targetSpeed = baseSpeed + ramp * maxExtraSpeed;
                spinning = true;
            }
        }

        if (spinning) {
            currentSpeed = targetSpeed;
        } else {
            float friction = 200f;
            if (currentSpeed > 0f) {
                currentSpeed -= friction * deltaSeconds;
                if (currentSpeed < 0f) currentSpeed = 0f;
            }
        }

        if (currentSpeed > 0f) {
            accumulatedAngle += currentSpeed * deltaSeconds;
            accumulatedAngle %= 360f;
        }

        lastTimeMap.put(key, currentTime);
        speedMap.put(key, currentSpeed);
        angleMap.put(key, accumulatedAngle);

        float angle = accumulatedAngle;

        poseStack.pushPose();

        float xOffset = 0.5f;
        float yOffset = 0.5f;
        float zOffset = 0.53125f;

        poseStack.translate(xOffset, yOffset, zOffset);
        poseStack.mulPose(Axis.ZP.rotationDegrees(angle));
        poseStack.translate(-xOffset, -yOffset, -zOffset);

        var drumBuf = CachedBuffers.partial(drum, Blocks.AIR.defaultBlockState());
        drumBuf.light(light).overlay(overlay).renderInto(poseStack, bufferSource.getBuffer(RenderType.cutout()));

        poseStack.popPose();
        poseStack.popPose();
    }
}
