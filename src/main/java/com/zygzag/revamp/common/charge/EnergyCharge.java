package com.zygzag.revamp.common.charge;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.capability.ChunkChargeHandler;
import com.zygzag.revamp.common.tag.RevampTags;
import com.zygzag.revamp.util.Constants;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EnergyCharge {
    private float charge;
    private int lifetime = 0;
    private BlockPos pos;
    private ChunkChargeHandler handler;
    private final Level world;
    public boolean dirty = true;

    public EnergyCharge(float charge, BlockPos pos, ChunkChargeHandler handler) {
        this.charge = charge;
        this.pos = pos;
        this.handler = handler;
        this.world = handler.getChunk().getWorldForge();
        //System.out.println("charge created");
    }

    public float getCharge() {
        return charge;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setCharge(float charge) {
        this.charge = charge;
        markDirty();
    }

    public void moveTo(BlockPos pos) {
        this.pos = pos;
        if (isInside(pos, handler.getChunk().getPos())) {
            handler.remove(this);
            handler.charges.put(pos, this);
        } else {
            if (world != null) {
                LevelChunk next = world.getChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
                handler.remove(this);
                GeneralUtil.ifCapability(next, Revamp.CHUNK_CHARGE_CAPABILITY, (h) -> {
                    h.charges.put(pos, this);
                    this.handler = h;
                });
            }
        }
        markDirty();
    }

    private static boolean isInside(BlockPos pos, ChunkPos chunk) {
        return pos.getX() >= chunk.getMinBlockX() && pos.getX() <= chunk.getMaxBlockX() && pos.getZ() >= chunk.getMinBlockZ() && pos.getZ() <= chunk.getMaxBlockZ();
    }

    public void remove() {
        handler.remove(this);
    }

    public boolean canSurvive() {
        return Revamp.CONDUCTIVENESS.isConductor(world.getBlockState(pos).getBlock());
    }

    public void tick() {
        lifetime++;
        RandomSource rng = world.getRandom();
        if (rng.nextDouble() < Math.abs(charge) / 20f) {
            VoxelShape shape = world.getBlockState(pos).getCollisionShape(world, pos);
            if (!shape.isEmpty()) {
                Vec3 point = GeneralUtil.randomPointOnAABB(shape.bounds().inflate(0.05), rng).add(pos.getX() - 0.075, pos.getY() - 0.075, pos.getZ() - 0.075);
                world.addParticle(GeneralUtil.particle(charge), point.x, point.y, point.z, 0, 0, 0);
            }
        }
        if (!canSurvive() || Math.abs(charge) < Constants.EPSILON) remove();
        if (!world.isClientSide) {
            float range = world.getBlockState(pos).is(RevampTags.EXTENDED_ARCS.get()) ? Constants.EXTENDED_ARC_RANGE : Constants.ARC_RANGE;
            List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(range), Constants.CHARGEABLE_PREDICATE);
            LivingEntity living = GeneralUtil.minByOrNull(entities, (e) -> e.distanceToSqr(Vec3.atCenterOf(pos)));
            if (living != null) GeneralUtil.ifCapability(living, Revamp.ENTITY_CHARGE_CAPABILITY, (handler) -> {
                float c = handler.getCharge();
                handler.setCharge(c + charge);
                Vec3 entityPos = living.getBoundingBox().getCenter();
                Vec3 thisPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                new Arc(thisPos, entityPos, (int) Math.abs(charge)).sendToClients();
                charge = 0;
            });
            if (Math.abs(charge) > Constants.EPSILON) spread();
        }
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void markClean() {
        this.dirty = false;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(charge);
        buf.writeBlockPos(pos);
    }

    @Nullable
    public static EnergyCharge decode(FriendlyByteBuf buf, Level world) {
        float charge = buf.readFloat();
        BlockPos pos = buf.readBlockPos();
        return GeneralUtil.ifCapabilityMap(world.getChunkAt(pos), Revamp.CHUNK_CHARGE_CAPABILITY, (h) -> new EnergyCharge(charge, pos, h));
    }

    public boolean canSpreadTo(BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        // System.out.println("can " + (Revamp.CONDUCTIVENESS.isInsulator(block) ? "not " : "") + "spread to block " + block);
        return !Revamp.CONDUCTIVENESS.isInsulator(block);
    }

    public void spread() {
        List<Direction> dirs = Arrays.stream(Direction.values()).filter((it) -> canSpreadTo(pos.relative(it))).toList();
        if (dirs.size() == 0) return;
        float c = 0;
        for (Direction dir : dirs) {
            if (Revamp.CONDUCTIVENESS.isConductor(world.getBlockState(pos.relative(dir)).getBlock())) {
                float blockCharge = GeneralUtil.getChargeAt(world, pos.relative(dir));
                c += (charge - blockCharge) / 2;
                GeneralUtil.setChargeAt(world, pos.relative(dir), (charge + blockCharge) / 2);
            } else {
                c += charge / 2;
            }
        }
        setCharge(charge - c);
    }

    public static void addOrCreateCharge(Level world, BlockPos pos, float value) {
        LevelChunk chunk = world.getChunkAt(pos);
        GeneralUtil.ifCapability(chunk, Revamp.CHUNK_CHARGE_CAPABILITY, (handler) -> {
            if (handler.charges.containsKey(pos)) {
                EnergyCharge c = handler.charges.get(pos);
                c.charge += value;
                c.markDirty();
            } else {
                handler.charges.put(pos, new EnergyCharge(value, pos, handler));
            }
        });
    }

    public double arcDistance() {
        return charge / 6;
    }
}
