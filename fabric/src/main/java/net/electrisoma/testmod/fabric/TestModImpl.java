package net.electrisoma.testmod.fabric;

import net.electrisoma.testmod.TestMod;

import net.electrisoma.visceralib.api.fabric.registration.VisceralBootstrapFabric;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class TestModImpl implements ModInitializer {
	@Override
	public void onInitialize() {
		//VisceralBootstrapFabric.init();

		TestMod.init();
		onServerStarting();
	}

	public void onServerStarting() {
		ServerLifecycleEvents.SERVER_STARTED.register(server ->
				TestMod.LOGGER.info(TestMod.SERVER_START)
		);
	}
}
