package com.zygzag.revamp.common.registry;

import com.zygzag.revamp.common.item.recipe.EmpowermentRecipe;
import com.zygzag.revamp.common.item.recipe.TransmutationRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.zygzag.revamp.common.Revamp.MODID;

public class RecipeTypeRegistry extends Registry<RecipeType<?>> {
    public static final RecipeTypeRegistry INSTANCE = new RecipeTypeRegistry(DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID), MODID);

    public static final RegistryObject<RecipeType<TransmutationRecipe>> TRANSMUTATION = registerRecipeType("transmutation");
    public static final RegistryObject<RecipeType<EmpowermentRecipe>> EMPOWERMENT = registerRecipeType("empowerment");

    public RecipeTypeRegistry(DeferredRegister<RecipeType<?>> register, String modid) {
        super(register, modid);
    }

    public static <T extends Recipe<?>> RegistryObject<RecipeType<T>> registerRecipeType(String id) {
        return INSTANCE.register(id, () -> new RecipeType<>() {
            @Override
            public String toString() {
                return id;
            }
        });
    }
}
