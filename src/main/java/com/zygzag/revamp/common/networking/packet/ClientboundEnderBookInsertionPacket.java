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

public record ClientboundEnderBookInsertionPacket(int documentId, int pageId, int index, String text) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(documentId);
        buf.writeInt(pageId);
        buf.writeInt(index);
        buf.writeUtf(text);
    }

    public static ClientboundEnderBookInsertionPacket decode(FriendlyByteBuf buf) {
        return new ClientboundEnderBookInsertionPacket(buf.readInt(), buf.readInt(), buf.readInt(), buf.readUtf());
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        Screen scr = Minecraft.getInstance().screen;
        if (scr instanceof EnderBookEditScreen screen && screen.getDocumentId() == documentId) {
            screen.insertPageText(pageId, index, text);
        }
    }
}
