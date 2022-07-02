package com.zygzag.revamp.common.networking.packet;

import com.zygzag.revamp.client.render.screen.EnderBookEditScreen;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundEnderBookDeletionPacket(int documentId, int pageId, int index, int length) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(documentId);
        buf.writeInt(pageId);
        buf.writeInt(index);
        buf.writeInt(length);
    }

    public static ClientboundEnderBookDeletionPacket decode(FriendlyByteBuf buf) {
        return new ClientboundEnderBookDeletionPacket(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        Screen scr = Minecraft.getInstance().screen;
        if (scr instanceof EnderBookEditScreen screen && screen.getDocumentId() == documentId) {
            screen.removePageText(pageId, index, length);
        }
        ctx.setPacketHandled(true);
    }
}
