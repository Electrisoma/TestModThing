package net.electrisoma.testmod.registry.items.tau_cannon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class LaserRenderer {

    private static final RenderType BEACON_BEAM_TYPE = RenderType.beaconBeam(
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/beacon_beam.png"), true);

    public static void renderBeamSegment(
            PoseStack poseStack,
            MultiBufferSource buffers,
            double startX, double startY, double startZ,
            double endX, double endY, double endZ,
            float r, float g, float b, float alpha,
            float thickness) {

        VertexConsumer vertexConsumer = buffers.getBuffer(BEACON_BEAM_TYPE);

        double dx = endX - startX;
        double dy = endY - startY;
        double dz = endZ - startZ;

        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length == 0) return;

        Vector3f dir = new Vector3f((float)(dx / length), (float)(dy / length), (float)(dz / length));

        // Choose arbitrary up vector not parallel to dir
        Vector3f arbitraryUp = new Vector3f(0, 1, 0);
        if (Math.abs(dir.dot(arbitraryUp)) > 0.99f) {
            arbitraryUp.set(1, 0, 0);
        }

        Vector3f right = new Vector3f();
        dir.cross(arbitraryUp, right).normalize();

        Vector3f up = new Vector3f();
        right.cross(dir, up).normalize();

        float halfThickness = thickness / 2f;

        Vector3f start = new Vector3f((float) startX, (float) startY, (float) startZ);
        Vector3f end = new Vector3f((float) endX, (float) endY, (float) endZ);

        // Quad corners with both right and up offsets
        Vector3f bl = new Vector3f(start)
                .sub(new Vector3f(right).mul(halfThickness))
                .sub(new Vector3f(up).mul(halfThickness));
        Vector3f br = new Vector3f(start)
                .add(new Vector3f(right).mul(halfThickness))
                .sub(new Vector3f(up).mul(halfThickness));
        Vector3f tr = new Vector3f(end)
                .add(new Vector3f(right).mul(halfThickness))
                .add(new Vector3f(up).mul(halfThickness));
        Vector3f tl = new Vector3f(end)
                .sub(new Vector3f(right).mul(halfThickness))
                .add(new Vector3f(up).mul(halfThickness));

        // Normal for lighting (right cross dir)
        Vector3f normal = new Vector3f();
        right.cross(dir, normal).normalize();

        Matrix4f matrix = poseStack.last().pose();

        vertexConsumer.addVertex(matrix, bl.x(), bl.y(), bl.z())
                .setColor(r, g, b, alpha)
                .setUv(0f, 0f)
                .setOverlay(0)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(normal.x(), normal.y(), normal.z());

        vertexConsumer.addVertex(matrix, br.x(), br.y(), br.z())
                .setColor(r, g, b, alpha)
                .setUv(1f, 0f)
                .setOverlay(0)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(normal.x(), normal.y(), normal.z());

        vertexConsumer.addVertex(matrix, tr.x(), tr.y(), tr.z())
                .setColor(r, g, b, alpha)
                .setUv(1f, (float) length)
                .setOverlay(0)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(normal.x(), normal.y(), normal.z());

        vertexConsumer.addVertex(matrix, tl.x(), tl.y(), tl.z())
                .setColor(r, g, b, alpha)
                .setUv(0f, (float) length)
                .setOverlay(0)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(normal.x(), normal.y(), normal.z());
    }

}
