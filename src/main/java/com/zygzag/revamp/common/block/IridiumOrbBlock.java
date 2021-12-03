package com.zygzag.revamp.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;

public class IridiumOrbBlock extends Block {

    public IridiumOrbBlock() {
        super(Properties.of(Material.CLAY).noOcclusion().lightLevel((state) -> 15));
    }
}
