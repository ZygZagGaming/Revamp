package com.zygzag.revamp.common.networking.packet;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.networking.RevampPacketHandler;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record ServerboundEnderBookPageEditPacket(int documentId, int pageId, String newText) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(documentId);
        buf.writeInt(pageId);
        buf.writeUtf(newText);
    }

    public static ServerboundEnderBookPageEditPacket decode(FriendlyByteBuf buf) {
        return new ServerboundEnderBookPageEditPacket(buf.readInt(), buf.readInt(), buf.readUtf());
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ServerPlayer sender = ctx.getSender();
        if (sender != null){
            Level world = sender.server.getLevel(Level.OVERWORLD);
            if (world != null) GeneralUtil.ifCapability(world, Revamp.SERVER_LEVEL_ENDER_BOOK_CAPABILITY, (handler) -> {
                handler.setPageText(documentId, pageId, newText);
            });
            ServerLevel nether = sender.server.getLevel(Level.NETHER);
            ServerLevel end = sender.server.getLevel(Level.END);
            List<ServerPlayer> players = new ArrayList<>(sender.server.overworld().players());
            if (nether != null) players.addAll(nether.players());
            if (end != null) players.addAll(end.players());
            for (ServerPlayer player : players) {
                if (player != sender) RevampPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new ClientboundEnderBookPageEditPacket(documentId, pageId, newText));
            }
        }
        ctx.setPacketHandled(true);
    }
}
