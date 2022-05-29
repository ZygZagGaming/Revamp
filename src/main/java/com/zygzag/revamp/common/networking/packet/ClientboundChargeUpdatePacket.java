package com.zygzag.revamp.common.networking.packet;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.charge.ChunkChargeHandler;
import com.zygzag.revamp.common.charge.ClientLevelChargeHandler;
import com.zygzag.revamp.common.charge.EnergyCharge;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nullable;
import java.util.*;
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

    @Nullable
    public static ClientboundChargeUpdatePacket decode(FriendlyByteBuf buf) {
        Map<BlockPos, EnergyCharge> map = new HashMap<>();
        List<BlockPos> toRemove = new ArrayList<>();
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            int n = buf.readInt();
            for (int i = 0; i < n; i++) {
                map.put(buf.readBlockPos(), EnergyCharge.decode(buf, world));
            }
            int k = buf.readInt();
            for (int i = 0; i < k; i++) {
                toRemove.add(buf.readBlockPos());
            }
            return new ClientboundChargeUpdatePacket(map, toRemove, new ChunkPos(buf.readInt(), buf.readInt()));
        }
        return null;
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context ctx = contextSupplier.get();
        ctx.enqueueWork(() ->
            // Make sure it's only executed on the physical client
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientLevel world = Minecraft.getInstance().level;
                if (world != null) {
                    GeneralUtil.ifCapability(world, Revamp.CLIENT_LEVEL_CHARGE_CAPABILITY, (handler) -> {
                        handler.charges.putAll(toSync);
                        for (BlockPos pos : toRemove) {
                            handler.charges.remove(pos);
                        }
                    });
                }
            })
        );
        ctx.setPacketHandled(true);
    }
}
