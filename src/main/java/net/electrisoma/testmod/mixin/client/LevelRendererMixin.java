// render level event for multiloader

package net.electrisoma.testmod.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.electrisoma.testmod.registry.items.tau_cannon.LaserBeamRenderQueue;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void onRenderLevel(
            DeltaTracker deltaTracker,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightTexture lightTexture,
            Matrix4f frustumMatrix,
            Matrix4f projectionMatrix,
            CallbackInfo ci) {

        Minecraft mc = Minecraft.getInstance();
        PoseStack ps = new PoseStack();
        MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();

        Vec3 cam = camera.getPosition();

        ps.pushPose();

        ps.translate(-cam.x, -cam.y, -cam.z);

        ps.mulPose(new Quaternionf(camera.rotation()).invert());

        LaserBeamRenderQueue.renderAll(ps, buf);

        ps.popPose();

        buf.endBatch();
    }
}
