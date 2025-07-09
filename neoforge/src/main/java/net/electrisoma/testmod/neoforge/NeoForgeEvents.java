package net.electrisoma.testmod.neoforge;

import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonDropHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class NeoForgeEvents {
    @SubscribeEvent
    public static void onMobDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (TauCannonDropHandler.wasHitByChargedShot(entity)) {
            TauCannonDropHandler.replaceDrops(entity.level(), entity, (List<ItemEntity>) event.getDrops());
        }
    }
}
