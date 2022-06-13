package com.zygzag.revamp.common.networking.packet;

import com.zygzag.revamp.client.render.screen.EnderBookEditScreen;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundEnderBookPageEditPacket(int documentId, int pageId, String newText) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(documentId);
        buf.writeInt(pageId);
        buf.writeUtf(newText);
    }

    public static ClientboundEnderBookPageEditPacket decode(FriendlyByteBuf buf) {
        return new ClientboundEnderBookPageEditPacket(buf.readInt(), buf.readInt(), buf.readUtf());
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        Screen scr = Minecraft.getInstance().screen;
        if (scr instanceof EnderBookEditScreen screen && screen.getDocumentId() == documentId) {
            screen.setPageText(pageId, newText);
        }
    }
}
