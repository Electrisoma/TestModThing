package net.electrisoma.testmod.registry.items.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface LeftClickHandlerItem {
    void handleLeftClick(ItemStack stack, Level level, Player player);
}
