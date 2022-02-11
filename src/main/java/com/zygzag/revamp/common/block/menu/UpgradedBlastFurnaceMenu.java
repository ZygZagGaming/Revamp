package com.zygzag.revamp.common.block.menu;

import com.zygzag.revamp.common.item.recipe.ModRecipeType;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class UpgradedBlastFurnaceMenu extends AbstractFurnaceMenu {
    public static final int SECONDARY_RESULT_SLOT = 3;
    public UpgradedBlastFurnaceMenu(int id, Inventory inventory) {
        super(Registry.UPGRADED_BLAST_FURNACE_MENU.get(), ModRecipeType.UPGRADED_BLASTING, RecipeBookType.BLAST_FURNACE, id, inventory);
        this.addSlot(new FurnaceResultSlot(inventory.player, new SimpleContainer(4), 3, 156, 75));
    }

    public UpgradedBlastFurnaceMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(Registry.UPGRADED_BLAST_FURNACE_MENU.get(), ModRecipeType.UPGRADED_BLASTING, RecipeBookType.BLAST_FURNACE, id, inventory, container, data);
        this.addSlot(new FurnaceResultSlot(inventory.player, container, 3, 156, 75));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 2 || index == 3) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index != 1 && index != 0) {
                if (this.canSmelt(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 3 && index < 30) {
                    if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 30 && index < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }
}
