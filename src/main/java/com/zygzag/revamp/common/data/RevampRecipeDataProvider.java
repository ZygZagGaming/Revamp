package com.zygzag.revamp.common.data;

import com.zygzag.revamp.common.registry.RecipeSerializerRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RevampRecipeDataProvider extends RecipeProvider {
    public RevampRecipeDataProvider(DataGenerator datagen) {
        super(datagen);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        SpecialRecipeBuilder.special(RecipeSerializerRegistry.ENDER_BOOK_COPY_CRAFTING.get()).save(consumer, "revamp:custom/ender_book_copy");
        SpecialRecipeBuilder.special(RecipeSerializerRegistry.SHULKER_BOWL_CRAFTING.get()).save(consumer, "revamp:custom/shulker_bowl");
        SpecialRecipeBuilder.special(RecipeSerializerRegistry.SOCKET_REMOVE_CRAFTING.get()).save(consumer, "revamp:custom/socket_remove");
    }
}
