package net.electrisoma.testmod.client.fabric;

import net.electrisoma.testmod.client.TestModClient;
import net.electrisoma.testmod.registry.TestItems;
import net.electrisoma.testmod.registry.TestParticles;
import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonItemRenderer;
import net.electrisoma.testmod.registry.particles.MoltenScorchParticle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

public class TestModClientImpl implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TestModClient.init();
        initializeItemRenderers();

        ParticleFactoryRegistry.getInstance().register(
                TestParticles.MOLTEN_SCORCH.get(),
                MoltenScorchParticle.Provider::new
        );
    }

    public static void initializeItemRenderers() {
            BuiltinItemRendererRegistry.INSTANCE.register(TestItems.TAU_CANNON.get(),
                    (BuiltinItemRendererRegistry.DynamicItemRenderer) new TauCannonItemRenderer());
    }
}