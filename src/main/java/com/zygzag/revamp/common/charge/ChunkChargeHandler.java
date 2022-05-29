package com.zygzag.revamp.common.charge;

import com.zygzag.revamp.common.networking.RevampPacketHandler;
import com.zygzag.revamp.common.networking.packet.ClientboundChargeUpdatePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;

public class ChunkChargeHandler {
    public final Map<BlockPos, EnergyCharge> charges = new HashMap<>();
    private final Level world;
    private final ChunkPos pos;
    private final LevelChunk chunk;
    public boolean dirty = false;
    private List<BlockPos> toRemove = new ArrayList<>();

    public ChunkChargeHandler(LevelChunk chunk) {
        this.chunk = chunk;
        this.world = chunk.getWorldForge();
        this.pos = chunk.getPos();
    }

    public LevelChunk getChunk() {
        return chunk;
    }

    public void add(EnergyCharge charge) {
        charges.put(charge.getPos(), charge);
        markDirty();
    }

    public void tick() {
        Map<BlockPos, EnergyCharge> dirty = new HashMap<>();
        for (Map.Entry<BlockPos, EnergyCharge> entry : new HashSet<>(charges.entrySet())) {
            entry.getValue().tick();
            if (entry.getValue().dirty) {
                dirty.put(entry.getKey(), entry.getValue());
                entry.getValue().markClean();
            }
        }
        if (!world.isClientSide && (dirty.size() > 0 || (this.dirty && toRemove.size() > 0))) {
            ClientboundChargeUpdatePacket packet = new ClientboundChargeUpdatePacket(dirty, toRemove, chunk.getPos());
            markClean();
            RevampPacketHandler.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
        }
    }

    public void remove(EnergyCharge charge) {
        charges.remove(charge.getPos());
        toRemove.add(charge.getPos());
        markDirty();
    }

    public void markClean() {
        dirty = false;
        toRemove = new ArrayList<>();
    }

    public void markDirty() {
        this.dirty = true;
    }
}
