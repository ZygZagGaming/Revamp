package com.zygzag.revamp.common.block;

import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OsteumBlock extends Block implements SimpleWaterloggedBlock {
    private static final BooleanProperty NORTH = BooleanProperty.create("north");
    private static final BooleanProperty SOUTH = BooleanProperty.create("south");
    private static final BooleanProperty EAST = BooleanProperty.create("east");
    private static final BooleanProperty WEST = BooleanProperty.create("west");
    private static final BooleanProperty UP = BooleanProperty.create("up");
    private static final BooleanProperty DOWN = BooleanProperty.create("down");
    private static BooleanProperty getProperty(Direction d) {
        return switch(d) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }
    private static VoxelShape getShape(Direction d) {
        return switch(d) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
            case UP -> UP_SHAPE;
            case DOWN -> DOWN_SHAPE;
        };
    }
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape CENTRAL_SHAPE = Block.box(6.5, 6.5, 6.5, 9.5, 9.5, 9.5);
    protected static final VoxelShape NORTH_SHAPE = Block.box(6.5, 6.5, 0, 9.5, 9.5, 6.5);
    protected static final VoxelShape SOUTH_SHAPE = Block.box(6.5, 6.5, 9.5, 9.5, 9.5, 16);
    protected static final VoxelShape EAST_SHAPE = Block.box(9.5, 6.5, 6.5, 16, 9.5, 9.5);
    protected static final VoxelShape WEST_SHAPE = Block.box(0, 6.5, 6.5, 6.5, 9.5, 9.5);
    protected static final VoxelShape UP_SHAPE = Block.box(6.5, 9.5, 6.5, 9.5, 16, 9.5);
    protected static final VoxelShape DOWN_SHAPE = Block.box(6.5, 0, 6.5, 9.5, 6.5, 9.5);

    public OsteumBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(NORTH, false)
                        .setValue(SOUTH, false)
                        .setValue(EAST, false)
                        .setValue(WEST, false)
                        .setValue(UP, true)
                        .setValue(DOWN, true)
                        .setValue(WATERLOGGED, false)
        );
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, WATERLOGGED);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        return defaultBlockState().setValue(UP, false).setValue(DOWN, false).setValue(getProperty(face), true).setValue(getProperty(face.getOpposite()), true);
    }

    static HashMap<BlockState, VoxelShape> cache = new HashMap<>();

    public static VoxelShape makeShape(BlockState state) {
        if (cache.containsKey(state)) return cache.get(state);
        VoxelShape shape = CENTRAL_SHAPE;
        for (Direction direction : Direction.values()) {
            if (state.getValue(getProperty(direction))) {
                shape = Shapes.or(shape, getShape(direction));
            }
        }
        cache.put(state, shape);
        return shape;
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return makeShape(state);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState state1, boolean flag) {
        super.onPlace(state, world, pos, state1, flag);
        for (Direction dir : Direction.values()) {
            BlockState state2 = world.getBlockState(pos.relative(dir));
            if (state2.is(Registry.OSTEUM.get())) {
                System.out.println("osteum located in dir " + dir);
                state.setValue(getProperty(dir), true);
                world.setBlock(pos, state, 1);
                state2.setValue(getProperty(dir.getOpposite()), true);
                world.setBlock(pos.relative(dir), state2, 1);
            }
        }
    }
}
