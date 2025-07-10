package net.electrisoma.testmod.client.neoforge;

import net.electrisoma.testmod.TestMod;
import net.electrisoma.testmod.client.TestModClient;

import net.electrisoma.testmod.registry.TestItems;
import net.electrisoma.testmod.registry.TestParticles;
import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonBEWLR;
import net.electrisoma.testmod.registry.particles.MoltenScorchParticle;
import net.electrisoma.visceralib.events.client.VisceralParticleRegistryEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = TestMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class TestModClientImpl {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        TestModClient.init();
    }

    @SubscribeEvent
    public static void setupParticles(RegisterParticleProvidersEvent registry) {
        registry.registerSpriteSet(TestParticles.MOLTEN_SCORCH.get(), MoltenScorchParticle.Provider::new);
    }

    @SubscribeEvent
    public static void initializeItemRenderers(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new TauCannonBEWLR();
            }
        }, TestItems.TAU_CANNON.get());
    }
}