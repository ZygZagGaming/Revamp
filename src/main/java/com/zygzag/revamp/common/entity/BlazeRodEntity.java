package com.zygzag.revamp.common.entity;

import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlazeRodEntity extends PartEntity<RevampedBlaze> {
    public State state = State.ORBIT;
    public Type type = Type.REGULAR;
    private final RevampedBlaze blaze;
    private final EntityDimensions size;
    private int index;
    private double angle;

    public BlazeRodEntity(RevampedBlaze blaze, int index, double angle) {
        super(blaze);
        this.blaze = blaze;
        this.size = EntityDimensions.scalable(2 / 16f, 8 / 16f);
        this.refreshDimensions();
        this.dimensions = size;
        this.index = index;
        this.angle = angle;
        setPos(blaze.position());
        setXRot(180f);
    }

    public EntityDimensions getDimensions(Pose p_31023_) {
        return this.size;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        super.tick();
        tickCount++;
        setOldPosAndRot();
        refreshPosition();
    }

    @Override
    public boolean hurt(DamageSource src, float amt) {
        return blaze.hurt(src, amt);
    }

    public boolean isPickable() {
        return true;
    }

    public int layer() {
        return index / 4;
    }

    private void refreshPosition() {
        Vec3 posToUse = blaze.pastPosition((blaze.totalLayers() - layer()));
        double k2 = GeneralUtil.rem((tickCount * 0.075 * Math.PI), (2 * Math.PI));
        double offset, k, r;
        if (index < 4) {
            k = GeneralUtil.rem((tickCount * 0.01875 * Math.PI), (2 * Math.PI));
            offset = 0.25;
            r = 0.4375;
            setPos(posToUse.add(Math.cos(k + index * (Math.PI / 2) + angle) * r, Math.cos(k2 + index * 0.15 + angle) / 9 + offset, Math.sin(k + index * (Math.PI / 2) + angle) * r));
        } else if (index < 8) {
            k = GeneralUtil.rem((tickCount * -0.0375 * Math.PI), (2 * Math.PI));
            offset = 0.75;
            r = 0.5;
            setPos(posToUse.add(Math.cos(k + (index - 4) * (Math.PI / 2) + angle) * r, Math.cos(k2 + index * 0.25 + angle) / 9 + offset, Math.sin(k + (index - 4) * (Math.PI / 2) + angle) * r));
        } else {
            k = GeneralUtil.rem((tickCount * 0.075 * Math.PI), (2 * Math.PI));
            offset = 1.25;
            r = 0.5625;
            setPos(posToUse.add(Math.cos(k + (index - 8) * (Math.PI / 2) + angle) * r, Math.cos(k2 + index * 0.35 + angle) / 9 + offset, Math.sin(k + (index - 8) * (Math.PI / 2) + angle) * r));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.type = Type.valueOf(tag.getString("type"));
        this.state = State.valueOf(tag.getString("state"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("type", type.toString());
        tag.putString("state", state.toString());
    }

    public enum State {
        ORBIT,
        STATIC,
        GUARD,
        ATTACK
    }

    public enum Type {
        REGULAR,
        SHIELD,
        DART
    }

    public boolean is(Entity entity) {
        return this == entity || this.blaze == entity;
    }

    // revamp: entity methods that I decided to add to all my entities (i would add them to the vanilla Entity class if i could)
    public Vec3 oldPos() {
        return new Vec3(xOld, yOld, zOld);
    }

    public Vec3 position(float partialTick) {
        return oldPos().lerp(position(), partialTick);
    }
}
