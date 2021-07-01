package com.zygzag.revamp.tier;

import com.zygzag.revamp.Registry;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;

public enum IridiumToolTier implements IItemTier {
    // Ugh..
    FULL(2768, 15, 5, 5, 18),
    HALF(2364, 15, 4.5f, 5, 18),
    _3_1(2204, 15, 4.33f, 5, 18),
    _3_2(2560, 15, 4.66f, 5, 18);

    int uses;
    float speed;
    float damage;
    int level;
    int enchantLevel;

    private IridiumToolTier(int uses, float speed, float damage, int level, int enchantLevel) {
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
