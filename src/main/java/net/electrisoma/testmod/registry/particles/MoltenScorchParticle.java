package net.electrisoma.testmod.registry.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class MoltenScorchParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected MoltenScorchParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z);

        this.sprites = sprites;

        this.lifetime = 40;
        this.setAlpha(1.0f);
        this.rCol = 1.0f;
        this.gCol = 0.5f;
        this.bCol = 0.0f;

        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.gravity = 0;

        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
        super.render(buffer, camera, partialTicks);
        RenderSystem.defaultBlendFunc();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new MoltenScorchParticle(level, x, y, z, sprites);
        }
    }
}
