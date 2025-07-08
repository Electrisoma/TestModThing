package net.electrisoma.testmod.registry;

import net.electrisoma.testmod.TestMod;

import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonItem;
import net.electrisoma.visceralib.api.registration.VisceralRegistrar;
import net.electrisoma.visceralib.api.registration.entry.ItemEntry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class TestItems {
    private static final VisceralRegistrar REGISTRAR = TestMod.registrar();

    public static void init() {
        TestMod.LOGGER.info("Registering Items for " + TestMod.NAME);
    }

    public static final ItemEntry<Item> TEST_ITEM = REGISTRAR
            .item("test_item", Item::new)
            .properties(p -> p
                    .stacksTo(1)
                    .rarity(Rarity.COMMON)
                    .food(new FoodProperties.Builder()
                            .nutrition(4)
                            .saturationModifier(0.1f)
                            .alwaysEdible()
                            .effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0),0.8f)
                            .effect(new MobEffectInstance(MobEffects.POISON, 300, 2),0.8f)
                            .build())
            )
            .register();

    public static final ItemEntry<TauCannonItem> TAU_CANNON = REGISTRAR
            .item("tau_cannon", TauCannonItem::new)
            .properties(p -> p
                    .stacksTo(1)
                    .rarity(Rarity.COMMON)
            )
            .register();
}
