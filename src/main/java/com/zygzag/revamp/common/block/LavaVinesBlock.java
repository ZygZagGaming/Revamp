package com.zygzag.revamp.common.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LavaVinesBlock extends HorizontalDirectionalBlock implements BonemealableBlock {
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);
    public enum Type implements StringRepresentable {
        END,
        MIDDLE;

        @Override
        public String getSerializedName() {
            return toString().toLowerCase();
        }
    };
    public static final double SIZE = 5;
    public static final double SIZE_MIN = 8 - SIZE / 2;
    public static final double SIZE_MAX = 8 + SIZE / 2;
    public static final double END_LENGTH = 12;
    public static final VoxelShape X_AXIS_AABB = Block.box(0.0D, SIZE_MIN, SIZE_MIN, 16.0D, SIZE_MAX, SIZE_MAX);
    public static final VoxelShape Z_AXIS_AABB = Block.box(SIZE_MIN, SIZE_MIN, 0.0D, SIZE_MAX, SIZE_MAX, 16.0D);
    public static final VoxelShape NORTH_END_AABB = Block.box(SIZE_MIN, SIZE_MIN, 16 - END_LENGTH, SIZE_MAX, SIZE_MAX, 16.0D);
    public static final VoxelShape SOUTH_END_AABB = Block.box(SIZE_MIN, SIZE_MIN, 0.0D, SIZE_MAX, SIZE_MAX, END_LENGTH);
    public static final VoxelShape EAST_END_AABB = Block.box(0.0D, SIZE_MIN, SIZE_MIN, END_LENGTH, SIZE_MAX, SIZE_MAX);
    public static final VoxelShape WEST_END_AABB = Block.box(16 - END_LENGTH, SIZE_MIN, SIZE_MIN, 16.0D, SIZE_MAX, SIZE_MAX);
    public LavaVinesBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(TYPE, Type.END));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
        builder.add(TYPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        BlockState clickedState = world.getBlockState(ctx.getClickedPos().relative(ctx.getClickedFace().getOpposite()));
        if ((clickedState.isFaceSturdy(world, ctx.getClickedPos().relative(ctx.getClickedFace().getOpposite()), ctx.getClickedFace()) || (clickedState.is(this) && clickedState.getValue(FACING) == ctx.getClickedFace())) && !ctx.getClickedFace().getAxis().isVertical()) return defaultBlockState().setValue(FACING, ctx.getClickedFace());
        for (Direction dir : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            BlockPos p = pos.relative(dir);
            BlockState state = world.getBlockState(p);
            if (state.isFaceSturdy(world, p, dir.getOpposite()) || (state.is(this) && state.getValue(FACING) == dir)) return defaultBlockState().setValue(FACING, dir);
        }
        return null;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        Direction dir = state.getValue(FACING);
        Type type = state.getValue(TYPE);
        if (type == Type.MIDDLE) return switch (dir) {
            case UP, DOWN, EAST, WEST -> X_AXIS_AABB;
            case NORTH, SOUTH -> Z_AXIS_AABB;
        };
        else return switch (dir) {
            case NORTH, UP, DOWN -> NORTH_END_AABB;
            case SOUTH -> SOUTH_END_AABB;
            case EAST -> EAST_END_AABB;
            case WEST -> WEST_END_AABB;
        };
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType pathType) {
        return true;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor world, BlockPos pos, BlockPos otherPos) {
        if (!state.canSurvive(world, pos)) return Blocks.AIR.defaultBlockState();
        Direction d1 = state.getValue(FACING);
        if (direction.getAxis() == d1.getAxis()) {
            if (state.is(this) && otherState.is(this) && d1 == otherState.getValue(FACING) && d1 == direction) {
                return state.setValue(TYPE, Type.MIDDLE);
            } else {
                return state.setValue(TYPE, Type.END);
            }
        }
        return state;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction dir = state.getValue(FACING);
        BlockPos pos2 = pos.relative(dir.getOpposite());
        BlockState state2 = world.getBlockState(pos2);
        return state2.isFaceSturdy(world, pos2, dir) || (state2.is(this) && state2.getValue(FACING).getAxis() == dir.getAxis());
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rng) {
        if (rng.nextDouble() < 0.25) {
            Direction facing = state.getValue(FACING);
            if (world.getBlockState(pos.relative(facing)).isAir()) {
                world.setBlockAndUpdate(pos.relative(facing), defaultBlockState().setValue(FACING, facing));
                world.setBlockAndUpdate(pos, state.setValue(TYPE, Type.MIDDLE));
            }
        }
    }

    public boolean isValidBonemealTarget(BlockGetter world, BlockPos pos, BlockState state, boolean bool) {
        BlockPos pos2 = pos.relative(state.getValue(FACING));
        BlockState state2 = world.getBlockState(pos.relative(state.getValue(FACING)));
        return (state2.is(this) && state2.getBlock() instanceof LavaVinesBlock b && b.isValidBonemealTarget(world, pos2, state2, bool)) || state2.isAir();
    }

    public boolean isBonemealSuccess(Level world, Random rng, BlockPos pos, BlockState state) {
        return true;
    }

    public void performBonemeal(ServerLevel world, Random rng, BlockPos pos, BlockState state) {
        Direction dir = state.getValue(FACING);

        BlockState kState = world.getBlockState(pos.relative(dir));
        if (kState.is(this) && kState.getBlock() instanceof LavaVinesBlock b) {
            b.performBonemeal(world, rng, pos.relative(dir), kState);
        } else {

            int numberToGrow = rng.nextInt(1, 6);
            for (int i = 1; i <= numberToGrow; i++) {
                BlockPos k = pos.relative(dir, i);
                if (world.getBlockState(k).isAir()) {
                    world.setBlockAndUpdate(k, state.setValue(TYPE, Type.END));
                    world.setBlockAndUpdate(k.relative(dir, -1), state.setValue(TYPE, Type.MIDDLE));
                }
            }
        }
    }
}
