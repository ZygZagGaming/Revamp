package com.zygzag.revamp.common.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public interface ISocketable {
    default boolean canApplySocket(ItemStack item, ItemStack socketItem) {
        if (item.getItem() instanceof ISocketable && socketItem.getItem() instanceof SocketItem) {
            return ItemStack.of(item.getOrCreateTag().getCompound("SocketItem")) == ItemStack.EMPTY;
        }
        return false;
    }

    default void applySocket(ItemStack item, ItemStack socketItem) {
        if (canApplySocket(item, socketItem)) {
            CompoundNBT nbt = socketItem.save(new CompoundNBT());
            item.getOrCreateTag().put("SocketItem", nbt);
            item.setTag(item.getTag());
        }
    }
}
