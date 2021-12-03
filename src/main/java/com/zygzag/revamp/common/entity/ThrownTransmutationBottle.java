package com.zygzag.revamp.common.entity;

import com.zygzag.revamp.common.item.recipe.ItemHolder;
import com.zygzag.revamp.common.item.recipe.ModRecipeType;
import com.zygzag.revamp.common.item.recipe.TransmutationRecipe;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ThrownTransmutationBottle extends ThrowableItemProjectile {
    public ThrownTransmutationBottle(EntityType<? extends ThrownTransmutationBottle> type, Level world) {
        super(type, world);
    }

    public ThrownTransmutationBottle(Level world, double xPos, double yPos, double zPos) {
        super(Registry.TRANSMUTATION_BOTTLE_ENTITY.get(), xPos, yPos, zPos, world);
    }

    public ThrownTransmutationBottle(Level world, LivingEntity thrower) {
        super(Registry.TRANSMUTATION_BOTTLE_ENTITY.get(), thrower, world);
    }

    protected Item getDefaultItem() {
        return Registry.TRANSMUTATION_BOTTLE.get();
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
            List<TransmutationRecipe> recipes = level.getRecipeManager().getAllRecipesFor(ModRecipeType.TRANSMUTATION);
            for (ItemEntity itemEntity : entities) {
                for (TransmutationRecipe recipe : recipes) {
                    ItemHolder holder = new ItemHolder(itemEntity.getItem());
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
}
