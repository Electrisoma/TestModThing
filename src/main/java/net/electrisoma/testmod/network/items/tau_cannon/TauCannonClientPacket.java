package net.electrisoma.testmod.network.items.tau_cannon;

import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.electrisoma.testmod.registry.TestNetwork;
import net.electrisoma.testmod.registry.items.tau_cannon.rendering.TauCannonFlash;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class TauCannonClientPacket implements ClientboundPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, TauCannonClientPacket> STREAM_CODEC =
            StreamCodec.of(TauCannonClientPacket::write, TauCannonClientPacket::read);

    private final List<Vec3> beamPoints;

    public TauCannonClientPacket(List<Vec3> beamPoints) {
        this.beamPoints = beamPoints;
    }

    private static TauCannonClientPacket read(RegistryFriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<Vec3> points = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            points.add(new Vec3(x, y, z));
        }
        return new TauCannonClientPacket(points);
    }

    private static void write(RegistryFriendlyByteBuf buf, TauCannonClientPacket packet) {
        buf.writeVarInt(packet.beamPoints.size());
        for (Vec3 point : packet.beamPoints) {
            buf.writeDouble(point.x);
            buf.writeDouble(point.y);
            buf.writeDouble(point.z);
        }
    }

    @Override
    public void handle(LocalPlayer player) {
        Level level = player.clientLevel;
        if (beamPoints.isEmpty()) return;

        Vec3 start = beamPoints.getFirst();
        TauCannonFlash.spawnMuzzleFlash(player);
        level.playLocalSound(start.x, start.y, start.z,
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS,
                1.0f, 1.0f,
                false
        );

        for (int i = 0; i < beamPoints.size() - 1; i++) {
            spawnBeamParticles(level, beamPoints.get(i), beamPoints.get(i + 1));
        }

        Vec3 end = beamPoints.getLast();
        spawnImpactParticles(level, end);
    }

    private static final DustParticleOptions ORANGE_BEAM_PARTICLE =
            new DustParticleOptions(new Vec3(1.0f, 0.5f, 0.0f).toVector3f(), 1.5f);

    private void spawnBeamParticles(Level level, Vec3 from, Vec3 to) {
        int steps = (int)(from.distanceTo(to) * 2);
        if (steps <= 0) return;

        Vec3 delta = to.subtract(from).scale(1.0 / steps);
        Vec3 pos = from;

        for (int i = 0; i < steps; i++) {
            level.addParticle(ORANGE_BEAM_PARTICLE, pos.x, pos.y, pos.z, 0, 0, 0);
            pos = pos.add(delta);
        }
    }

    private void spawnImpactParticles(Level level, Vec3 pos) {
//        for (int i = 0; i < 5; i++) {
//            double offsetX = (level.random.nextDouble() - 0.5) * 0.2;
//            double offsetY = (level.random.nextDouble() - 0.5) * 0.2;
//            double offsetZ = (level.random.nextDouble() - 0.5) * 0.2;
//            level.addParticle(TauCannonParticles.MOLTEN_SCORCH.get(),
//                    pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ, 0, 0, 0);
//        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return TestNetwork.TAU_CANNON_BEAM_CLIENT;
    }
}
