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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.annotation.Nullable;
import java.util.*;

public class ClientUtils {
	
	private static Optional<Minecraft> MC_INSTANCE = Optional.empty();
	
    public static void openEnderBookScreen(Player player, ItemStack stack, InteractionHand hand) {
    	MC_INSTANCE.ifPresent( inst -> {
    		inst.setScreen(new EnderBookEditScreen(player, stack, hand));
    	});
    }

    public static void chargeUpdate(Map<BlockPos, EnergyCharge> toSync, List<BlockPos> toRemove) {
    	
    	MC_INSTANCE.ifPresent(inst -> {
            ClientLevel world = inst.level;
            if (world != null) {
                GeneralUtil.ifCapability(world, Revamp.CLIENT_LEVEL_CHARGE_CAPABILITY, (handler) -> {
                    handler.charges.putAll(toSync);
                    for (BlockPos pos : toRemove) {
                        handler.charges.remove(pos);
                    }
                });
            }
    	});
    }

    @SuppressWarnings("resource")
	@Nullable
    public static ClientboundChargeUpdatePacket decodeClientboundChargeUpdatePacket(FriendlyByteBuf buf) {
    	if (MC_INSTANCE.isPresent()) {
	        Map<BlockPos, EnergyCharge> map = new HashMap<>();
	        List<BlockPos> toRemove = new ArrayList<>();
	        ClientLevel world = MC_INSTANCE.get().level;
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
    	}
		return null;
    }

    @SuppressWarnings("resource")
	public static ClientboundEntityChargeSyncPacket decodeClientboudnEntityChargeSyncPacket(FriendlyByteBuf buf) {
    	if(MC_INSTANCE.isPresent()) {
            ClientLevel world = MC_INSTANCE.get().level;
            if (world == null) return null;
            return new ClientboundEntityChargeSyncPacket(buf.readUUID(), buf.readFloat(), buf.readFloat());
    	}
    	return null;
    }

    public static void createArc(Arc arc) {
    	MC_INSTANCE.ifPresent( inst -> {
            ClientLevel world = inst.level;
            if (world != null) {
                GeneralUtil.ifCapability(world, Revamp.ARC_CAPABILITY, (handler) -> {
                    handler.add(arc);
                });
            }
    	});
    }

    public static void syncEntityCharge(UUID uuid, float newCharge, float newMaxCharge) {
    	MC_INSTANCE.ifPresent(inst -> {
            ClientLevel world = inst.level;
            //System.out.println("received sync packet w/ charge: " + newCharge + " and max charge: " + newMaxCharge);
            if (world != null) {
                Entity entity = world.getEntities().get(uuid);
                if (entity != null) GeneralUtil.ifCapability(entity, Revamp.ENTITY_CHARGE_CAPABILITY, (handler) -> {
                    handler.setCharge(newCharge);
                    handler.setMaxCharge(newMaxCharge);
                });
            }
    	});
    }
    
    //static initializer to ensure that the client version has the MC handle.
    static {
    	if(FMLEnvironment.dist == Dist.CLIENT) {
    		MC_INSTANCE = Optional.of(Minecraft.getInstance());
    		Revamp.LOGGER.debug("MC_INSTANCE found? {} ", MC_INSTANCE.get() != null);
    	}
    }
}
