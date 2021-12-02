package com.zygzag.revamp.item.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface ModRecipeType<T extends Recipe<?>> extends RecipeType<T> {
    RecipeType<TransmutationRecipe> TRANSMUTATION = RecipeType.register("smithing");
    RecipeType<EmpowermentRecipe> EMPOWERMENT = RecipeType.register("empowerment");
}
