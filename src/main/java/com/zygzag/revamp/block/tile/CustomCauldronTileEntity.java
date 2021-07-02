package com.zygzag.revamp.block.tile;

import com.zygzag.revamp.Registry;
import net.minecraft.tileentity.TileEntity;

public class CustomCauldronTileEntity extends TileEntity {
    public CustomCauldronTileEntity() {
        super(Registry.CUSTOM_CAULDRON_TILE_ENTITY.get());
    }
}
