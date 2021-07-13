package com.zygzag.revamp.common.block;

import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;

public class IridiumOreBlock extends OreBlock {
    public IridiumOreBlock() {
        super(Properties.of(Material.STONE).harvestLevel(4).strength(3.0f, 3.0f).requiresCorrectToolForDrops());
    }
}
