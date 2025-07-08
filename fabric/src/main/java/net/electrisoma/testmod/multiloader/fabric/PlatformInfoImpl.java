package net.electrisoma.testmod.multiloader.fabric;

import net.electrisoma.testmod.TestMod;
import net.electrisoma.testmod.multiloader.PlatformInfo;
import net.fabricmc.loader.api.FabricLoader;

public class PlatformInfoImpl {
    public static PlatformInfo getCurrent() {
        return PlatformInfo.FABRIC;
    }

    public static String findVersion() {
        return FabricLoader.getInstance()
                .getModContainer(TestMod.MOD_ID)
                .orElseThrow()
                .getMetadata()
                .getVersion()
                .getFriendlyString();
    }
}
