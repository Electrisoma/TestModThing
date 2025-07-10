package net.electrisoma.testmod.neoforge;

import net.electrisoma.testmod.TestMod;

import net.electrisoma.testmod.registry.TestSounds;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@Mod(TestMod.MOD_ID)
public class TestModImpl {
	static IEventBus eventBus;
	static IEventBus neoforgeBus;

	public TestModImpl() {
		eventBus = ModLoadingContext.get().getActiveContainer().getEventBus();
		neoforgeBus = NeoForge.EVENT_BUS;

		TestMod.init();

		TestSounds.register(eventBus);
		neoforgeBus.addListener(this::onServerStarted);
	}

	public void onServerStarted(ServerStartedEvent event) {
		TestMod.LOGGER.info(TestMod.SERVER_START);
	}
}
