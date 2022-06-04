package com.zygzag.revamp.common.item.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DynamoEnchantment extends RevampEnchantment {
    public DynamoEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_LEGS, new EquipmentSlot[]{EquipmentSlot.LEGS});
    }

    @Override
    public int getMinCost(int level) {
        return 25;
    }

    @Override
    public int getMaxCost(int level) {
        return 30;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
