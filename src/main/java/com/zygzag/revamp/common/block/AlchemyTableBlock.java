package com.zygzag.revamp.common.block;

import com.zygzag.revamp.common.block.tile.AlchemyTableTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class AlchemyTableBlock extends Block {
    public AlchemyTableBlock() {
        super(Properties.of(Material.WOOD));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new AlchemyTableTileEntity();
    }
}
