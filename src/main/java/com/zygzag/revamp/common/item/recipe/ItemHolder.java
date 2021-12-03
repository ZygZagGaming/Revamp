package com.zygzag.revamp.common.item.recipe;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Used to hold one item.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemHolder implements Container {
    public ItemStack stack;
    public ItemHolder(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stack == null;
    }

    @Override
    public ItemStack getItem(int index) {
        return stack;
    }

    @Override
    public ItemStack removeItem(int x, int y) {
        ItemStack cache = stack;
        stack = null;
        return cache;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_18951_) {
        return stack;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        stack = null;
    }
}
