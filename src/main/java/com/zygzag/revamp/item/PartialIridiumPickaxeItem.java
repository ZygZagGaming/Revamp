package com.zygzag.revamp.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PartialIridiumPickaxeItem extends PickaxeItem {

    int max;
    int amount;

    public PartialIridiumPickaxeItem(IItemTier p_i48478_1_, int p_i48478_2_, float p_i48478_3_, Properties p_i48478_4_, int max, int amount) {
        super(p_i48478_1_, p_i48478_2_, p_i48478_3_, p_i48478_4_);
        this.max = max;
        this.amount = amount;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);
        list.add(new StringTextComponent("Iridium Plated: " + this.amount + " / " + this.max));
    }
}
