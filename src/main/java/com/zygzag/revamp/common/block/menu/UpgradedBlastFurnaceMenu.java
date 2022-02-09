package com.zygzag.revamp.common.block.menu;

import com.zygzag.revamp.common.item.recipe.ModRecipeType;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

public class UpgradedBlastFurnaceMenu extends AbstractFurnaceMenu {
    public static final int SECONDARY_RESULT_SLOT = 3;
    public UpgradedBlastFurnaceMenu(int id, Inventory inventory) {
        super(Registry.UPGRADED_BLAST_FURNACE_MENU.get(), ModRecipeType.UPGRADED_BLASTING, RecipeBookType.BLAST_FURNACE, id, inventory);
        this.addSlot(new FurnaceResultSlot(inventory.player, new SimpleContainer(4), 3, 116, 75));
    }

    public UpgradedBlastFurnaceMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(Registry.UPGRADED_BLAST_FURNACE_MENU.get(), ModRecipeType.UPGRADED_BLASTING, RecipeBookType.BLAST_FURNACE, id, inventory, container, data);
        this.addSlot(new FurnaceResultSlot(inventory.player, container, 3, 116, 75));
    }
}
