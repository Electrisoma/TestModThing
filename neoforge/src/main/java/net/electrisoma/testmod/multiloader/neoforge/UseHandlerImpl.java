package net.electrisoma.testmod.multiloader.neoforge;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;

public class UseHandlerImpl {
    public static InteractionResultHolder<ItemStack> wrapUseResult(ItemStack stack) {
        return InteractionResultHolder.success(stack);
    }
}
