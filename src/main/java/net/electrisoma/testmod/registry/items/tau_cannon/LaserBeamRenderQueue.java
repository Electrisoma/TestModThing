package net.electrisoma.testmod.registry.items.tau_cannon;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;
import java.util.*;

public class LaserBeamRenderQueue {
    private static final List<LaserBeam> BEAMS = new ArrayList<>();
    private static final long LIFE_MS = 10_000;

    public static void addBeam(Vec3 s, Vec3 e, float[] rgb, float thickness) {
        BEAMS.add(new LaserBeam(s, e, rgb, thickness, System.currentTimeMillis()));
    }

    public static void renderAll(PoseStack ps, MultiBufferSource buffers) {
        long now = System.currentTimeMillis();
        Iterator<LaserBeam> it = BEAMS.iterator();

        while (it.hasNext()) {
            LaserBeam b = it.next();
            long age = now - b.timeCreated;
            if (age > LIFE_MS) { it.remove(); continue; }

            float alpha = 1f - age / (float)LIFE_MS;
            float thickness = b.thickness * alpha;

            LaserRenderer.renderBeamSegment(
                    ps, buffers,
                    b.start.x, b.start.y, b.start.z,
                    b.end.x,   b.end.y,   b.end.z,
                    b.rgb[0],  b.rgb[1],  b.rgb[2],
                    alpha, thickness
            );
        }
    }

    private record LaserBeam(Vec3 start, Vec3 end, float[] rgb, float thickness, long timeCreated) {}
}
