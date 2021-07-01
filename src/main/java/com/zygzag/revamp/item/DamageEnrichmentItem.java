package com.zygzag.revamp.item;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

import java.util.Map;

public class DamageEnrichmentItem extends Item implements IEnrichment {

    public DamageEnrichmentItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canBeAppliedTo(ItemStack item) {
        return item.getItem() instanceof SwordItem || item.getItem() instanceof AxeItem;
    }

    @Override
    public ItemStack apply(ItemStack item, float amount, AttributeModifier.Operation operation) {
        ItemStack stack = new ItemStack(item.getItem(), item.getCount());
        boolean flag = false;
        for (Map.Entry<Attribute, AttributeModifier> entry : stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).entries()) {
            System.out.println(entry.getValue());
            if (entry.getKey().equals(Attributes.ATTACK_DAMAGE) && entry.getValue().getOperation().equals(operation)) {
                stack.addAttributeModifier(entry.getKey(), new AttributeModifier(entry.getValue().getName(), (entry.getValue().getAmount() + (amount * 2)), entry.getValue().getOperation()), EquipmentSlotType.MAINHAND);
                flag = true;
            }
            else stack.addAttributeModifier(entry.getKey(), entry.getValue(), EquipmentSlotType.MAINHAND);
        }
        if (!flag) stack.addAttributeModifier(Attributes.ATTACK_DAMAGE, new AttributeModifier("attack", amount, operation), EquipmentSlotType.MAINHAND);
        return stack;
    }
}
