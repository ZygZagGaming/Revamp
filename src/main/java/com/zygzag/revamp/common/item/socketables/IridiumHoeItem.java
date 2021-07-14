package com.zygzag.revamp.common.item.socketables;

import com.zygzag.revamp.common.item.ISocketable;
import net.minecraft.item.HoeItem;
import net.minecraft.item.IItemTier;

public class IridiumHoeItem extends HoeItem implements ISocketable {
    public IridiumHoeItem(IItemTier tier, int damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }
}
