package com.zygzag.revamp.common.networking.packet;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ServerboundEnderBookDeletionPacket(int documentId, int pageId, int index, int length) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(documentId);
        buf.writeInt(pageId);
        buf.writeInt(index);
        buf.writeInt(length);
    }

    public static ServerboundEnderBookDeletionPacket decode(FriendlyByteBuf buf) {
        return new ServerboundEnderBookDeletionPacket(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        ServerPlayer sender = ctxSupplier.get().getSender();
        if (sender != null) {
            Level world = sender.server.getLevel(Level.OVERWORLD);
            if (world != null) GeneralUtil.ifCapability(world, Revamp.SERVER_LEVEL_ENDER_BOOK_CAPABILITY, (handler) -> {
                handler.deleteText(documentId, pageId, index, length);
            });
        }
    }
}
