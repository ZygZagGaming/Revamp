package com.zygzag.revamp.common.networking.packet;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.capability.Document;
import com.zygzag.revamp.common.capability.ServerLevelEnderBookHandler;
import com.zygzag.revamp.common.networking.RevampPacketHandler;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public record ServerboundRequestDocumentPacket(int id) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(id);
    }

    public static ServerboundRequestDocumentPacket decode(FriendlyByteBuf buf) {
        return new ServerboundRequestDocumentPacket(buf.readInt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ServerPlayer player = ctx.getSender();
        if (player != null) {
            ServerLevel world = player.getLevel();
            GeneralUtil.ifCapability(world, Revamp.SERVER_LEVEL_ENDER_BOOK_CAPABILITY, (handler) -> {
                while (handler.documents.size() <= id) handler.documents.add(new Document());
                RevampPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundSendDocumentPacket(id, handler.documents.get(id)));
                ctx.setPacketHandled(true);
            });
        }
    }
}
