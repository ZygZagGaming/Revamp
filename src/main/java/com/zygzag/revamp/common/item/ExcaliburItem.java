package com.zygzag.revamp.common.item;

import com.zygzag.revamp.common.item.tier.ModTiers;
import net.minecraft.world.item.SwordItem;

public class ExcaliburItem extends SwordItem {
    public ExcaliburItem(Properties prop) {
        super(ModTiers.EXCALIBUR, 3, -2.2f, prop);
    }
}
