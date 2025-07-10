package net.electrisoma.testmod.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.electrisoma.testmod.TestMod;
import net.electrisoma.testmod.client.render.TestPartials;
import net.electrisoma.testmod.registry.TestItems;
import net.electrisoma.testmod.registry.TestParticles;
import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonBEWLR;
import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonFlash;
import net.electrisoma.testmod.registry.items.util.renderers.ItemRendererRegistry;
import net.electrisoma.testmod.registry.particles.MoltenScorchParticle;
import net.electrisoma.visceralib.annotations.Env;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.particle.ParticleEngine;

@Env(Env.EnvType.CLIENT)
public class TestModClient {
    public static void init() {
        TestMod.LOGGER.info("womp womp client called");

        TauCannonFlash.init();

        registerRenderers();

        TestPartials.init();
    }

    public static void registerRenderers() {
        ItemRendererRegistry.register(TestItems.TAU_CANNON.get(), TauCannonBEWLR::new);
    }
}