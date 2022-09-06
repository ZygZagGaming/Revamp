package com.zygzag.revamp.common.entity;

import com.zygzag.revamp.common.registry.EntityRegistry;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class CannonFiredShulkerBullet extends ShulkerBullet {
    public static double fireSpeed = 0.2;
    public static double gravitySpeed = 0.004;
    public CannonFiredShulkerBullet(Level world, Direction dir) {
        super(EntityRegistry.CANNON_SHULKER_BULLET.get(), world);
        setDeltaMovement(GeneralUtil.vec3iToVec3(dir.getNormal()).scale(fireSpeed));
    }
    public CannonFiredShulkerBullet(EntityType<? extends CannonFiredShulkerBullet> type, Level world) {
        super(type, world);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide) {
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -gravitySpeed, 0.0D));
            }

            HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                this.onHit(hitresult);
            }
        }

        this.checkInsideBlocks();
        Vec3 vec31 = this.getDeltaMovement();
        this.setPos(this.getX() + vec31.x, this.getY() + vec31.y, this.getZ() + vec31.z);
        ProjectileUtil.rotateTowardsMovement(this, 0.5F);
        if (this.level.isClientSide) {
            this.level.addParticle(ParticleTypes.END_ROD, this.getX() - vec31.x, this.getY() - vec31.y + 0.15D, this.getZ() - vec31.z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        Entity entity1 = this.getOwner();
        LivingEntity livingentity = entity1 instanceof LivingEntity ? (LivingEntity)entity1 : null;
        entity.hurt(DamageSource.indirectMobAttack(this, livingentity).setProjectile(), 1.0F);
        ((ServerLevel)this.level).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 2, 0.2D, 0.2D, 0.2D, 0.0D);
        this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 1.0F);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        BlockState state = level.getBlockState(result.getBlockPos());
        if (state.getBlock() != Blocks.SLIME_BLOCK) super.onHitBlock(result);
        else {
            //Vec3 old = getDeltaMovement();
            setDeltaMovement(getDeltaMovement().multiply(GeneralUtil.absolute(GeneralUtil.vec3iToVec3(result.getDirection().getNormal())).scale(-2).add(1, 1, 1)));
            //System.out.println("hit slime block with direction " + result.getDirection() + ", changed delta from " + old + " to " + getDeltaMovement());
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
    }

    @Override
    protected void onHit(HitResult result) {
        HitResult.Type hitresult$type = result.getType();
        if (hitresult$type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult)result);
            this.level.gameEvent(GameEvent.PROJECTILE_LAND, result.getLocation(), GameEvent.Context.of(this, (BlockState)null));
        } else if (hitresult$type == HitResult.Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult)result;
            this.onHitBlock(blockhitresult);
            BlockPos blockpos = blockhitresult.getBlockPos();
            this.level.gameEvent(GameEvent.PROJECTILE_LAND, blockpos, GameEvent.Context.of(this, this.level.getBlockState(blockpos)));
        }
        if (!(result instanceof BlockHitResult) || level.getBlockState(((BlockHitResult) result).getBlockPos()).getBlock() != Blocks.SLIME_BLOCK) this.discard();
    }
}
