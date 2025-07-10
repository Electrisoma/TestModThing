package net.electrisoma.testmod.registry;

import net.electrisoma.testmod.registry.particles.MoltenScorchParticle;
import net.electrisoma.visceralib.VisceraLib;
import net.electrisoma.visceralib.api.registration.VisceralRegistrar;
import net.electrisoma.visceralib.api.registration.entry.ParticleEntry;

import net.minecraft.core.particles.SimpleParticleType;

public class TestParticles {
    public static void init() {
        VisceraLib.LOGGER.info("Registering Particles for " + VisceraLib.NAME);
    }

    private static final VisceralRegistrar REGISTRAR = VisceraLib.registrar();

    public static final ParticleEntry<SimpleParticleType> MOLTEN_SCORCH = REGISTRAR
            .particle("molten_scorch", () -> new SimpleParticleType(false))
            .factory(MoltenScorchParticle.Provider::new)
            .register();
}
