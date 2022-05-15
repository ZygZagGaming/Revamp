package com.zygzag.revamp.common.item.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class UpgradedBlastFurnaceRecipe extends AbstractCookingRecipe implements RecipeWithMultiOutput<Container> {
    private final ItemStack outSecondary;
    private final double chance;
    public UpgradedBlastFurnaceRecipe(ResourceLocation id, String group, Ingredient in, ItemStack out, ItemStack outSecondary, float xp, int cookingTime, double chance) {
        super(ModRecipeType.UPGRADED_BLASTING, id, group, in, out, xp, cookingTime);
        this.outSecondary = outSecondary;
        this.chance = chance;
    }

    public ItemStack getOutSecondary() {
        return outSecondary;
    }

    public double getChance() {
        return chance;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registry.RecipeSerializerRegistry.UPGRADED_BLAST_FURNACE_SERIALIZER.get();
    }

    @Override
    public boolean matches(Container container, Level world) {
        ItemStack existing = container.getItem(3);
        return (existing.isEmpty() || (existing.is(getOutSecondary().getItem()) && container.getMaxStackSize() - existing.getCount() >= getOutSecondary().getCount())) && super.matches(container, world);
    }

    @Override
    public ItemStack assemble(Container container) {
        return super.assemble(container);
    }

    @Override
    public ItemStack[] getExtraOutputs(Container container) {
        if (Math.random() < chance) return new ItemStack[]{ outSecondary.copy() };
        return new ItemStack[]{ ItemStack.EMPTY };
    }

    @Override
    public int numberOfOutputs(Container container) {
        return 0;
    }


    public static class UpgradedBlastingSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<UpgradedBlastFurnaceRecipe> {

        @Override
        public UpgradedBlastFurnaceRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            JsonElement jsonelement = GsonHelper.isArrayNode(json, "ingredient") ? GsonHelper.getAsJsonArray(json, "ingredient") : GsonHelper.getAsJsonObject(json, "ingredient");
            Ingredient ingredient = Ingredient.fromJson(jsonelement);

            ItemStack result = getStack(json, "result");
            ItemStack secondaryResult = getStack(json, "secondary_result");

            double chanceForSecondary = GsonHelper.getAsDouble(json, "chance_for_secondary", 0.0);

            float xp = GsonHelper.getAsFloat(json, "experience", 0.0F);
            int time = GsonHelper.getAsInt(json, "cookingtime", 100);

            return new UpgradedBlastFurnaceRecipe(id, group, ingredient, result, secondaryResult, xp, time, chanceForSecondary);
        }

        @Nullable
        @Override
        public UpgradedBlastFurnaceRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            ItemStack secondaryResult = buffer.readItem();
            double chanceForSecondary = buffer.readDouble();
            float xp = buffer.readFloat();
            int time = buffer.readInt();

            return new UpgradedBlastFurnaceRecipe(id, group, ingredient, result, secondaryResult, xp, time, chanceForSecondary);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, UpgradedBlastFurnaceRecipe recipe) {
            buffer.writeUtf(recipe.group);
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.result);
            buffer.writeItem(recipe.getOutSecondary());
            buffer.writeDouble(recipe.chance);
            buffer.writeFloat(recipe.experience);
            buffer.writeInt(recipe.cookingTime);
        }

        private static ItemStack getStack(JsonObject json, String path) {
            if (!json.has(path)) throw new JsonSyntaxException("Missing result, expected to find a string or object");
            ItemStack stack;
            if (json.get(path).isJsonObject()) stack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, path));
            else {
                String s1 = GsonHelper.getAsString(json, path);
                ResourceLocation resourcelocation = new ResourceLocation(s1);
                stack = new ItemStack(ForgeRegistries.ITEMS.getValue(resourcelocation));
            }
            return stack;
        }
    }
}
