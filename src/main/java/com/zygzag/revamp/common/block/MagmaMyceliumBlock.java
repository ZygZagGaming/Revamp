package com.zygzag.revamp.common.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MyceliumBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LayerLightEngine;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagmaMyceliumBlock extends Block {
    public MagmaMyceliumBlock(Properties properties) {
        super(properties);
    }

    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if (!entity.fireImmune() && entity instanceof LivingEntity l && !EnchantmentHelper.hasFrostWalker(l)) {
            entity.hurt(DamageSource.HOT_FLOOR, 1.0F);
        }

        super.stepOn(world, pos, state, entity);
    }

    private static boolean canPropagate(BlockState state, LevelReader world, BlockPos pos) {
        BlockState k = world.getBlockState(pos.above());
        return (!k.isFaceSturdy(world, pos.above(), Direction.DOWN) || k.isAir()) && k.getFluidState().isEmpty();
    }

    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rng) {
        if (!world.isAreaLoaded(pos, 3)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
        BlockState blockstate = this.defaultBlockState();
        BlockState k = world.getBlockState(pos.above());
        if ((k.isFaceSturdy(world, pos.above(), Direction.DOWN) && !k.isAir()) || !k.getFluidState().isEmpty()) world.setBlockAndUpdate(pos, Blocks.NETHERRACK.defaultBlockState());

        for(int i = 0; i < 4; ++i) {
            BlockPos blockpos = pos.offset(rng.nextInt(3) - 1, rng.nextInt(5) - 3, rng.nextInt(3) - 1);
            if (world.getBlockState(blockpos).is(Blocks.NETHERRACK) && canPropagate(blockstate, world, blockpos)) {
                world.setBlockAndUpdate(blockpos, blockstate);
            }
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }
}
