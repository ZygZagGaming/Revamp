package com.zygzag.revamp.common.networking.packet;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.util.ClientUtils;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

public class ClientboundEntityChargeSyncPacket {
    public UUID uuid;
    public float newCharge;
    public float newMaxCharge;

    public ClientboundEntityChargeSyncPacket(UUID entityUUID, float newCharge, float newMaxCharge) {
        this.uuid = entityUUID;
        this.newCharge = newCharge;
        this.newMaxCharge = newMaxCharge;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
        buf.writeFloat(newCharge);
        buf.writeFloat(newMaxCharge);
    }

    @Nullable
    public static ClientboundEntityChargeSyncPacket decode(FriendlyByteBuf buf) {
        if (Minecraft.getInstance().level.isClientSide) return ClientUtils.decodeClientboudnEntityChargeSyncPacket(buf);
        else return null;
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        if (Minecraft.getInstance().level.isClientSide) ClientUtils.syncEntityCharge(ctxSupplier, uuid, newCharge, newMaxCharge);
    }
}
