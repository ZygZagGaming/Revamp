package com.zygzag.revamp.common.networking.packet;

import com.zygzag.revamp.client.render.screen.EnderBookEditScreen;
import com.zygzag.revamp.common.capability.Document;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundSendDocumentPacket(int id, Document document) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(id);
        document.encode(buf);
    }

    public static ClientboundSendDocumentPacket decode(FriendlyByteBuf buf) {
        return new ClientboundSendDocumentPacket(buf.readInt(), Document.decode(buf));
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        Screen scr = Minecraft.getInstance().screen;
        if (scr instanceof EnderBookEditScreen screen) {
            screen.setDocument(document);
            screen.clearDisplayCache();
        }
        ctx.setPacketHandled(true);
    }
}
