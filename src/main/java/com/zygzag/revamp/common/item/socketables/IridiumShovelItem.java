package com.zygzag.revamp.common.item.socketables;

import com.zygzag.revamp.common.item.ISocketable;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ShovelItem;

public class IridiumShovelItem extends ShovelItem implements ISocketable {
    public IridiumShovelItem(IItemTier tier, float damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }
}
