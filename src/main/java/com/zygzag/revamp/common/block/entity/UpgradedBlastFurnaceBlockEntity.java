package com.zygzag.revamp.common.block.entity;

import com.zygzag.revamp.common.block.menu.UpgradedBlastFurnaceMenu;
import com.zygzag.revamp.common.item.recipe.ModRecipeType;
import com.zygzag.revamp.common.item.recipe.UpgradedBlastFurnaceRecipe;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class UpgradedBlastFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    public UpgradedBlastFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.UPGRADED_BLAST_FURNACE_BLOCK_ENTITY.get(), pos, state, ModRecipeType.UPGRADED_BLASTING);
        this.items = NonNullList.withSize(4, ItemStack.EMPTY);
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.upgraded_blast_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new UpgradedBlastFurnaceMenu(id, inventory, this, this.dataAccess);
    }

    @Override
    protected int getBurnDuration(ItemStack stack) {
        return super.getBurnDuration(stack) / 2;
    }

    @Override
    public boolean burn(@Nullable Recipe<?> recipe, NonNullList<ItemStack> items, int time) {
        /*  items[0] = input
            items[1] = fuel
            items[2] = output   */
        if (canBurn(recipe, items, time) && recipe instanceof UpgradedBlastFurnaceRecipe uRecipe && uRecipe.getChance() < Math.random()) {
            ItemStack extraResult = uRecipe.getOutSecondary().copy();
            ItemStack current = getItem(3);
            if (current.isEmpty()) setItem(3, extraResult);
            else if (current.getItem() == extraResult.getItem() && current.getCount() + extraResult.getCount() <= getMaxStackSize()) current.grow(extraResult.getCount());
        }
        return super.burn(recipe, items, time);
    }
}
