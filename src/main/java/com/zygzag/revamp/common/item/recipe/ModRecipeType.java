package com.zygzag.revamp.common.item.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public interface ModRecipeType<T extends Recipe<?>> extends RecipeType<T> {
    RecipeType<TransmutationRecipe> TRANSMUTATION = RecipeType.register("transmutation");
    RecipeType<EmpowermentRecipe> EMPOWERMENT = RecipeType.register("empowerment");
    RecipeType<UpgradedBlastFurnaceRecipe> UPGRADED_BLASTING = RecipeType.register("upgraded_blasting");
}
