package com.zygzag.revamp.common.item.tier;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.registry.ItemRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.tags.TagKey;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeTier;

import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
public class ModTiers {
    public static final ForgeTier IRIDIUM = new ForgeTier(5, 2589, 8.85F, 5.5F, 18, Revamp.NEEDS_IRIDIUM_TOOL_TAG, () -> {
        return Ingredient.of(ItemRegistry.IRIDIUM_PLATING.get());
    }),
    EXCALIBUR = new ForgeTier(3, 950, 9.5f, 3.5f, 50, Revamp.NEEDS_IRIDIUM_TOOL_TAG, () -> Ingredient.EMPTY);

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

    public TagKey<Block> getTag() { return Revamp.NEEDS_IRIDIUM_TOOL_TAG; }

}
