package com.zygzag.revamp.common.item.socketables;

import com.zygzag.revamp.common.item.ISocketable;
import net.minecraft.item.IItemTier;
import net.minecraft.item.PickaxeItem;

public class IridiumPickaxeItem extends PickaxeItem implements ISocketable {
    public IridiumPickaxeItem(IItemTier tier, int damage, float speed, Properties properties) {
        super(tier, damage, speed, properties);
    }
}
