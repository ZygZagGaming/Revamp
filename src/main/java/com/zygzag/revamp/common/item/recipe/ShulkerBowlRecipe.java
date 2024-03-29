package com.zygzag.revamp.common.item.recipe;

import com.mojang.datafixers.util.Pair;
import com.zygzag.revamp.common.item.ShulkerBowlItem;
import com.zygzag.revamp.common.registry.ItemRegistry;
import com.zygzag.revamp.common.registry.RecipeSerializerRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
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

    private static TagKey<Item> stewTag;

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        if (stewTag == null) stewTag = ItemTags.create(new ResourceLocation("revamp:stews"));
        ArrayList<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        if (stacks.size() == 2) {
            ItemStack first = stacks.get(0);
            ItemStack second = stacks.get(1);
            if (first.is(stewTag) && (second.is(Items.SHULKER_SHELL) || second.is(ItemRegistry.SHULKER_BOWL_ITEM.get()))) {
                return ShulkerBowlItem.getNumberOfStews(second.getOrCreateTag()) < 16;
            } else if (second.is(stewTag) && (first.is(Items.SHULKER_SHELL) || first.is(ItemRegistry.SHULKER_BOWL_ITEM.get()))) {
                return ShulkerBowlItem.getNumberOfStews(first.getOrCreateTag()) < 16;
            } else return false;
        }
        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        if (stewTag == null) stewTag = ItemTags.create(new ResourceLocation("revamp:stews"));
        ArrayList<ItemStack> stacks = new ArrayList<>();
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
            if (first.is(stewTag) && (second.is(Items.SHULKER_SHELL) || second.is(ItemRegistry.SHULKER_BOWL_ITEM.get()))) {
                stew = first;
                bowl = second;
            } else if (second.is(stewTag) && (first.is(Items.SHULKER_SHELL) || first.is(ItemRegistry.SHULKER_BOWL_ITEM.get()))) {
                stew = second;
                bowl = first;
            } else return ItemStack.EMPTY;

            if (bowl.is(Items.SHULKER_SHELL)) {
                ItemStack stack = new ItemStack(ItemRegistry.SHULKER_BOWL_ITEM.get());
                CompoundTag tag = stack.getOrCreateTag();
                FoodProperties prop = stew.getItem().getFoodProperties();
                if (prop != null) {
                    tag.putInt("Hunger", prop.getNutrition());
                    tag.putFloat("Saturation", prop.getSaturationModifier());
                    tag.putInt("Stews", 1);
                    List<Pair<MobEffectInstance, Float>> effects = prop.getEffects();
                    if (stew.getItem() instanceof SuspiciousStewItem) {
                        ListTag list = stew.getOrCreateTag().getList("Effects", 10);
                        for (net.minecraft.nbt.Tag t : list) {
                            if (t instanceof CompoundTag effect) {
                                System.out.println(effect);
                                MobEffect effect1 = MobEffect.byId(effect.getInt("EffectId"));
                                if (effect1 != null) {
                                    MobEffectInstance inst = new MobEffectInstance(effect1, effect.getInt("EffectDuration"));
                                    effects.add(Pair.of(inst, 1f));
                                }
                            }
                        }
                    }
                    System.out.println(effects);
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
                if (ShulkerBowlItem.getNumberOfStews(bowl.getOrCreateTag()) < ShulkerBowlItem.getMaximumNumberOfStews()) {
                    ItemStack newBowl = bowl.copy();
                    CompoundTag tag = newBowl.getOrCreateTag();
                    FoodProperties prop = stew.getItem().getFoodProperties();
                    if (prop != null) {
                        tag.putInt("Hunger", tag.getInt("Hunger") + prop.getNutrition());
                        tag.putFloat("Saturation", tag.getFloat("Saturation") + prop.getSaturationModifier());
                        tag.putInt("Stews", tag.getInt("Stews") + 1);
                        List<Pair<MobEffectInstance, Float>> effects = prop.getEffects();
                        if (stew.getItem() instanceof SuspiciousStewItem) {
                            ListTag list = stew.getOrCreateTag().getList("Effects", 10);
                            for (net.minecraft.nbt.Tag t : list) {
                                if (t instanceof CompoundTag effect) {
                                    System.out.println(effect);
                                    MobEffect effect1 = MobEffect.byId(effect.getInt("EffectId"));
                                    if (effect1 != null) {
                                        MobEffectInstance inst = new MobEffectInstance(effect1, effect.getInt("EffectDuration"));
                                        effects.add(Pair.of(inst, 1f));
                                    }
                                }
                            }
                        }
                        System.out.println(effects);
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
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.SHULKER_BOWL_CRAFTING.get();
    }
}
