package com.zygzag.revamp.common.item.recipe;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class ItemAndEntityHolder extends ItemHolder {
    public Entity entity;
    public ItemAndEntityHolder(ItemStack stack, Entity entity) {
        super(stack);
        this.entity = entity;
    }
}
