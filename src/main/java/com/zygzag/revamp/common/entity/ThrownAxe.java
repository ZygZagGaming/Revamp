package com.zygzag.revamp.common.entity;

import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ThrownAxe extends ThrownTrident implements ItemSupplier {
    private final ItemStack item;
    public ThrownAxe(EntityType<? extends ThrownAxe> type, Level world) {
        super(type, world);
        this.item = ItemStack.EMPTY;
    }

    public ThrownAxe(Level world, Player owner, ItemStack item) {
        super(Registry.THROWN_AX
        this.setOwner(owner);
        this.pickup = AbstractArrow.Pickup.ALLOWED;
        this.setPos(owner.getX(), owner.getEyeY() - 0.1f, owner.getZ());
        System.out.println(item);
        this.item = item.copy();
    }

    public ItemStack getItem() {
        System.out.println(item);
        return item;
    }
}
