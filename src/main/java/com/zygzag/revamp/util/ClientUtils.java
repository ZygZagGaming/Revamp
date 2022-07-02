package com.zygzag.revamp.util;

import com.zygzag.revamp.client.render.screen.EnderBookEditScreen;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.charge.Arc;
import com.zygzag.revamp.common.charge.EnergyCharge;
import com.zygzag.revamp.common.networking.packet.ClientboundChargeUpdatePacket;
import com.zygzag.revamp.common.networking.packet.ClientboundEntityChargeSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class ClientUtils {
    public static void openEnderBookScreen(Player player, ItemStack stack, InteractionHand hand) {
        Minecraft.getInstance().setScreen(new EnderBookEditScreen(player, stack, hand));
    }

    public static void chargeUpdate(Map<BlockPos, EnergyCharge> toSync, List<BlockPos> toRemove) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            GeneralUtil.ifCapability(world, Revamp.CLIENT_LEVEL_CHARGE_CAPABILITY, (handler) -> {
                handler.charges.putAll(toSync);
                for (BlockPos pos : toRemove) {
                    handler.charges.remove(pos);
                }
            });
        }
    }

    @Nullable
    public static ClientboundChargeUpdatePacket decodeClientboundChargeUpdatePacket(FriendlyByteBuf buf) {
        Map<BlockPos, EnergyCharge> map = new HashMap<>();
        List<BlockPos> toRemove = new ArrayList<>();
        ClientLevel world = Minecraft.getInstance().level;
        if (world != null) {
            int n = buf.readInt();
            for (int i = 0; i < n; i++) {
                BlockPos pos = buf.readBlockPos();
                map.put(pos, EnergyCharge.decode(buf, world));
            }
            int k = buf.readInt();
            for (int i = 0; i < k; i++) {
                toRemove.add(buf.readBlockPos());
            }
            return new ClientboundChargeUpdatePacket(map, toRemove, new ChunkPos(buf.readInt(), buf.readInt()));
        }
        return null;
    }

    public static ClientboundEntityChargeSyncPacket decodeClientboudnEntityChargeSyncPacket(FriendlyByteBuf buf) {
        Level world = Minecraft.getInstance().level;
        if (world == null) return null;
        return new ClientboundEntityChargeSyncPacket(buf.readUUID(), buf.readFloat(), buf.readFloat());
    }

    public static void createArc(Supplier<NetworkEvent.Context> ctxSupp, Arc arc) {
        NetworkEvent.Context ctx = ctxSupp.get();
        Level world = Minecraft.getInstance().level;
        if (world != null && world.isClientSide) {
            GeneralUtil.ifCapability(world, Revamp.ARC_CAPABILITY, (handler) -> {
                handler.add(arc);
            });
        }
        ctx.setPacketHandled(true);
    }

    public static void syncEntityCharge(Supplier<NetworkEvent.Context> ctxSupplier, UUID uuid, float newCharge, float newMaxCharge) {
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
