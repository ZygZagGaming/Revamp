package com.zygzag.revamp.common.item.socketables;

import com.zygzag.revamp.common.item.ISocketable;
import net.minecraft.item.IItemTier;
import net.minecraft.item.SwordItem;

public class IridiumSwordItem extends SwordItem implements ISocketable {
    public IridiumSwordItem(IItemTier tier, int damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }
}
