package net.electrisoma.testmod.registry;

import net.electrisoma.testmod.TestMod;

import net.electrisoma.visceralib.api.registration.VisceralRegistrar;
import net.electrisoma.visceralib.api.registration.entry.TabEntry;
import net.minecraft.world.item.*;

import static net.electrisoma.testmod.TestMod.MOD_ID;
import static net.electrisoma.testmod.TestMod.NAME;

@SuppressWarnings("unused")
public class TestTabs {
    public static void init() {
        TestMod.LOGGER.info("Registering Tabs for " + TestMod.NAME);
    }

    private static final VisceralRegistrar REGISTRAR = TestMod.registrar();

    public static final TabEntry<CreativeModeTab> BASE = REGISTRAR
            .tab(MOD_ID)
            .lang(NAME)
            .icon(Items.IRON_INGOT)
            .register();

    public static final TabEntry<CreativeModeTab> BLOCKS = REGISTRAR
            .tab("blocks")
            .icon(Items.DIRT)
            //.after(BASE)
            .register();
}
