package com.zygzag.revamp.common.networking.packet;

import com.zygzag.revamp.common.charge.EnergyCharge;
import com.zygzag.revamp.util.ClientUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class ClientboundChargeUpdatePacket {
    private final Map<BlockPos, EnergyCharge> toSync;
    private final List<BlockPos> toRemove;
    private final ChunkPos pos;

    public ClientboundChargeUpdatePacket(Map<BlockPos, EnergyCharge> toSync, List<BlockPos> toRemove, ChunkPos pos) {
        this.toSync = toSync;
        this.toRemove = toRemove;
        this.pos = pos;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(toSync.size());
        for (Map.Entry<BlockPos, EnergyCharge> entry : toSync.entrySet()) {
            buf.writeBlockPos(entry.getKey());
            entry.getValue().encode(buf);
        }
        buf.writeInt(toRemove.size());
        for (BlockPos pos : toRemove) {
            buf.writeBlockPos(pos);
        }
        buf.writeInt(pos.x);
        buf.writeInt(pos.z);
    }

    public static ClientboundChargeUpdatePacket decode(FriendlyByteBuf buf) {
    	return ClientUtils.decodeClientboundChargeUpdatePacket(buf);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ClientUtils.chargeUpdate(toSync, toRemove);
        ctx.setPacketHandled(true);
    }
}
