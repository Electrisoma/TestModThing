package net.electrisoma.testmod.multiloader;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface UseHandler {
    default InteractionResultHolder<ItemStack> handleUse(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        return wrapUseResult(stack);
    }

    @ExpectPlatform
    static InteractionResultHolder<ItemStack> wrapUseResult(ItemStack stack) {
        throw new AssertionError();
    }
}
