package com.zygzag.revamp.common.block;

import com.zygzag.revamp.common.block.entity.ShulkstoneCannonBlockEntity;
import com.zygzag.revamp.common.entity.CannonFiredShulkerBullet;
import com.zygzag.revamp.common.registry.BlockEntityTypeRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ShulkstoneCannonBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static Map<Direction, VoxelShape> SHAPES = Map.of(
            Direction.UP, Shapes.or(Shapes.box(0 / 16.0, 0 / 16.0, 0 / 16.0, 16 / 16.0, 7 / 16.0, 16 / 16.0), Shapes.box(4 / 16.0, 7 / 16.0, 4 / 16.0, 12 / 16.0, 16 / 16.0, 12 / 16.0)),
            Direction.DOWN, Shapes.or(Shapes.box(0 / 16.0, 9 / 16.0, 0 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0), Shapes.box(4 / 16.0, 0 / 16.0, 4 / 16.0, 12 / 16.0, 9 / 16.0, 12 / 16.0)),
            Direction.SOUTH, Shapes.or(Shapes.box(0 / 16.0, 0 / 16.0, 0 / 16.0, 16 / 16.0, 16 / 16.0, 7 / 16.0), Shapes.box(4 / 16.0, 4 / 16.0, 7 / 16.0, 12 / 16.0, 12 / 16.0, 16 / 16.0)),
            Direction.NORTH, Shapes.or(Shapes.box(0 / 16.0, 0 / 16.0, 9 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0), Shapes.box(4 / 16.0, 4 / 16.0, 0 / 16.0, 12 / 16.0, 12 / 16.0, 9 / 16.0)),
            Direction.WEST, Shapes.or(Shapes.box(9 / 16.0, 0 / 16.0, 0 / 16.0, 16 / 16.0, 16 / 16.0, 16 / 16.0), Shapes.box(0 / 16.0, 4 / 16.0, 4 / 16.0, 9 / 16.0, 12 / 16.0, 12 / 16.0)),
            Direction.EAST, Shapes.or(Shapes.box(0 / 16.0, 0 / 16.0, 0 / 16.0, 7 / 16.0, 16 / 16.0, 16 / 16.0), Shapes.box(7 / 16.0, 4 / 16.0, 4 / 16.0, 16 / 16.0, 12 / 16.0, 12 / 16.0))
        );

    public ShulkstoneCannonBlock(Properties prop) {
        super(prop);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return SHAPES.get(state.getValue(BlockStateProperties.FACING));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        if (ctx instanceof EntityCollisionContext ectx && ectx.getEntity() instanceof CannonFiredShulkerBullet blt && blt.tickCount < 10) return Shapes.empty();
        else return super.getCollisionShape(state, world, pos, ctx);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.WATERLOGGED, BlockStateProperties.FACING, BlockStateProperties.POWERED);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidstate = ctx.getLevel().getFluidState(ctx.getClickedPos());
        return this.defaultBlockState().setValue(BlockStateProperties.POWERED, false).setValue(BlockStateProperties.FACING, ctx.getNearestLookingDirection().getOpposite()).setValue(BlockStateProperties.WATERLOGGED, fluidstate.is(FluidTags.WATER) && fluidstate.getAmount() == 8);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState otherState, LevelAccessor world, BlockPos pos, BlockPos otherPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return state;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos otherPos, boolean idfk) {
        if (!world.isClientSide) {
            boolean flag = world.hasNeighborSignal(pos);
            if (flag != state.getValue(BlockStateProperties.POWERED)) {
                world.setBlock(pos, state.setValue(BlockStateProperties.POWERED, flag), 2);
            }
        }
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShulkstoneCannonBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        if (type == BlockEntityTypeRegistry.SHULKSTONE_CANNON.get()) return createTickerHelper(type, BlockEntityTypeRegistry.SHULKSTONE_CANNON.get(), (w, p, s, e) -> e.tick(w, p, s));
        else return null;
    }
}
