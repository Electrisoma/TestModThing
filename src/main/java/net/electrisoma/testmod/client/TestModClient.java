package net.electrisoma.testmod.client;

import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.electrisoma.testmod.TestMod;
import net.electrisoma.testmod.client.render.TestPartials;
import net.electrisoma.testmod.registry.TestItems;
import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonFlash;
import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonItemRenderer;
import net.electrisoma.visceralib.annotations.Env;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

@Env(Env.EnvType.CLIENT)
public class TestModClient {
    public static void init() {
        TestMod.LOGGER.info("womp womp client called");

        TauCannonFlash.init();
        SuperByteBufferCache cache = SuperByteBufferCache.getInstance();
        cache.registerCompartment(CachedBuffers.GENERIC_BLOCK);
        cache.registerCompartment(CachedBuffers.PARTIAL);
        cache.registerCompartment(CachedBuffers.DIRECTIONAL_PARTIAL);

        TestPartials.init();

        BuiltinItemRendererRegistry.INSTANCE.register(
                TestItems.TAU_CANNON.get(),
                new TauCannonItemRenderer(TestPartials.TAU_CANNON_ITEM, TestPartials.TAU_CANNON_DRUM)::render
        );
    }
}