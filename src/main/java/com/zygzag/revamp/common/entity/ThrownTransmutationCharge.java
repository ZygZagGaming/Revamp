package com.zygzag.revamp.common.entity;

import com.zygzag.revamp.common.item.recipe.TransmutationRecipe;
import com.zygzag.revamp.common.registry.EntityRegistry;
import com.zygzag.revamp.common.registry.ItemRegistry;
import com.zygzag.revamp.common.registry.RecipeTypeRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ThrownTransmutationCharge extends ThrowableItemProjectile {
    public ThrownTransmutationCharge(EntityType<? extends ThrownTransmutationCharge> type, Level world) {
        super(type, world);
    }

    public ThrownTransmutationCharge(Level world, double xPos, double yPos, double zPos) {
        super(EntityRegistry.TRANSMUTATION_BOTTLE_ENTITY.get(), xPos, yPos, zPos, world);
    }

    public ThrownTransmutationCharge(Level world, LivingEntity thrower) {
        super(EntityRegistry.TRANSMUTATION_BOTTLE_ENTITY.get(), thrower, world);
    }

    protected Item getDefaultItem() {
        return ItemRegistry.TRANSMUTATION_CHARGE.get();
    }

    protected float getGravity() {
        return 0.07F;
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        if (this.level instanceof ServerLevel) {
            this.level.levelEvent(2002, this.blockPosition(), 0x727175);
            AABB box = getBoundingBox().inflate(5.0);
            List<ItemEntity> entities = level.getEntitiesOfClass(ItemEntity.class, box);
            List<TransmutationRecipe> recipes = level.getRecipeManager().getAllRecipesFor(RecipeTypeRegistry.TRANSMUTATION.get());
            for (ItemEntity itemEntity : entities) {
                for (TransmutationRecipe recipe : recipes) {
                    SimpleContainer holder = new SimpleContainer(itemEntity.getItem());
                    if (recipe.matches(holder, level)) {
                        ItemEntity newItem = new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), recipe.assemble(holder));
                        level.addFreshEntity(newItem);
                        itemEntity.kill();
                    }
                }
            }
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
