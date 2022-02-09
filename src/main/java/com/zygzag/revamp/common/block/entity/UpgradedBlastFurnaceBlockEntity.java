package com.zygzag.revamp.common.block.entity;

import com.zygzag.revamp.common.block.menu.UpgradedBlastFurnaceMenu;
import com.zygzag.revamp.common.item.recipe.ModRecipeType;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

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


}
