package com.zygzag.revamp.common.capability;

import com.zygzag.revamp.common.charge.EnergyCharge;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class ClientLevelChargeHandler {
    public Map<BlockPos, EnergyCharge> charges = new HashMap<>();
    public final Level world;
    public ClientLevelChargeHandler(Level world) {
        this.world = world;
    }

    public void tick() {
        for (Map.Entry<BlockPos, EnergyCharge> entry : charges.entrySet()) {
            entry.getValue().tick();
        }
    }
}
