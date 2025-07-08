package net.electrisoma.testmod.client.fabric;

import net.electrisoma.testmod.client.TestModClient;
import net.fabricmc.api.ClientModInitializer;

public class TestModClientImpl implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TestModClient.init();
    }
}