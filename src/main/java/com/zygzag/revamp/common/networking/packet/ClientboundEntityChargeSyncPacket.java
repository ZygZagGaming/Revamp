package com.zygzag.revamp.common.networking.packet;

import com.zygzag.revamp.common.Revamp;
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
        Level world = Minecraft.getInstance().level;
        if (world == null) return null;
        return new ClientboundEntityChargeSyncPacket(buf.readUUID(), buf.readFloat(), buf.readFloat());
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        Level world = Minecraft.getInstance().level;
        //System.out.println("received sync packet w/ charge: " + newCharge + " and max charge: " + newMaxCharge);
        if (world != null && world.isClientSide) {
            Entity entity = world.getEntities().get(uuid);
            if (entity != null) GeneralUtil.ifCapability(entity, Revamp.ENTITY_CHARGE_CAPABILITY, (handler) -> {
                handler.setCharge(newCharge);
                handler.setMaxCharge(newMaxCharge);
            });
        }
        ctx.setPacketHandled(true);
    }
}
