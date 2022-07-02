package com.zygzag.revamp.common.networking;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.networking.packet.*;
import com.zygzag.revamp.util.ClientUtils;
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
    static int id = -1;

    public static void registerMessages() {
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
        INSTANCE.registerMessage(
                id++,
                ClientboundEnderBookPageEditPacket.class,
                ClientboundEnderBookPageEditPacket::encode,
                ClientboundEnderBookPageEditPacket::decode,
                ClientboundEnderBookPageEditPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                ClientboundEnderBookInsertionPacket.class,
                ClientboundEnderBookInsertionPacket::encode,
                ClientboundEnderBookInsertionPacket::decode,
                ClientboundEnderBookInsertionPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                ClientboundEnderBookDeletionPacket.class,
                ClientboundEnderBookDeletionPacket::encode,
                ClientboundEnderBookDeletionPacket::decode,
                ClientboundEnderBookDeletionPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                ClientboundSendDocumentPacket.class,
                ClientboundSendDocumentPacket::encode,
                ClientboundSendDocumentPacket::decode,
                ClientboundSendDocumentPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                ServerboundEnderBookPageEditPacket.class,
                ServerboundEnderBookPageEditPacket::encode,
                ServerboundEnderBookPageEditPacket::decode,
                ServerboundEnderBookPageEditPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                ServerboundEnderBookInsertionPacket.class,
                ServerboundEnderBookInsertionPacket::encode,
                ServerboundEnderBookInsertionPacket::decode,
                ServerboundEnderBookInsertionPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                ServerboundEnderBookDeletionPacket.class,
                ServerboundEnderBookDeletionPacket::encode,
                ServerboundEnderBookDeletionPacket::decode,
                ServerboundEnderBookDeletionPacket::handle
        );
        INSTANCE.registerMessage(
                id++,
                ServerboundRequestDocumentPacket.class,
                ServerboundRequestDocumentPacket::encode,
                ServerboundRequestDocumentPacket::decode,
                ServerboundRequestDocumentPacket::handle
        );
    }
}
