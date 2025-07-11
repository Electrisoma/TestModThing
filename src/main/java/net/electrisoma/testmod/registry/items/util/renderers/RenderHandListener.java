package net.electrisoma.testmod.registry.items.util.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public interface RenderHandListener {
    boolean onRenderHand(AbstractClientPlayer player, InteractionHand hand, ItemStack stack,
                         PoseStack matrices, MultiBufferSource vertexConsumers,
                         float tickDelta, float pitch, float swingProgress, float equipProgress, int light);
}
