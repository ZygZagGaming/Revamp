package com.zygzag.revamp.common.networking.packet;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ServerboundEnderBookInsertionPacket(int documentId, int pageId, int index, String text) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(documentId);
        buf.writeInt(pageId);
        buf.writeInt(index);
        buf.writeUtf(text);
    }

    public static ServerboundEnderBookInsertionPacket decode(FriendlyByteBuf buf) {
        return new ServerboundEnderBookInsertionPacket(buf.readInt(), buf.readInt(), buf.readInt(), buf.readUtf());
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        ServerPlayer sender = ctxSupplier.get().getSender();
        if (sender != null) {
            Level world = sender.server.getLevel(Level.OVERWORLD);
            if (world != null) GeneralUtil.ifCapability(world, Revamp.SERVER_LEVEL_ENDER_BOOK_CAPABILITY, (handler) -> {
                handler.addText(documentId, pageId, index, text);
            });
        }
    }
}
