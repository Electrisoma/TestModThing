package net.electrisoma.testmod.multiloader.fabric;

import net.electrisoma.testmod.multiloader.Env;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class EnvImpl {
    public static Env getCurrent() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? Env.CLIENT : Env.SERVER;
    }
}