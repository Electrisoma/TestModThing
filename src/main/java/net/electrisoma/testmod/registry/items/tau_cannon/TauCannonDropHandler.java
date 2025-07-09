package net.electrisoma.testmod.registry.items.tau_cannon;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TauCannonDropHandler {
    private static final Set<Integer> hitEntities = new HashSet<>();
    private static final Set<Integer> chargedShots = new HashSet<>();

    public static void markHitByTauCannon(LivingEntity entity, boolean isCharged) {
        hitEntities.add(entity.getId());
        if (isCharged) {
            chargedShots.add(entity.getId());
        }
    }

    public static boolean wasHitByChargedShot(LivingEntity entity) {
        return chargedShots.remove(entity.getId());
    }

    public static Item getCookedDrop(Item item) {
        if (item == Items.BEEF) return Items.COOKED_BEEF;
        if (item == Items.PORKCHOP) return Items.COOKED_PORKCHOP;
        if (item == Items.CHICKEN) return Items.COOKED_CHICKEN;
        if (item == Items.RABBIT) return Items.COOKED_RABBIT;
        if (item == Items.MUTTON) return Items.COOKED_MUTTON;
        if (item == Items.COD) return Items.COOKED_COD;
        if (item == Items.SALMON) return Items.COOKED_SALMON;
        return null;
    }

    public static void replaceDrops(Level level, LivingEntity entity, List<ItemEntity> drops) {
        if (wasHitByChargedShot(entity)) {
            for (int i = 0; i < drops.size(); i++) {
                ItemEntity drop = drops.get(i);
                ItemStack stack = drop.getItem();
                Item cooked = getCookedDrop(stack.getItem());

                if (cooked != null) {
                    ItemStack cookedStack = new ItemStack(cooked, stack.getCount());
                    ItemEntity cookedDrop = new ItemEntity(level, drop.getX(), drop.getY(), drop.getZ(), cookedStack);
                    drops.set(i, cookedDrop);
                }
            }
        }
    }
}
