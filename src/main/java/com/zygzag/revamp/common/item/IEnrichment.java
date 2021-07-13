package com.zygzag.revamp.common.item;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;

public interface IEnrichment {
    boolean canBeAppliedTo(ItemStack item);
    ItemStack apply(ItemStack item, float amount, AttributeModifier.Operation operation);
}
