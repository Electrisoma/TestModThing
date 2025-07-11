package net.electrisoma.testmod.client;

import net.electrisoma.testmod.TestMod;
import net.electrisoma.testmod.client.render.TestPartials;
import net.electrisoma.testmod.registry.items.tau_cannon.rendering.TauCannonFlash;
import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonItem;
import net.electrisoma.testmod.registry.items.util.ClientRenderableItem;
import net.electrisoma.testmod.registry.items.util.renderers.ItemRendererRegistrar;
import net.electrisoma.visceralib.annotations.Env;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.UUID;

@Env(Env.EnvType.CLIENT)
public class TestModClient {
    public static void init() {
        TestMod.LOGGER.info("womp womp client called");

        TauCannonFlash.init();

        TestPartials.init();
    }

    public static void registerClientItemRenderers(ItemRendererRegistrar registrar) {
        var modelSet = Minecraft.getInstance().getEntityModels();
        var dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();

        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (TestMod.MOD_ID.equals(id.getNamespace())) if (item instanceof ClientRenderableItem renderable) {
                BlockEntityWithoutLevelRenderer renderer = renderable.createRenderer(dispatcher, modelSet);
                registrar.register(item, renderer);
            }
        }
    }

    public static void clientTick() {
//        Minecraft mc = Minecraft.getInstance();
//        Level level = mc.level;
//        if (level == null) return;
//
//        TauCannonItem.beamTimers.entrySet().removeIf(entry -> {
//            UUID uuid = entry.getKey();
//            int timeLeft = entry.getValue() - 1;
//            if (timeLeft <= 0) {
//                TauCannonItem.activeBeams.remove(uuid);
//                return true;
//            } else {
//                entry.setValue(timeLeft);
//                return false;
//            }
//        });
//
//        for (Map.Entry<UUID, java.util.List<Vec3>> entry : TauCannonItem.activeBeams.entrySet()) {
//            var points = entry.getValue();
//            for (int i = 0; i < points.size() - 1; i++) {
//                TauCannonItem.spawnBeamParticles(level, points.get(i), points.get(i + 1));
//            }
//        }
    }
}