package com.zygzag.revamp.common.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;

public interface ISocketable {
    default boolean canApplySocket(ItemStack item, ItemStack socketItem) {
        if (item.getItem() instanceof ISocketable && socketItem.getItem() instanceof SocketItem) {
            System.out.println(ItemStack.of(item.getOrCreateTag().getCompound("SocketItem")).getItem());
            System.out.println(Items.AIR);
            return ItemStack.of(item.getOrCreateTag().getCompound("SocketItem")).getItem().equals(Items.AIR);
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
