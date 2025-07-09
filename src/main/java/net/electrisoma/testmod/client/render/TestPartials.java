package net.electrisoma.testmod.client.render;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.electrisoma.testmod.TestMod;

public class TestPartials {
    public static final PartialModel TAU_CANNON_DRUM = PartialModel.of(TestMod.path("item/tau_cannon/drum"));
    public static final PartialModel TAU_CANNON_ITEM = PartialModel.of(TestMod.path("item/tau_cannon/body"));

    public static void init() {
        TAU_CANNON_DRUM.get();
        TAU_CANNON_ITEM.get();
    }
}
