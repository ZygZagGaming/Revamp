package com.zygzag.revamp.common.block.entity;

import com.zygzag.revamp.common.entity.CannonFiredShulkerBullet;
import com.zygzag.revamp.common.registry.BlockEntityTypeRegistry;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ShulkstoneCannonBlockEntity extends BlockEntity {
    public int shotCooldown = 0;
    public final int ticksPerFire = 40;

    public ShulkstoneCannonBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypeRegistry.SHULKSTONE_CANNON.get(), pos, state);
    }

    public void tick(Level world, BlockPos pos, BlockState state) {
        if (shotCooldown > 0) shotCooldown--;
        if (state.getValue(BlockStateProperties.POWERED)) {
            if (shotCooldown == 0) {
                shotCooldown = ticksPerFire;
                CannonFiredShulkerBullet bullet = new CannonFiredShulkerBullet(world, state.getValue(BlockStateProperties.FACING));
                bullet.setPos(GeneralUtil.vec3iToVec3Centered(pos).subtract(0, 0.15625, 0));
                world.addFreshEntity(bullet);
            }
        }
    }
}
