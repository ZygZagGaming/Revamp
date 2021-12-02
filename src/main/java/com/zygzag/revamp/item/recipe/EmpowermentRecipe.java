package com.zygzag.revamp.item.recipe;

import com.google.gson.JsonObject;
import com.zygzag.revamp.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EmpowermentRecipe implements Recipe<ItemAndEntityHolder> {
    EntityType<?> inEntity;
    EntityType<?> outEntity;
    Ingredient item;
    ResourceLocation id;

    public EmpowermentRecipe(EntityType<?> inEntity, EntityType<?> outEntity, Ingredient item, ResourceLocation id) {
        this.inEntity = inEntity;
        this.outEntity = outEntity;
        this.item = item;
        this.id = id;
    }

    @Override
    public boolean matches(ItemAndEntityHolder holder, Level world) {
        return holder.entity.getType() == inEntity && item.test(holder.stack);
    }

    @Override
    public ItemStack assemble(ItemAndEntityHolder holder) {
        holder.entity = outEntity.create(holder.entity.level);
        holder.stack.shrink(1);
        return holder.stack;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 1;
    }

    @Override
    public ItemStack getResultItem() {
        return Registry.EMPOWERMENT_STAR_ITEM.get().getDefaultInstance();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return new EmpowermentSerializer();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeType.EMPOWERMENT;
    }

    public static class EmpowermentSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<EmpowermentRecipe> {

        @Override
        public EmpowermentRecipe fromJson(ResourceLocation id, JsonObject obj) {
            EntityType<?> in = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(obj.get("base").getAsString()));
            EntityType<?> out = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(obj.get("out").getAsString()));
            Ingredient item = Ingredient.fromJson(obj.get("item"));
            assert out != null && in != null;
            return new EmpowermentRecipe(in, out, item, id);
        }

        @Nullable
        @Override
        public EmpowermentRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            EntityType<?> in = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(buf.readUtf()));
            EntityType<?> out = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(buf.readUtf()));
            Ingredient item = Ingredient.fromNetwork(buf);
            assert out != null && in != null;
            return new EmpowermentRecipe(in, out, item, id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, EmpowermentRecipe recipe) {
            buf.writeUtf(Objects.requireNonNull(recipe.inEntity.getRegistryName()).toString());
            buf.writeUtf(Objects.requireNonNull(recipe.outEntity.getRegistryName()).toString());
            recipe.item.toNetwork(buf);
        }
    }
}
