package net.electrisoma.testmod.client.fabric;

import net.electrisoma.testmod.client.TestModClient;
import net.electrisoma.testmod.registry.TestParticles;
import net.electrisoma.testmod.registry.particles.TestCloudParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class TestModClientImpl implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TestModClient.init();

        ParticleFactoryRegistry.getInstance().register(
                TestParticles.CLOUD.get(),
                TestCloudParticle.provider::new
        );
    }
}