package com.zygzag.revamp.common.item.socketables;

import com.zygzag.revamp.common.item.ISocketable;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;

public class IridiumChestplateItem extends ArmorItem implements ISocketable {
    public IridiumChestplateItem(IArmorMaterial material, Properties properties) {
        super(material, EquipmentSlotType.CHEST, properties);
    }
}
