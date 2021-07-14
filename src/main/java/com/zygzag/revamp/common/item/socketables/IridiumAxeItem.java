package com.zygzag.revamp.common.item.socketables;

import com.zygzag.revamp.common.item.ISocketable;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;

public class IridiumAxeItem extends AxeItem implements ISocketable {
    public IridiumAxeItem(IItemTier tier, float damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }


}
