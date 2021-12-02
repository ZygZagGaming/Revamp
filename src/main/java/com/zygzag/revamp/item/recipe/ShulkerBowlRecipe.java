package com.zygzag.revamp.item.recipe;

import com.mojang.datafixers.util.Pair;
import com.zygzag.revamp.item.ShulkerBowlItem;
import com.zygzag.revamp.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ShulkerBowlRecipe extends CustomRecipe {
    public ShulkerBowlRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        Tag<Item> stewTag = ItemTags.getAllTags().getTag(new ResourceLocation("revamp:stews"));
        ArrayList<ItemStack> stacks = new ArrayList<>();
        if (stewTag != null) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (!stack.isEmpty()) {
                    stacks.add(stack);
                }
            }
            if (stacks.size() == 2) {
                ItemStack first = stacks.get(0);
                ItemStack second = stacks.get(1);
                if (first.is(stewTag) && (second.is(Items.SHULKER_SHELL) || second.is(Registry.SHULKER_BOWL_ITEM.get()))) {
                    return ShulkerBowlItem.getNumberOfStews(second.getOrCreateTag()) < 16;
                } else if (second.is(stewTag) && (first.is(Items.SHULKER_SHELL) || first.is(Registry.SHULKER_BOWL_ITEM.get()))) {
                    return ShulkerBowlItem.getNumberOfStews(first.getOrCreateTag()) < 16;
                } else return false;
            }
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        Tag<Item> stewTag = ItemTags.getAllTags().getTag(new ResourceLocation("revamp:stews"));
        ArrayList<ItemStack> stacks = new ArrayList<>();
        if (stewTag != null) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.getItem(i);
                if (!stack.isEmpty()) {
                    stacks.add(stack);
                }
            }
            if (stacks.size() == 2) {
                ItemStack first = stacks.get(0);
                ItemStack second = stacks.get(1);
                ItemStack bowl;
                ItemStack stew;
                if (first.is(stewTag) && (second.is(Items.SHULKER_SHELL) || second.is(Registry.SHULKER_BOWL_ITEM.get()))) {
                    stew = first;
                    bowl = second;
                } else if (second.is(stewTag) && (first.is(Items.SHULKER_SHELL) || first.is(Registry.SHULKER_BOWL_ITEM.get()))) {
                    stew = second;
                    bowl = first;
                } else return ItemStack.EMPTY;

                if (bowl.is(Items.SHULKER_SHELL)) {
                    ItemStack stack = new ItemStack(Registry.SHULKER_BOWL_ITEM.get());
                    CompoundTag tag = stack.getOrCreateTag();
                    FoodProperties prop = stew.getItem().getFoodProperties();
                    if (prop != null) {
                        tag.putInt("Hunger", prop.getNutrition());
                        tag.putFloat("Saturation", prop.getSaturationModifier());
                        tag.putInt("Stews", 1);
                        List<Pair<MobEffectInstance, Float>> effects = prop.getEffects();
                        if (effects.size() != 0) {
                            ListTag effectsTag = new ListTag();
                            for (Pair<MobEffectInstance, Float> pair : effects) {
                                CompoundTag newTag = new CompoundTag();
                                newTag.putFloat("Chance", pair.getSecond());
                                pair.getFirst().save(newTag);
                                effectsTag.add(newTag);
                            }
                            tag.put("Effects", effectsTag);
                        }
                    }
                    return stack;
                } else {
                    if (ShulkerBowlItem.getNumberOfStews(bowl.getOrCreateTag()) < 16) {
                        ItemStack newBowl = bowl.copy();
                        CompoundTag tag = newBowl.getOrCreateTag();
                        FoodProperties prop = stew.getItem().getFoodProperties();
                        if (prop != null) {
                            tag.putInt("Hunger", tag.getInt("Hunger") + prop.getNutrition());
                            tag.putFloat("Saturation", tag.getFloat("Saturation") + prop.getSaturationModifier());
                            tag.putInt("Stews", tag.getInt("Stews") + 1);
                            List<Pair<MobEffectInstance, Float>> effects = prop.getEffects();
                            ListTag ogEffects = tag.getList("Effects", CompoundTag.TAG_COMPOUND);
                            if (effects.size() != 0) {
                                ListTag effectsTag = new ListTag();
                                for (Pair<MobEffectInstance, Float> pair : effects) {
                                    boolean flag = false;
                                    MobEffectInstance e = pair.getFirst();
                                    for (int i = 0; i < ogEffects.size(); i++) {
                                        CompoundTag t = ogEffects.getCompound(i);
                                        if (t.getByte("Id") == MobEffect.getId(e.getEffect())) {
                                            MobEffectInstance inst = MobEffectInstance.load(t);
                                            if (inst != null) {
                                                flag = true;
                                                float chance = t.getFloat("Chance");
                                                t.putFloat("Chance", chance + pair.getSecond());
                                                MobEffectInstance newInst = new MobEffectInstance(inst.getEffect(),
                                                        inst.getDuration() + e.getDuration(),
                                                        Math.max(inst.getAmplifier(), e.getAmplifier()));
                                                newInst.save(t);
                                                effectsTag.add(t);
                                                break;
                                            }
                                        }
                                    }
                                    if (!flag) {
                                        CompoundTag newTag = new CompoundTag();
                                        newTag.putFloat("Chance", pair.getSecond());
                                        System.out.println(e);
                                        e.save(newTag);
                                        effectsTag.add(newTag);
                                    }
                                }
                                tag.put("Effects", effectsTag);
                            }
                        }
                        return newBowl;
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registry.SHULKER_BOWL_CRAFTING.get();
    }
}
