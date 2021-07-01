package com.zygzag.revamp.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PartialIridiumShovelItem extends ShovelItem {

    int max;
    int amount;

    public PartialIridiumShovelItem(IItemTier p_i48469_1_, float p_i48469_2_, float p_i48469_3_, Properties p_i48469_4_, int max, int amount) {
        super(p_i48469_1_, p_i48469_2_, p_i48469_3_, p_i48469_4_);
        this.max = max;
        this.amount = amount;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);
        list.add(new StringTextComponent("Iridium Plated: " + this.amount + " / " + this.max));
    }
}
