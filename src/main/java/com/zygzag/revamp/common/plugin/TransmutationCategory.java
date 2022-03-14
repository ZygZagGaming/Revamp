package com.zygzag.revamp.common.plugin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.item.recipe.TransmutationRecipe;
import com.zygzag.revamp.common.registry.Registry;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TransmutationCategory implements IRecipeCategory<TransmutationRecipe> {
    private final IJeiHelpers helpers;
    private final IGuiHelper guiHelper;
    private final IDrawable background;
    private final IDrawable icon;

    public static final String TEXTURE_GUI_PATH = "textures/gui/";
    public static final String TEXTURE_GUI_VANILLA = TEXTURE_GUI_PATH + "gui_vanilla.png";

    public static final ResourceLocation RECIPE_GUI_VANILLA = new ResourceLocation(ModIds.JEI_ID, TEXTURE_GUI_VANILLA);

    public TransmutationCategory(IJeiHelpers helpers) {
        this.helpers = helpers;
        guiHelper = helpers.getGuiHelper();
        background = guiHelper.drawableBuilder(RECIPE_GUI_VANILLA, 0, 220, 82, 34)
                .addPadding(0, 10, 0, 0)
                .build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, Registry.TRANSMUTATION_CHARGE.get().getDefaultInstance());
    }

    @Override
    public Class<? extends TransmutationRecipe> getRecipeClass() {
        return TransmutationRecipe.class;
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("revamp.recipe.transmutation");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public ResourceLocation getUid() {
        return Revamp.loc("transmutation");
    }

    @Override
    public void setIngredients(TransmutationRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(Arrays.stream(new Ingredient[]{recipe.getInItem()}).collect(Collectors.toList()));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutItem().getDefaultInstance());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, TransmutationRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
        guiItemStacks.init(0, true, 0, 8);
        guiItemStacks.init(1, false, 60, 8);

        guiItemStacks.set(ingredients);
    }

    @Override
    public void draw(TransmutationRecipe recipe, PoseStack stack, double mouseX, double mouseY) {
        drawRateCost(Minecraft.getInstance(), stack, "Rate: " + recipe.getRate() + "x");
    }

    private void drawRateCost(Minecraft minecraft, PoseStack poseStack, String text) {
        int shadowColor = 0xFF000000 | (0xFF111111 & 0xFCFCFC) >> 2;
        int width = minecraft.font.width(text);
        int x = background.getWidth() - 2 - width;
        int y = 37;

        minecraft.font.draw(poseStack, text, x, y, 0xFF111111);
    }
}
