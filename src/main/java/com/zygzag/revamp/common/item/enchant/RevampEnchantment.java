package com.zygzag.revamp.common.item.enchant;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.swing.text.AttributeSet;

/**
 * Adds some color functionality to the base Enchantment class.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class RevampEnchantment extends Enchantment {
    protected RevampEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
        super(rarity, category, slots);
    }

    @Override
    public Component getFullname(int level) {
        MutableComponent mutablecomponent = Component.translatable(this.getDescriptionId());
        mutablecomponent.withStyle(Style.EMPTY.withColor(getColor(level)));

        if (level != 1 || this.getMaxLevel() != 1) {
            mutablecomponent.append(" ").append(Component.translatable("enchantment.level." + level));
        }

        return mutablecomponent;
    }

    public int getColor(int level) {
        if (this.isCurse()) {
            return 0xff5555;
        } else {
            return 0xaaaaaa;
        }
    }
}
