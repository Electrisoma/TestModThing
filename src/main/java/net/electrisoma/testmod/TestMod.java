package net.electrisoma.testmod;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.createmod.catnip.lang.LangBuilder;

import net.electrisoma.testmod.multiloader.PlatformInfo;
import net.electrisoma.testmod.registry.ModSetup;
import net.electrisoma.testmod.registry.TestTabs;
import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonItem;
import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonItemRenderer;
import net.electrisoma.visceralib.VisceraLib;
import net.electrisoma.visceralib.api.registration.VisceralRegistrar;
import net.fabricmc.fabric.impl.client.rendering.BuiltinItemRendererRegistryImpl;
import net.minecraft.resources.ResourceLocation;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

@SuppressWarnings("unused")
public class TestMod {
	public static final String MOD_ID = "testmod";
	public static final String NAME = "TestMod";
	public static final String VERSION = PlatformInfo.findVersion();
	public static final String VLIB_NAME = VisceraLib.NAME;
	public static final String VLIB_VERSION = VisceraLib.VERSION;
	public static final String SERVER_START = "waaa";
	public static final PlatformInfo LOADER = PlatformInfo.getCurrent();
	public static final Logger LOGGER = LogUtils.getLogger();

	private static final VisceralRegistrar REGISTRAR = VisceralRegistrar
			.create(MOD_ID)
			.defaultCreativeTab(() -> TestTabs.BASE);

	public static void init() {
		LOGGER.info("{} {} & {} {} initializing! on platform: {}",
				NAME, VERSION, VLIB_NAME, VLIB_VERSION, LOADER);

		ModSetup.register();

		//onRegister();
	}

//	@SuppressWarnings("UnimplementedExpectPlatform")
//	@ExpectPlatform
//	public static void onRegister() {
//		throw new AssertionError();
//	}

	public static LangBuilder lang() {
		return new LangBuilder(MOD_ID);
	}

	public static ResourceLocation path(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

	public static VisceralRegistrar registrar() {
		return REGISTRAR;
	}
}