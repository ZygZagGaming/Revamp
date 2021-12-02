package com.zygzag.revamp.block;

import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.material.Material;

public class IridiumOreBlock extends OreBlock {
    public IridiumOreBlock() {
        super(Properties.of(Material.STONE).strength(3.0f, 3.0f).requiresCorrectToolForDrops());
    }
}
