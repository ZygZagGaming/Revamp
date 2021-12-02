package com.zygzag.revamp.item.tier;

import com.zygzag.revamp.Revamp;
import com.zygzag.revamp.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
public enum ModTiers implements Tier {
    IRIDIUM(5, 2589, 8.85F, 5.5F, 18, () -> {
        return Ingredient.of(Registry.IRIDIUM_PLATING.get());
    });

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    ModTiers(int level, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = new LazyLoadedValue<>(repairIngredient);
    }

    public int getUses() {
        return this.uses;
    }

    public float getSpeed() {
        return this.speed;
    }

    public float getAttackDamageBonus() {
        return this.damage;
    }

    public int getLevel() {
        return this.level;
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    public net.minecraft.tags.Tag<net.minecraft.world.level.block.Block> getTag() { return Revamp.NEEDS_IRIDIUM_TOOL_TAG; }

}
