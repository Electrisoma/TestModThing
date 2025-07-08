package net.electrisoma.testmod.client;

import net.electrisoma.testmod.TestMod;
import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonFlash;
import net.electrisoma.visceralib.annotations.Env;

@Env(Env.EnvType.CLIENT)
public class TestModClient {
    public static void init() {
        TestMod.LOGGER.info("womp womp client called");
        TauCannonFlash.init();
    }
}
