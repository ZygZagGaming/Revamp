package com.zygzag.revamp.common.item.recipe;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public interface RecipeWithMultiOutput<T extends Container> extends Recipe<T> {
    ItemStack assemble(T container);

    ItemStack[] getExtraOutputs(T container);

    int numberOfOutputs(T container);
}
