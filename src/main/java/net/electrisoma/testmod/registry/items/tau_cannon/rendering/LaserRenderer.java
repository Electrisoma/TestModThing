package net.electrisoma.testmod.registry.items.tau_cannon.rendering;

import com.mojang.blaze3d.vertex.*;
import foundry.veil.api.client.render.vertex.VertexArray;
import foundry.veil.api.client.render.vertex.VertexArrayBuilder;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;

public class LaserRenderer {
    private static final ResourceLocation BEACON_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/entity/beacon_beam.png");
    private static final RenderType BEACON_BEAM_TYPE = RenderType.beaconBeam(BEACON_TEXTURE, true);

    private static VertexArray vao;
    private static boolean initialized = false;

    private static final Map<UUID, List<Vec3>> activeBeams = new HashMap<>();
    private static final Map<UUID, Integer> beamTimers = new HashMap<>();

    private static final int BEAM_LIFETIME_TICKS = 10;

    private static final Vector3f AXIS_Y = new Vector3f(0, 1, 0);
    private static final Vector3f AXIS_Z = new Vector3f(0, 0, 1);

    public static void init() {
        if (initialized) return;

        vao = VertexArray.create();

        int vertexBuffer = vao.getOrCreateBuffer(VertexArray.VERTEX_BUFFER);
        int colorBuffer = vao.getOrCreateBuffer(1);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            ByteBuffer vertexData = stack.malloc(Float.BYTES * 5 * 4);
            FloatBuffer vbuf = vertexData.asFloatBuffer();

            float size = 0.5f;
            vbuf.put(-size).put(-size).put(0f).put(0f).put(1f);
            vbuf.put(size).put(-size).put(0f).put(1f).put(1f);
            vbuf.put(size).put(size).put(0f).put(1f).put(0f);
            vbuf.put(-size).put(size).put(0f).put(0f).put(0f);
            vbuf.flip();

            VertexArray.upload(vertexBuffer, vertexData, VertexArray.DrawUsage.STATIC);

            ByteBuffer colorData = stack.malloc(4 * 4);
            colorData.put((byte) 255).put((byte) 128).put((byte) 0).put((byte) 255);
            colorData.put((byte) 255).put((byte) 128).put((byte) 0).put((byte) 255);
            colorData.put((byte) 255).put((byte) 128).put((byte) 0).put((byte) 255);
            colorData.put((byte) 255).put((byte) 128).put((byte) 0).put((byte) 255);
            colorData.flip();

            VertexArray.upload(colorBuffer, colorData, VertexArray.DrawUsage.STATIC);

            ByteBuffer indices = stack.bytes(
                    (byte) 0, (byte) 1, (byte) 2,
                    (byte) 2, (byte) 3, (byte) 0
            );

            vao.uploadIndexBuffer(indices);
            vao.setIndexCount(6, VertexArray.IndexType.BYTE);

            vao.setDrawMode(VertexFormat.Mode.TRIANGLES);

            VertexArrayBuilder builder = vao.editFormat();

            builder.defineVertexBuffer(0, vertexBuffer, 0, Float.BYTES * 5, 0);
            builder.defineVertexBuffer(1, colorBuffer, 0, 4, 0);

            builder.setVertexAttribute(0, 0, 3, VertexArrayBuilder.DataType.FLOAT, false, 0);
            builder.setVertexAttribute(1, 0, 2, VertexArrayBuilder.DataType.FLOAT, false, Float.BYTES * 3);
            builder.setVertexAttribute(2, 1, 4, VertexArrayBuilder.DataType.UNSIGNED_BYTE, true, 0);
        }

        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage, levelRenderer, bufferSource, matrixStack,
                                                           frustumMatrix, projectionMatrix, renderTick,
                                                           deltaTracker, camera, frustum) -> {
            tick();
            render((PoseStack) matrixStack);
        });

        initialized = true;
    }

    private static Quaternionf toMcQuaternion(Quaternionf q) {
        return new Quaternionf(q.x(), q.y(), q.z(), q.w());
    }

    private static void applyYawPitchRotation(PoseStack poseStack, double yaw, double pitch) {
        Quaternionf yawQuat = new Quaternionf().fromAxisAngleRad(AXIS_Y, (float) (-yaw + Math.PI / 2));
        Quaternionf pitchQuat = new Quaternionf().fromAxisAngleRad(AXIS_Z, (float) pitch);

        yawQuat.mul(pitchQuat);

        Quaternionf mcQuat = toMcQuaternion(yawQuat);
        poseStack.mulPose(mcQuat);
    }

    public static void render(PoseStack poseStack) {
        if (!initialized) init();

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) return;

        LightTexture lightTexture = minecraft.gameRenderer.lightTexture();
        lightTexture.turnOnLightLayer();

        Vec3 camPos = minecraft.gameRenderer.getMainCamera().getPosition();

        for (List<Vec3> points : activeBeams.values()) {
            if (points.size() < 2) continue;

            for (int i = 0; i < points.size() - 1; i++) {
                Vec3 start = points.get(i).subtract(camPos);
                Vec3 end = points.get(i + 1).subtract(camPos);

                Vec3 delta = end.subtract(start);
                double length = delta.length();

                if (length < 0.01) continue;

                poseStack.pushPose();

                poseStack.translate(start.x, start.y, start.z);

                double yaw = Math.atan2(delta.z, delta.x);
                double pitch = Math.acos(delta.y / length);

                applyYawPitchRotation(poseStack, yaw, pitch);

                poseStack.scale((float) length, 1f, 1f);

                vao.drawWithRenderType(BEACON_BEAM_TYPE);

                poseStack.popPose();
            }
        }

        lightTexture.turnOffLightLayer();
    }

    public static void addBeam(UUID playerUUID, List<Vec3> points) {
        if (points == null || points.size() < 2) return;
        activeBeams.put(playerUUID, points);
        beamTimers.put(playerUUID, BEAM_LIFETIME_TICKS);
    }

    public static void tick() {
        Iterator<Map.Entry<UUID, Integer>> iterator = beamTimers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            int timeLeft = entry.getValue() - 1;
            if (timeLeft <= 0) {
                iterator.remove();
                activeBeams.remove(entry.getKey());
            } else {
                entry.setValue(timeLeft);
            }
        }
    }

}
