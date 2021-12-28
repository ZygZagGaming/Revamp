package com.zygzag.revamp.common.item.recipe;

import com.google.gson.JsonObject;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TransmutationRecipe implements Recipe<ItemHolder> {
    Ingredient inItem;
    Item outItem;
    double rate;
    ResourceLocation id;
    public TransmutationRecipe(Ingredient inItem, Item outItem, double rate, ResourceLocation id) {
        this.inItem = inItem;
        this.outItem = outItem;
        this.rate = rate;
        this.id = id;
    }

    @Override
    public boolean matches(ItemHolder holder, Level world) {
        return inItem.test(holder.stack);
    }

    @Override
    public ItemStack assemble(ItemHolder holder) {
        int count = holder.stack.getCount();
        double random = (count * rate) % 1;
        int newCount = (int) (count * rate) + (Math.random() < random ? 1 : 0);
        return new ItemStack(outItem, newCount);
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 1;
    }

    @Override
    public ItemStack getResultItem() {
        return outItem.getDefaultInstance();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registry.TRANSMUTATION_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeType.TRANSMUTATION;
    }

    public Ingredient getInItem() {
        return inItem;
    }

    public Item getOutItem() {
        return outItem;
    }

    public double getRate() {
        return rate;
    }

    public static class TransmutationSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<TransmutationRecipe> {

        @Override
        public TransmutationRecipe fromJson(ResourceLocation id, JsonObject obj) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(obj.get("output").getAsString()));
            if (item == null) item = Items.AIR;
            return new TransmutationRecipe(
                    Ingredient.fromJson(obj.get("input")),
                    item,
                    obj.get("rate").getAsDouble(),
                    id
            );
        }

        @Nullable
        @Override
        public TransmutationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(buf.readUtf()));
            if (item == null) item = Items.AIR;
            return new TransmutationRecipe(
                    Ingredient.fromNetwork(buf),
                    item,
                    buf.readDouble(),
                    id
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, TransmutationRecipe recipe) {
            ResourceLocation id = recipe.outItem.getRegistryName();
            if (id == null) id = new ResourceLocation("minecraft:air");
            buf.writeUtf(id.toString());
            recipe.inItem.toNetwork(buf);
            buf.writeDouble(recipe.rate);
        }
    }

}