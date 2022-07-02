package com.zygzag.revamp.common.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EmpoweredWitherHeadPart extends PartEntity<EmpoweredWither> {
    public final EmpoweredWither parentMob;
    public final String name;
    private final EntityDimensions size;
    private final float damageMultiplier, leftOffset, vOffset, forwardOffset;

    public EmpoweredWitherHeadPart(EmpoweredWither parent, String name, float width, float height, float damageMultiplier, float leftOffset, float vOffset, float forwardOffset) {
        super(parent);
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
        this.parentMob = parent;
        this.name = name;
        this.damageMultiplier = damageMultiplier;
        this.leftOffset = leftOffset;
        this.vOffset = vOffset;
        this.forwardOffset = forwardOffset;
    }

    void tickPos() {
        double oldX = getX();
        double oldY = getY();
        double oldZ = getZ();
        float rot = parentMob.getYRot();
        float x = (float) (leftOffset*Math.cos(rot)-forwardOffset*Math.sin(rot));
        float z = (float) (leftOffset*Math.sin(rot)+forwardOffset*Math.cos(rot));
        Vec3 parentPos = parentMob.position();
        setPos(parentPos.add(x, vOffset, z));

        xo = oldX;
        xOld = oldX;
        yo = oldY;
        yOld = oldY;
        zo = oldZ;
        zOld = oldZ;

        //System.out.println(name + ": " + position());
    }

    @Override
    protected void defineSynchedData() { }

    @Override
    protected void readAdditionalSaveData(CompoundTag data) { }

    @Override
    protected void addAdditionalSaveData(CompoundTag data) { }

    public boolean isPickable() {
        return true;
    }

    public boolean hurt(DamageSource source, float amount) {
        return !this.isInvulnerableTo(source) && this.parentMob.hurt(this, source, amount * damageMultiplier);
    }

    public boolean is(Entity entity) {
        return this == entity || this.parentMob == entity;
    }

    public Packet<?> getAddEntityPacket() {
        throw new UnsupportedOperationException();
    }

    public EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }

    public boolean shouldBeSaved() {
        return false;
    }

    // revamp: entity methods that I decided to add to all my entities (i would add them to the vanilla Entity class if i could)
    public Vec3 oldPos() {
        return new Vec3(xOld, yOld, zOld);
    }

    public Vec3 position(float partialTick) {
        return oldPos().lerp(position(), partialTick);
    }
}
