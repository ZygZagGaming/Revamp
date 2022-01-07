package com.zygzag.revamp.common.entity;

import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ThrownAxe extends ThrownTrident {
    private ItemStack item;
    public ThrownAxe(EntityType<? extends ThrownAxe> type, Level world) {
        super(type, world);
    }

    public ThrownAxe(Level world, Player owner, ItemStack item) {
        super(world, owner, item);
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }
}
