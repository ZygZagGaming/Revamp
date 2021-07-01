package com.zygzag.revamp.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PartialIridiumArmorItem extends ArmorItem {

    int max;
    int amount;

    public PartialIridiumArmorItem(IArmorMaterial p_i48534_1_, EquipmentSlotType p_i48534_2_, Properties p_i48534_3_, int max, int amount) {
        super(p_i48534_1_, p_i48534_2_, p_i48534_3_);
        this.max = max;
        this.amount = amount;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);
        list.add(new StringTextComponent("Iridium Plated: " + this.amount + " / " + this.max));
    }
}
