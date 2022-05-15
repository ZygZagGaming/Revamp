package com.zygzag.revamp.common.plugin;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.item.recipe.UpgradedBlastFurnaceRecipe;
import com.zygzag.revamp.common.registry.Registry;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class UpgradedBlastingCategory implements IRecipeCategory<UpgradedBlastFurnaceRecipe> {
    protected static final int inputSlot = 0;
    protected static final int fuelSlot = 1;
    protected static final int outputSlot = 2;
    protected static final int extraOutputSlot = 3;

    protected final IDrawableStatic staticFlame;
    protected final IDrawableAnimated animatedFlame;

    public static final String TEXTURE_GUI_PATH = "textures/gui/";
    public static final String TEXTURE_GUI_VANILLA = TEXTURE_GUI_PATH + "gui_vanilla.png";

    public static final ResourceLocation RECIPE_GUI_VANILLA = new ResourceLocation(ModIds.JEI_ID, TEXTURE_GUI_VANILLA);

    private final IDrawable background;
    private final int regularCookTime;
    private final IDrawable icon;
    private final Component localizedName;
    private final LoadingCache<Integer, IDrawableAnimated> cachedArrows;

    private final IGuiHelper guiHelper;

    public UpgradedBlastingCategory(IJeiHelpers helpers) {
        this.guiHelper = helpers.getGuiHelper();
        staticFlame = guiHelper.createDrawable(RECIPE_GUI_VANILLA, 82, 114, 14, 14);
        animatedFlame = guiHelper.createAnimatedDrawable(staticFlame, 300, IDrawableAnimated.StartDirection.TOP, true);
        this.background = guiHelper.createDrawable(RECIPE_GUI_VANILLA, 0, 114, 82, 54);
        this.regularCookTime = 100;
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(Registry.ItemRegistry.UPGRADED_BLAST_FURNACE_ITEM.get()));
        this.localizedName = new TranslatableComponent("revamp.recipe.upgraded_blasting");
        this.cachedArrows = CacheBuilder.newBuilder()
                .maximumSize(25)
                .build(new CacheLoader<>() {
                    @Override
                    public IDrawableAnimated load(Integer cookTime) {
                        return guiHelper.drawableBuilder(RECIPE_GUI_VANILLA, 82, 128, 24, 17)
                                .buildAnimated(cookTime, IDrawableAnimated.StartDirection.LEFT, false);
                    }
                });
    }

    protected IDrawableAnimated getArrow(UpgradedBlastFurnaceRecipe recipe) {
        int cookTime = recipe.getCookingTime();
        if (cookTime <= 0) {
            cookTime = regularCookTime;
        }
        return this.cachedArrows.getUnchecked(cookTime);
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
    public void setIngredients(UpgradedBlastFurnaceRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutputs(VanillaTypes.ITEM, List.of(recipe.getResultItem(), recipe.getOutSecondary()));
    }

    @Override
    public void draw(UpgradedBlastFurnaceRecipe recipe, PoseStack poseStack, double mouseX, double mouseY) {
        animatedFlame.draw(poseStack, 1, 20);

        IDrawableAnimated arrow = getArrow(recipe);
        arrow.draw(poseStack, 24, 18);

        drawExperience(recipe, poseStack);
        drawCookTime(recipe, poseStack);
    }

    protected void drawExperience(UpgradedBlastFurnaceRecipe recipe, PoseStack poseStack) {
        float experience = recipe.getExperience();
        if (experience > 0) {
            TranslatableComponent experienceString = new TranslatableComponent("gui.jei.category.smelting.experience", experience);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            int stringWidth = fontRenderer.width(experienceString);
            fontRenderer.draw(poseStack, experienceString, background.getWidth() - stringWidth, 0, 0xFF808080);
        }
    }

    protected void drawCookTime(UpgradedBlastFurnaceRecipe recipe, PoseStack poseStack) {
        int cookTime = recipe.getCookingTime();
        if (cookTime > 0) {
            int cookTimeSeconds = cookTime / 20;
            TranslatableComponent timeString = new TranslatableComponent("gui.jei.category.smelting.time.seconds", cookTimeSeconds);
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            int stringWidth = fontRenderer.width(timeString);
            fontRenderer.draw(poseStack, timeString, background.getWidth() - stringWidth, 45, 0xFF808080);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return Revamp.loc("upgraded_blasting");
    }

    @Override
    public Class<? extends UpgradedBlastFurnaceRecipe> getRecipeClass() {
        return UpgradedBlastFurnaceRecipe.class;
    }

    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, UpgradedBlastFurnaceRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(inputSlot, true, 0, 0);
        guiItemStacks.init(outputSlot, false, 60, 18);

        guiItemStacks.set(ingredients);
    }

    @Override
    public boolean isHandled(UpgradedBlastFurnaceRecipe recipe) {
        return !recipe.isSpecial();
    }
}
