package com.zygzag.revamp.common.block;

import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OsteumBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty X = BooleanProperty.create("x");
    public static final BooleanProperty Y = BooleanProperty.create("y");
    public static final BooleanProperty Z = BooleanProperty.create("z");
    public static BooleanProperty getProperty(Direction.Axis a) {
        return switch(a) {
            case X -> X;
            case Y -> Y;
            case Z -> Z;
        };
    }
    private static VoxelShape getShape(Direction.Axis a) {
        return switch(a) {
            case X -> X_SHAPE;
            case Y -> Y_SHAPE;
            case Z -> Z_SHAPE;
        };
    }
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape X_SHAPE = Block.box(0, 6.5, 6.5, 16, 9.5, 9.5);
    protected static final VoxelShape Y_SHAPE = Block.box(6.5, 0, 6.5, 9.5, 16, 9.5);
    protected static final VoxelShape Z_SHAPE = Block.box(6.5, 6.5, 0, 9.5, 9.5, 16);

    public OsteumBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(X, false)
                        .setValue(Y, true)
                        .setValue(Z, false)
                        .setValue(WATERLOGGED, false)
        );
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(X, Y, Z, WATERLOGGED);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.is(this)) return state.setValue(getProperty(face.getAxis()), true);
        return defaultBlockState().setValue(Y, false).setValue(getProperty(face.getAxis()), true);
    }

    static HashMap<BlockState, VoxelShape> cache = new HashMap<>();

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape = null;
        for (Direction.Axis axis : Direction.Axis.VALUES) {
            if (state.getValue(getProperty(axis))) {
                if (shape == null) shape = getShape(axis);
                else shape = Shapes.or(shape, getShape(axis));
            }
        }
        return shape == null ? Y_SHAPE : shape;
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext ctx) {
        ItemStack itemstack = ctx.getItemInHand();
        Direction.Axis axis = ctx.getClickedFace().getAxis();
        return itemstack.is(this.asItem()) && !state.getValue(getProperty(axis));
    }
}
