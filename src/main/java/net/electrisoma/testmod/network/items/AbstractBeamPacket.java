package net.electrisoma.testmod.network.items;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractBeamPacket implements ClientboundPacketPayload {
    protected final Vec3 location;
    protected final InteractionHand hand;
    protected final boolean self;

    public AbstractBeamPacket(Vec3 location, InteractionHand hand, boolean self) {
        this.location = location;
        this.hand = hand;
        this.self = self;
    }

    protected abstract void handleAdditional(LocalPlayer player);

    public void handle(LocalPlayer player) {
        Entity renderViewEntity = Minecraft.getInstance().getCameraEntity();
        if (renderViewEntity == null) return;
        if (renderViewEntity.position().distanceTo(location) > 100) return;

        handleAdditional(player);
    }
}
