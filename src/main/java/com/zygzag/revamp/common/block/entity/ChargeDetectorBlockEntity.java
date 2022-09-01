package com.zygzag.revamp.common.block.entity;

import com.zygzag.revamp.common.registry.BlockEntityTypeRegistry;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.DiodeBlock.POWERED;
import static net.minecraft.world.level.block.HorizontalDirectionalBlock.FACING;


public class ChargeDetectorBlockEntity extends BlockEntity {
    public int output = 0;
    public ChargeDetectorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityTypeRegistry.CHARGE_DETECTOR_BLOCK_ENTITY.get(), pos, state);
    }

    public static float chargeToSignalStrength(float charge) {
        return (7.5f * charge / 20f) + 7.5f;
    }

    public static int getSignal(BlockPos pos, Level world, BlockState state) {
        Direction facing = state.getValue(FACING);
        BlockPos toDetect = pos.relative(facing);
        float charge = GeneralUtil.getChargeAt(world, toDetect);
        return Math.round(chargeToSignalStrength(charge));
    }

    public static void serverTick(Level world, BlockPos pos, BlockState state, ChargeDetectorBlockEntity blockEntity) {
        blockEntity.serverTick(world, pos, state);
    }

    public void serverTick(Level world, BlockPos pos, BlockState state) {
        int signal = getSignal(pos, world, state);
        float charge = GeneralUtil.getChargeAt(world, pos.relative(state.getValue(FACING)));
        if (output != signal) {
            output = signal;
            world.updateNeighborsAt(pos, state.getBlock());
        }
        world.setBlockAndUpdate(pos, state.setValue(POWERED, output != 0));
        //System.out.println("charge is: " + charge + ", signal should be: " + chargeToSignalStrength(charge));
    }
}
