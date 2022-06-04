package com.zygzag.revamp.common.item.enchant;

import com.zygzag.revamp.util.Constants;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VoltageEnchantment extends RevampEnchantment {
    private final float chargePerTick;
    public VoltageEnchantment(float chargePerTick) {
        super(Rarity.VERY_RARE, EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
        this.chargePerTick = chargePerTick;
    }

    @Override
    public int getColor(int level) {
        return chargePerTick > 0 ? Constants.VOLTAGE_POSITIVE_COLOR : Constants.VOLTAGE_NEGATIVE_COLOR;
    }

    public float getChargePerTick() {
        return chargePerTick;
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
        return 1;
    }

    @Override
    protected boolean checkCompatibility(Enchantment enchant) {
        return !(enchant instanceof VoltageEnchantment) || enchant == this;
    }
}
