package com.zygzag.revamp.common.item.tier;

import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

@MethodsReturnNonnullByDefault
public enum IridiumToolTier implements Tier {
    FULL(2768, 15, 5, 5, 18),
    HALF(2364, 15, 4.5f, 5, 18),
    _3_1(2204, 15, 4.33f, 5, 18),
    _3_2(2560, 15, 4.66f, 5, 18),
    DIAMOND_SOCKETED(2894, 16, 5.33f, 6, 12),
    DIAMOND_SOCKETED_PICK(2894, 19, 5.33f, 6, 12),
    EMERALD_SOCKETED(2648, 17, 4f, 5, 21),
    WITHER_SOCKETED_PICK(2768, Integer.MAX_VALUE, 5, 5, 18);

    int uses;
    float speed;
    float damage;
    int level;
    int enchantLevel;

    IridiumToolTier(int uses, float speed, float damage, int level, int enchantLevel) {
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.level = level;
        this.enchantLevel = enchantLevel;
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantLevel;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Registry.IRIDIUM_PLATING.get());
    }
}
