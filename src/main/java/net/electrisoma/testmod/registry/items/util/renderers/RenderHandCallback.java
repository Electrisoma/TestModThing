package net.electrisoma.testmod.registry.items.util.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RenderHandCallback {
    private static final List<RenderHandListener> listeners = new ArrayList<>();

    public static void register(RenderHandListener listener) {
        listeners.add(listener);
    }

    public static boolean invoke(AbstractClientPlayer player, InteractionHand hand, ItemStack stack,
                                 PoseStack matrices, MultiBufferSource vertexConsumers,
                                 float tickDelta, float pitch, float swingProgress, float equipProgress, int light) {
        for (RenderHandListener listener : listeners) {
            if (listener.onRenderHand(player, hand, stack, matrices, vertexConsumers,
                    tickDelta, pitch, swingProgress, equipProgress, light)) {
                return true; // canceled
            }
        }
        return false;
    }
}
