package com.zygzag.revamp.common.block;

import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.material.Material;

public class IridiumOreBlock extends DropExperienceBlock {
    public IridiumOreBlock() {
        super(Properties.of(Material.STONE).strength(3.0f, 3.0f).requiresCorrectToolForDrops());
    }

    public IridiumOreBlock(Properties prop) {
        super(prop);
    }
}
