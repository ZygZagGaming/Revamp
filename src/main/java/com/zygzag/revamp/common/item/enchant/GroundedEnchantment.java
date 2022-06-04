package com.zygzag.revamp.common.item.enchant;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class GroundedEnchantment extends RevampEnchantment {
    public GroundedEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[]{EquipmentSlot.FEET});
    }

    @Override
    public int getMinCost(int level) {
        return 20;
    }

    @Override
    public int getMaxCost(int level) {
        return 30;
    }

    @Override
    public int getMaxLevel() {
        return 2;
    }
}
