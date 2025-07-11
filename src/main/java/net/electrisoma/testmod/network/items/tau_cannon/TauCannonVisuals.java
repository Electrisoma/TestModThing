//package net.electrisoma.testmod.network.items.tau_cannon;
//
//import io.netty.buffer.ByteBuf;
//import net.createmod.catnip.codecs.stream.CatnipStreamCodecs;
//import net.createmod.catnip.platform.CatnipClientServices;
//import net.electrisoma.testmod.network.items.AbstractBeamPacket;
//import net.electrisoma.testmod.registry.TestNetwork;
//import net.electrisoma.testmod.registry.items.tau_cannon.rendering.TauCannonFlash;
//import net.electrisoma.testmod.registry.items.tau_cannon.TauCannonItem;
//import net.minecraft.client.player.LocalPlayer;
//import net.minecraft.network.codec.ByteBufCodecs;
//import net.minecraft.network.codec.StreamCodec;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.sounds.SoundSource;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.phys.Vec3;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.UUID;
//
//public class TauCannonVisuals extends AbstractBeamPacket {
//    private final List<Vec3> rayPath;
//    private final UUID shooterId;
//
//    public TauCannonVisuals(UUID shooterId, Vec3 location, InteractionHand hand, boolean self, List<Vec3> rayPath) {
//        super(location, hand, self);
//        this.shooterId = shooterId;
//        this.rayPath = rayPath;
//    }
//
//    public static void sendVisualsToClients(TauCannonVisuals visualsPacket) {
//        CatnipClientServices.NETWORK.sendToAllClients(visualsPacket);
//    }
//
//    @Override
//    protected void handleAdditional(LocalPlayer player) {
//        Entity shooter = player.level().getEntity(Objects.requireNonNull(player.level().getPlayerByUUID(shooterId)).getId());
//        if (shooter == null)
//            return;
//
//        TauCannonItem.activeBeams.put(shooterId, List.of((Vec3) rayPath));
//        TauCannonItem.beamTimers.put(shooterId, 10);
//
//        for (int i = 0; i < rayPath.size() - 1; i++) {
//            Vec3 from = rayPath.get(i);
//            Vec3 to = rayPath.get(i + 1);
//            TauCannonItem.spawnBeamParticles(player.level(), from, to);
//        }
//
//        if (self) {
//            TauCannonFlash.spawnMuzzleFlash(player);
//        }
//
//        player.level().playLocalSound(
//                shooter.getX(), shooter.getY(), shooter.getZ(),
//                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.0f, 1.0f, false
//        );
//    }
//
//    public static final StreamCodec<ByteBuf, UUID> UUID_CODEC = StreamCodec.composite(
//            ByteBufCodecs.VAR_LONG, UUID::getMostSignificantBits,
//            ByteBufCodecs.VAR_LONG, UUID::getLeastSignificantBits,
//            UUID::new
//    );
//
//    public static final StreamCodec<ByteBuf, List<Vec3>> VEC3_LIST_CODEC = StreamCodec.of(
//            (buf, list) -> {
//                ByteBufCodecs.VAR_INT.encode(buf, list.size());
//                for (Vec3 vec : list)
//                    CatnipStreamCodecs.VEC3.encode(buf, vec);
//            },
//            buf -> {
//                int size = ByteBufCodecs.VAR_INT.decode(buf);
//                List<Vec3> list = new ArrayList<>(size);
//                for (int i = 0; i < size; i++)
//                    list.add(CatnipStreamCodecs.VEC3.decode(buf));
//                return list;
//            }
//    );
//
//    public static final StreamCodec<ByteBuf, TauCannonVisuals> STREAM_CODEC = StreamCodec.composite(
//            UUID_CODEC, packet -> packet.shooterId,
//            CatnipStreamCodecs.VEC3, packet -> packet.location,
//            CatnipStreamCodecs.HAND, packet -> packet.hand,
//            ByteBufCodecs.BOOL, packet -> packet.self,
//            VEC3_LIST_CODEC, packet -> packet.rayPath,
//            TauCannonVisuals::new
//    );
//
//    @Override
//    public PacketTypeProvider getTypeProvider() {
//        return TestNetwork.TAU_CANNON_BEAM_CLIENT;
//    }
//}