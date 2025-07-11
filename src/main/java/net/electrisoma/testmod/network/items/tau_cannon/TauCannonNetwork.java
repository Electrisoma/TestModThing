package net.electrisoma.testmod.network.items.tau_cannon;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

public class TauCannonNetwork {
    public static void sendBeamLogicPacket(float damage, float recoil, int bounces, int pierces) {
        TauCannonServerPacket packet = new TauCannonServerPacket(damage, recoil, bounces, pierces);
        Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(new ServerboundCustomPayloadPacket(packet));
    }
    public static void sendBeamVisualPacket(List<Vec3> beamPoints) {
        TauCannonClientPacket packet = new TauCannonClientPacket(beamPoints);
        Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(new ClientboundCustomPayloadPacket(packet));
    }
}
