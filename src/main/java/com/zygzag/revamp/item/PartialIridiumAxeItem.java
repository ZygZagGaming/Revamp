package com.zygzag.revamp.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PartialIridiumAxeItem extends AxeItem {

    int max;
    int amount;

    public PartialIridiumAxeItem(IItemTier p_i48530_1_, float p_i48530_2_, float p_i48530_3_, Properties p_i48530_4_, int max, int amount) {
        super(p_i48530_1_, p_i48530_2_, p_i48530_3_, p_i48530_4_);
        this.max = max;
        this.amount = amount;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);
        list.add(new StringTextComponent("Iridium Plated: " + this.amount + " / " + this.max));
    }
}
