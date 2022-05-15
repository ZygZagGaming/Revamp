package com.zygzag.revamp.common.plugin;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.item.recipe.ModRecipeType;
import com.zygzag.revamp.common.item.recipe.TransmutationRecipe;
import com.zygzag.revamp.common.item.recipe.UpgradedBlastFurnaceRecipe;
import com.zygzag.revamp.common.registry.Registry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("unused")
@JeiPlugin
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class JEIPlugin implements IModPlugin {
    @Nullable public IRecipeCategory<TransmutationRecipe> transmutationCategory;
    @Nullable public IRecipeCategory<UpgradedBlastFurnaceRecipe> upgradedBlastingCategory;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Revamp.MODID + ".plugin.jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(transmutationCategory = new TransmutationCategory(registration.getJeiHelpers()));
        registration.addRecipeCategories(upgradedBlastingCategory = new UpgradedBlastingCategory(registration.getJeiHelpers()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (Minecraft.getInstance().level != null) {
            registration.addRecipes(Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(ModRecipeType.TRANSMUTATION), Revamp.loc("transmutation"));
            registration.addRecipes(Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(ModRecipeType.UPGRADED_BLASTING), Revamp.loc("upgraded_blasting"));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(Registry.ItemRegistry.TRANSMUTATION_CHARGE.get().getDefaultInstance(), Revamp.loc("transmutation"));
        registration.addRecipeCatalyst(Registry.IridiumGearRegistry.SKULL_SOCKETED_IRIDIUM_PICKAXE.get().getDefaultInstance(), Revamp.loc("transmutation"));
        registration.addRecipeCatalyst(Registry.ItemRegistry.UPGRADED_BLAST_FURNACE_ITEM.get().getDefaultInstance(), Revamp.loc("upgraded_blasting"));
    }
}
