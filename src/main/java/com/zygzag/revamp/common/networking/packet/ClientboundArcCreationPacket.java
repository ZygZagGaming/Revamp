package com.zygzag.revamp.common.networking.packet;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.charge.Arc;
import com.zygzag.revamp.common.charge.ArcHandler;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ClientboundArcCreationPacket {
    private Arc arc;
    public ClientboundArcCreationPacket(Arc arc) {
        this.arc = arc;
    }

    public static void writeVec3(FriendlyByteBuf buf, Vec3 vec) {
        buf.writeDouble(vec.x);
        buf.writeDouble(vec.y);
        buf.writeDouble(vec.z);
    }

    public static Vec3 readVec3(FriendlyByteBuf buf) {
        return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public void encode(FriendlyByteBuf buf) {
        writeVec3(buf, arc.start);
        writeVec3(buf, arc.end);
        buf.writeInt(arc.between);
        buf.writeInt(arc.lifetime);
    }

    public static ClientboundArcCreationPacket decode(FriendlyByteBuf buf) {
        return new ClientboundArcCreationPacket(new Arc(readVec3(buf), readVec3(buf), buf.readInt(), buf.readInt()));
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupp) {
        NetworkEvent.Context ctx = ctxSupp.get();
        Level world = Minecraft.getInstance().level;
        if (world != null && world.isClientSide) {
            GeneralUtil.ifCapability(world, Revamp.ARC_CAPABILITY, (handler) -> {
                handler.add(arc);
            });
        }
        ctx.setPacketHandled(true);
    }
}
