package com.zygzag.revamp.common.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EmpowermentStar extends Item {
    public EmpowermentStar(Properties prop) {
        super(prop);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
