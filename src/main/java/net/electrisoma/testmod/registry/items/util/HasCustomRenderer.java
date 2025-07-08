package net.electrisoma.testmod.registry.items.util;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.function.Consumer;

public interface HasCustomRenderer {
    RendererProvider provideRenderer();

    interface RendererProvider {
        Object provideRenderer();
    }

    @ExpectPlatform
    static void initializeClient(Consumer<Object> consumer, RendererProvider provider) {
        throw new AssertionError("Platform implementation missing");
    }
}
