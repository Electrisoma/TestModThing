package net.electrisoma.testmod.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.electrisoma.testmod.registry.items.util.ReequipAnimItem;
import net.electrisoma.testmod.registry.items.util.renderers.RenderHandCallback;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Shadow
    private ItemStack mainHandItem;
    @Shadow
    private ItemStack offHandItem;

    @Unique
    private static int testmod$mainHandSlot = 0;

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void testmod$tick(CallbackInfo ci,
                               LocalPlayer clientPlayerEntity,
                               ItemStack itemStack,
                               ItemStack itemStack2) {

        if (!testmod$shouldCauseReequipAnimation(mainHandItem, itemStack, clientPlayerEntity.getInventory().selected)) {
        } else {
            mainHandItem = itemStack;
        }

        if (!testmod$shouldCauseReequipAnimation(offHandItem, itemStack2, -1)) {
        } else {
            offHandItem = itemStack2;
        }
    }

    @Unique
    private static boolean testmod$shouldCauseReequipAnimation(ItemStack from, ItemStack to, int slot) {
        if (!from.isEmpty() && !to.isEmpty()) {
            boolean changed = false;
            if (slot != -1) {
                changed = slot != testmod$mainHandSlot;
                testmod$mainHandSlot = slot;
            }

            if (from.getItem() instanceof ReequipAnimItem handler) {
                return handler.shouldCauseReequipAnimation(from, to, changed);
            }
        }
        return true;
    }
}
