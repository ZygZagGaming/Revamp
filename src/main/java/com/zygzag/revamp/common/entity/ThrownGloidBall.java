package com.zygzag.revamp.common.entity;

import com.zygzag.revamp.common.registry.EntityRegistry;
import com.zygzag.revamp.common.registry.ItemRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ThrownGloidBall extends ThrowableItemProjectile {
    public ThrownGloidBall(EntityType<? extends ThrownGloidBall> type, Level world) {
        super(type, world);
    }

    public ThrownGloidBall(Level world, double xPos, double yPos, double zPos) {
        super(EntityRegistry.THROWN_GLOID_BALL.get(), xPos, yPos, zPos, world);
    }

    public ThrownGloidBall(Level world, LivingEntity thrower) {
        super(EntityRegistry.THROWN_GLOID_BALL.get(), thrower, world);
    }

    protected Item getDefaultItem() {
        return ItemRegistry.GLOID_BALL.get();
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level instanceof ServerLevel) {
            this.level.levelEvent(2002, this.blockPosition(), 0x632873);
            GloidBubbleEntity bubble = new GloidBubbleEntity(EntityRegistry.GLOID_BUBBLE.get(), level);
            bubble.setSize(10);
            bubble.setPos(position());
            level.addFreshEntity(bubble);
            this.discard();
        }

    }

    // revamp: entity methods that I decided to add to all my entities (i would add them to the vanilla Entity class if i could)
    public Vec3 oldPos() {
        return new Vec3(xOld, yOld, zOld);
    }

    public Vec3 position(float partialTick) {
        return oldPos().lerp(position(), partialTick);
    }
}
