package com.zygzag.revamp.common.networking.packet;

import com.zygzag.revamp.util.ClientUtils;
import net.minecraft.network.FriendlyByteBuf;
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
    	return ClientUtils.decodeClientboudnEntityChargeSyncPacket(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
    	NetworkEvent.Context ctx = ctxSupplier.get();
    	ClientUtils.syncEntityCharge(uuid, newCharge, newMaxCharge);
    	ctx.setPacketHandled(true);
    }
}
