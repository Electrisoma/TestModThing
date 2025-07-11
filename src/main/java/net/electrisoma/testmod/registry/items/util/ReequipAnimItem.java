package net.electrisoma.testmod.registry.items.util;

import net.minecraft.world.item.ItemStack;

public interface ReequipAnimItem {
    boolean shouldCauseReequipAnimation(ItemStack from, ItemStack to, boolean changed);
}
