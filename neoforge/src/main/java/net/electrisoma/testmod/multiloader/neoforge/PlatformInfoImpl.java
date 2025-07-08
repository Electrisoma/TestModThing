package net.electrisoma.testmod.multiloader.neoforge;

import net.electrisoma.testmod.TestMod;
import net.electrisoma.testmod.multiloader.PlatformInfo;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.MavenVersionAdapter;

import java.util.List;

@SuppressWarnings("unused")
public class PlatformInfoImpl {
    public static PlatformInfo getCurrent() {
        return PlatformInfo.NEOFORGE;
    }

    public static String findVersion() {
        String versionString = "UNKNOWN";

        List<IModInfo> infoList = ModList.get().getModFileById(TestMod.MOD_ID).getMods();
        if (infoList.size() > 1) TestMod.LOGGER.error("Multiple mods for MOD_ID: " + TestMod.MOD_ID);
        for (IModInfo info : infoList) {
            if (info.getModId().equals(TestMod.MOD_ID)) {
                versionString = String.valueOf(MavenVersionAdapter.createFromVersionSpec(String.valueOf(info.getVersion())));
                break;
            }
        }
        return versionString;
    }
}
