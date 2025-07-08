package net.electrisoma.testmod.events;

import net.electrisoma.testmod.TestMod;
import net.electrisoma.testmod.registry.items.util.LeftClickHandlerItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CommonEvents {
    public static void onLeftClick(Player player, Level level) {
        TestMod.LOGGER.info("left clickced");

        ItemStack stack = player.getMainHandItem();

        if (stack.getItem() instanceof LeftClickHandlerItem handler) {
            handler.handleLeftClick(stack, level, player);
        }
    }
}
