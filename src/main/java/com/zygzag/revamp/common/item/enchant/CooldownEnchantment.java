package com.zygzag.revamp.common.item.enchant;

import com.zygzag.revamp.common.Revamp;
import net.minecraft.world.entity.EquipmentSlot;

public class CooldownEnchantment extends RevampEnchantment {
    public CooldownEnchantment(Rarity rarity) {
        super(rarity, Revamp.COOLDOWN_CATEGORY, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        return 1 + 15 * level;
    }

    @Override
    public int getMaxCost(int level) {
        return 1 + 20 * level;
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }
}
