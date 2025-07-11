package net.electrisoma.testmod.registry;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.electrisoma.testmod.TestMod;
import net.electrisoma.testmod.network.items.tau_cannon.TauCannonClientPacket;
import net.electrisoma.testmod.network.items.tau_cannon.TauCannonServerPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;

public enum TestNetwork implements BasePacketPayload.PacketTypeProvider {
    TAU_CANNON_BEAM_SERVER(TauCannonServerPacket.class, TauCannonServerPacket.STREAM_CODEC),
    TAU_CANNON_BEAM_CLIENT(TauCannonClientPacket.class, TauCannonClientPacket.STREAM_CODEC),

    ;

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> TestNetwork(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(TestMod.path(name)),
                clazz,
                codec
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry registry = new CatnipPacketRegistry(TestMod.MOD_ID, 1);
        for (TestNetwork packet : TestNetwork.values()) {
            registry.registerPacket(packet.type);
        }
        registry.registerAllPackets();
    }
}
