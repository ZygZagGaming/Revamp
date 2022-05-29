package com.zygzag.revamp.common.block;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.charge.EnergyCharge;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ChargeCrystalBlock extends Block {
    private final float charge;
    public ChargeCrystalBlock(Properties properties, float charge) {
        super(properties);
        this.charge = charge;
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState state2, boolean flag) {
        GeneralUtil.ifCapability(world.getChunkAt(pos), Revamp.CHUNK_CHARGE_CAPABILITY, (h) -> h.charges.put(pos, new EnergyCharge(charge, pos, h)));
    }
}
