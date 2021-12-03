package com.zygzag.revamp.common.item;

import com.mojang.datafixers.util.Pair;
import com.zygzag.revamp.common.Revamp;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ShulkerBowlItem extends BowlFoodItem {
    public ShulkerBowlItem() {
        super(new Properties()
                .tab(Revamp.MAIN_TAB)
                .food(new FoodProperties.Builder().build())
        );
    }

    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (entity instanceof Player player) {
            CompoundTag tag = stack.getOrCreateTag();
            ListTag effectsTag = tag.getList("Effects", 10);
            for (int i = 0; i < effectsTag.size(); i++) {
                CompoundTag compound = effectsTag.getCompound(i);
                float chance = compound.getFloat("Chance");
                MobEffectInstance inst = MobEffectInstance.load(compound);
                if (inst != null) {
                    int r = getNumberOfStews(tag);
                    MobEffectInstance effectToGiveToPlayer = new MobEffectInstance(inst.getEffect(), inst.getDuration() / r, inst.getAmplifier());
                    int n = compound.getInt("Duration");
                    compound.putInt("Duration",  n - (n / r));
                    if (Math.random() < chance) player.addEffect(effectToGiveToPlayer);
                }
            }
            Pair<Integer, Float> pair = getAdjustedHungerAndSaturation(tag);
            player.getFoodData().eat(pair.getFirst(), pair.getSecond());
        }
        return getNumberOfStews(stack.getOrCreateTag()) != 0 ? stack : new ItemStack(Items.SHULKER_SHELL);
    }

    public static Pair<Integer, Float> getAdjustedHungerAndSaturation(CompoundTag tag) {
        int h = getHunger(tag);
        float s = getSaturation(tag);
        int n = getNumberOfStews(tag);
        tag.putInt("Hunger", h - (h / n));
        tag.putFloat("Saturation", s - (s / n));
        tag.putInt("Stews", n - 1);
        return Pair.of(h, s);
    }

    public static int getHunger(CompoundTag tag) {
        return tag.getInt("Hunger");
    }

    public static float getSaturation(CompoundTag tag) {
        return tag.getFloat("Saturation") / getNumberOfStews(tag);
    }

    public static int getNumberOfStews(CompoundTag tag) {
        return tag.getInt("Stews");
    }
}
