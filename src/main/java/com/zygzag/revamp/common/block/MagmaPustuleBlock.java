package com.zygzag.revamp.common.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MagmaPustuleBlock extends Block {
    public static final DirectionProperty FACE = DirectionProperty.create("face");
    public static final VoxelShape DOWN_SHAPE = box(6, 0, 6, 10, 2, 10);
    public static final VoxelShape UP_SHAPE = box(6, 14, 6, 10, 16, 10);
    public static final VoxelShape NORTH_SHAPE = box(6, 6, 0, 10, 10, 2);
    public static final VoxelShape SOUTH_SHAPE = box(6, 6, 14, 10, 10, 16);
    public static final VoxelShape WEST_SHAPE = box(0, 6, 6, 2, 10, 10);
    public static final VoxelShape EAST_SHAPE = box(14, 6, 6, 16, 10, 10);
    public static final DamageSource PUSTULE_DAMAGE = new DamageSource("pustule");

    public MagmaPustuleBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACE, Direction.DOWN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACE);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockPos p = pos.relative(state.getValue(FACE));
        return world.getBlockState(p).isFaceSturdy(world, p, state.getValue(FACE).getOpposite());
    }

    public static VoxelShape shape(Direction direction) {
        return switch (direction) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
        };
    }

    public BlockState state(Direction direction) {
        return defaultBlockState().setValue(FACE, direction);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return world.getBlockState(pos).is(this) ? shape(world.getBlockState(pos).getValue(FACE)) : Shapes.empty();
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (entity.getBoundingBox().intersects(shape(state.getValue(FACE)).bounds().move(pos).inflate(0.0625))) {
            world.destroyBlock(pos, true);
            if (entity instanceof LivingEntity living) {
                living.hurt(PUSTULE_DAMAGE, 4f);
                if (!(living instanceof Player p) || !p.getAbilities().instabuild) living.setRemainingFireTicks(Math.max(living.getRemainingFireTicks(), 60));
            }
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return state(ctx.getClickedFace().getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState otherState, LevelAccessor world, BlockPos pos, BlockPos otherPos) {
        if (!state.canSurvive(world, pos)) return Blocks.AIR.defaultBlockState();
        return state;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }
}
