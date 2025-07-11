package net.electrisoma.testmod.client.fabric;

import net.electrisoma.testmod.client.TestModClient;
import net.electrisoma.testmod.registry.TestParticles;
import net.electrisoma.testmod.registry.particles.MoltenScorchParticle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

public class TestModClientImpl implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TestModClient.init();

        initializeParticles();
        initializeItemRenderers();
        initializeTickEvents();
    }

    public static void initializeParticles() {
        ParticleFactoryRegistry.getInstance().register(
                TestParticles.MOLTEN_SCORCH.get(),
                MoltenScorchParticle.Provider::new
        );
    }


    public static void initializeItemRenderers() {
        TestModClient.registerClientItemRenderers((item, renderer) ->
                BuiltinItemRendererRegistry.INSTANCE.register(
                        item, renderer::renderByItem
                )
        );
    }

    public static void initializeTickEvents() {
//        ClientTickEvents.END_CLIENT_TICK.register(client -> TestModClient.clientTick());
    }
}