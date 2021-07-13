package com.zygzag.revamp.common.item;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ShulkerBowlItem extends Item {
    public ShulkerBowlItem(Properties properties) {
        super(properties.food(new Food.Builder().alwaysEat().build()));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 33;
    }
}
