package com.zygzag.revamp.common.item;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ShulkerBowlItem extends BowlFoodItem {
    public ShulkerBowlItem() {
        super(new Properties()
                .food(new FoodProperties.Builder().build())
        );
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        float n = getNumberOfStews(stack.getOrCreateTag());
        return Math.round((n / getMaximumNumberOfStews()) * MAX_BAR_WIDTH);
    }

    @Override
    public int getBarColor(ItemStack p_150901_) {
        return 0xde8c83;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (entity instanceof Player player) {
            CompoundTag tag = stack.getOrCreateTag();
            ListTag effectsTag = tag.getList("Effects", 10);
            for (int i = 0; i < effectsTag.size(); i++) {
                CompoundTag compound = effectsTag.getCompound(i);
                float chance = compound.getFloat("Chance");
                MobEffectInstance inst = MobEffectInstance.load(compound);
                if (inst != null) {
                    System.out.println(inst);
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
        return tag.getFloat("Saturation");
    }

    public static int getNumberOfStews(CompoundTag tag) {
        return tag.getInt("Stews");
    }

    public static int getMaximumNumberOfStews() {
        return 16;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        int n = getNumberOfStews(stack.getOrCreateTag());
        MutableComponent comp = new TranslatableComponent("revamp.contains").withStyle(ChatFormatting.GRAY);
        comp.append(new TextComponent(": ").withStyle(ChatFormatting.GRAY));
        comp.append(new TextComponent(n + " ").withStyle(ChatFormatting.GOLD));
        comp.append(new TranslatableComponent(n == 1 ? "revamp.stew" : "revamp.stews").withStyle(ChatFormatting.GRAY));
        text.add(comp);
    }
}
