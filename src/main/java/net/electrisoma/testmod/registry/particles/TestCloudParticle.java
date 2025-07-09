package net.electrisoma.testmod.registry.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class TestCloudParticle extends TextureSheetParticle {
    public TestCloudParticle(ClientLevel level, double x, double y, double z,
                             double dx, double dy, double dz, SpriteSet spriteSet) {
        super(level, x, y, z, dx, dy, dz);
        this.lifetime = 40 + level.random.nextInt(10);
        this.gravity = 0.0f;
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.setSize(0.5f, 0.5f);
        this.pickSprite(spriteSet);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new net.electrisoma.visceralib.testreg.particles.TestCloudParticle(level, x, y, z, dx, dy, dz, sprites);
        }
    }

}
