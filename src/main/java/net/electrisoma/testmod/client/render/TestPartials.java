package net.electrisoma.testmod.client.render;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.electrisoma.testmod.TestMod;

public class TestPartials {
    public static PartialModel TAU_CANNON_ITEM;
    public static PartialModel TAU_CANNON_DRUM;

    public static void init() {
        SuperByteBufferCache cache = SuperByteBufferCache.getInstance();
        cache.registerCompartment(CachedBuffers.GENERIC_BLOCK);
        cache.registerCompartment(CachedBuffers.PARTIAL);
        cache.registerCompartment(CachedBuffers.DIRECTIONAL_PARTIAL);

        TAU_CANNON_ITEM = PartialModel.of(TestMod.path("item/tau_cannon/body"));
        TAU_CANNON_DRUM = PartialModel.of(TestMod.path("item/tau_cannon/drum"));
    }
}
