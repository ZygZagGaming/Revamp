package com.zygzag.revamp.common.block;

import com.zygzag.revamp.common.registry.Registry;
import com.zygzag.revamp.util.Constants;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArcCrystalBlock extends Block {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public ArcCrystalBlock(Properties prop) {
        super(prop);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        Direction clicked = ctx.getClickedFace();
        Direction dir = clicked.getOpposite();
        Level world = ctx.getLevel();
        BlockPos relative = pos.relative(dir);
        if (world.getBlockState(relative).isFaceSturdy(world, relative, clicked)) {
            return defaultBlockState().setValue(FACING, clicked);
        } else {
            for (Direction d2 : Direction.values()) {
                relative = pos.relative(d2);
                if (d2 != dir && world.getBlockState(relative).isFaceSturdy(world, relative, d2)) return defaultBlockState().setValue(FACING, d2);
            }
        }
        return null;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos relative = pos.relative(facing.getOpposite());
        return world.getBlockState(relative).isFaceSturdy(world, relative, facing);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    public static int getColor(BlockState state, @Nullable BlockAndTintGetter getter, @Nullable BlockPos pos, int n) {
        System.out.println("bruh, " + getter);
        if (getter instanceof Level world && pos != null) {
            float charge = GeneralUtil.getChargeAt(world, pos);
            return getColor(charge);
        }
        else return Constants.CRYSTAL_NEGATIVE_COLOR;
    }

    public static int getColor(float charge) {
        if (charge == 0) return Constants.CRYSTAL_NEUTRAL_COLOR;
        else if (charge > 0) {
            float t = charge / 20;
            return GeneralUtil.lerpColor(Constants.CRYSTAL_NEUTRAL_COLOR, Constants.CRYSTAL_POSITIVE_COLOR, t);
        } else {
            float t = -charge / 20;
            return GeneralUtil.lerpColor(Constants.CRYSTAL_NEUTRAL_COLOR, Constants.CRYSTAL_NEGATIVE_COLOR, t);
        }
    }
}
