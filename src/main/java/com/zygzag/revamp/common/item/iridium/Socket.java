package com.zygzag.revamp.common.item.iridium;

import net.minecraft.FieldsAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@FieldsAreNonnullByDefault
public enum Socket {
    NONE(Items.AIR),
    DIAMOND(Items.DIAMOND),
    EMERALD(Items.EMERALD),
    SKULL(Items.SKELETON_SKULL),
    WITHER_SKULL(Items.WITHER_SKELETON_SKULL),
    AMETHYST(Items.AMETHYST_SHARD);

    Item i;
    Socket(Item i) {
        this.i = i;
    }
}
