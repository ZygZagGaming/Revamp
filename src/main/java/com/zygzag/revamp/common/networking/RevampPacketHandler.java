package com.zygzag.revamp.common.networking;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.networking.packet.ClientboundArcCreationPacket;
import com.zygzag.revamp.common.networking.packet.ClientboundChargeUpdatePacket;
import com.zygzag.revamp.common.networking.packet.ClientboundEntityChargeSyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class RevampPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Revamp.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerMessages() {
        int id = -1;
        INSTANCE.registerMessage(
                id++,
                ClientboundChargeUpdatePacket.class,
                ClientboundChargeUpdatePacket::encode,
                ClientboundChargeUpdatePacket::decode,
                ClientboundChargeUpdatePacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                ClientboundArcCreationPacket.class,
                ClientboundArcCreationPacket::encode,
                ClientboundArcCreationPacket::decode,
                ClientboundArcCreationPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                ClientboundEntityChargeSyncPacket.class,
                ClientboundEntityChargeSyncPacket::encode,
                ClientboundEntityChargeSyncPacket::decode,
                ClientboundEntityChargeSyncPacket::handle
        );
    }
}
