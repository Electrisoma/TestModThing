package net.electrisoma.testmod.network.items.tau_cannon;

import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.electrisoma.testmod.registry.TestNetwork;
import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TauCannonServerPacket implements ServerboundPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, TauCannonServerPacket> STREAM_CODEC =
            StreamCodec.of(TauCannonServerPacket::write, TauCannonServerPacket::read);

    private final float damage;
    private final float recoil;
    private final int bounces;
    private final int pierces;

    public TauCannonServerPacket(float damage, float recoil, int bounces, int pierces) {
        this.damage = damage;
        this.recoil = recoil;
        this.bounces = bounces;
        this.pierces = pierces;
    }

    private static TauCannonServerPacket read(RegistryFriendlyByteBuf buf) {
        return new TauCannonServerPacket(
                buf.readFloat(),
                buf.readFloat(),
                buf.readVarInt(),
                buf.readVarInt()
        );
    }

    private static void write(RegistryFriendlyByteBuf buf, TauCannonServerPacket packet) {
        buf.writeFloat(packet.damage);
        buf.writeFloat(packet.recoil);
        buf.writeVarInt(packet.bounces);
        buf.writeVarInt(packet.pierces);
    }

    @Override
    public void handle(ServerPlayer player) {
        List<Vec3> beamPoints = TauCannonItem.shootRay(player.level(), player, damage, recoil, bounces, pierces);
        TauCannonItem.sendBeamToClient(player, beamPoints);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return TestNetwork.TAU_CANNON_BEAM_SERVER;
    }
}
