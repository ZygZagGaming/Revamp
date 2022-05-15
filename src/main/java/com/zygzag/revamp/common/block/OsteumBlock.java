package com.zygzag.revamp.common.block;

import com.zygzag.revamp.common.tag.RevampTags;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OsteumBlock extends Block implements BucketPickup, LiquidBlockContainer, ConditionalStickyBlock {
    public static final BooleanProperty X = BooleanProperty.create("x");
    public static final BooleanProperty Y = BooleanProperty.create("y");
    public static final BooleanProperty Z = BooleanProperty.create("z");
    public static final EnumProperty<OsteumSideState> NORTH = EnumProperty.create("north", OsteumSideState.class);
    public static final EnumProperty<OsteumSideState> SOUTH = EnumProperty.create("south", OsteumSideState.class);
    public static final EnumProperty<OsteumSideState> EAST = EnumProperty.create("east", OsteumSideState.class);
    public static final EnumProperty<OsteumSideState> WEST = EnumProperty.create("west", OsteumSideState.class);
    public static final EnumProperty<OsteumSideState> UP = EnumProperty.create("up", OsteumSideState.class);
    public static final EnumProperty<OsteumSideState> DOWN = EnumProperty.create("down", OsteumSideState.class);
    public static final EnumProperty<FluidState> FLUID_STATE = EnumProperty.create("fluid_state", FluidState.class);
    private final HashMap<Direction.Axis, BlockState> stateMap = new HashMap<>();
    public static BooleanProperty getProperty(Direction.Axis a) {
        return switch(a) {
            case X -> X;
            case Y -> Y;
            case Z -> Z;
        };
    }
    public static EnumProperty<OsteumSideState> getProperty(Direction a) {
        return switch(a) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }
    private static VoxelShape getShape(Direction.Axis a) {
        return switch(a) {
            case X -> X_SHAPE;
            case Y -> Y_SHAPE;
            case Z -> Z_SHAPE;
        };
    }
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
                        .setValue(NORTH, OsteumSideState.NONE)
                        .setValue(SOUTH, OsteumSideState.NONE)
                        .setValue(EAST, OsteumSideState.NONE)
                        .setValue(WEST, OsteumSideState.NONE)
                        .setValue(UP, OsteumSideState.NORMAL)
                        .setValue(DOWN, OsteumSideState.NORMAL)
                        .setValue(FLUID_STATE, FluidState.NONE)
        );
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(X, Y, Z, NORTH, SOUTH, EAST, WEST, UP, DOWN, FLUID_STATE);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.is(this)) return state.setValue(getProperty(face.getAxis()), true).setValue(getProperty(face), OsteumSideState.NORMAL).setValue(getProperty(face.getOpposite()), OsteumSideState.NORMAL);
        return getStateForAxis(face.getAxis()).setValue(FLUID_STATE, FluidState.state(context.getLevel().getFluidState(context.getClickedPos()).getType()));
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

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(Y) && state.getValue(X) && state.getValue(Z) && state.getValue(FLUID_STATE) == FluidState.LAVA;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rng) {
        if (rng.nextDouble() < 0.25) {
            grow(state, world, pos, rng, 0);
        }
    }

    public void grow(BlockState state, ServerLevel world, BlockPos pos, Random rng, int amount) {
        ArrayList<Direction> dirs = new ArrayList<>(Arrays.stream(Direction.values()).toList());
        if (amount == 10) return;
        for (Direction dir : dirs)
            if (rng.nextDouble() < 0.25) {
                BlockPos relative = pos.relative(dir);
                if (state.getValue(getProperty(dir.getAxis()))) {
                    BlockState otherState = world.getBlockState(relative);
                    if (otherState.is(this)) {
                        if (otherState.getValue(getProperty(dir.getAxis())))
                            grow(otherState, world, relative, rng, amount + 1);
                        else world.setBlockAndUpdate(relative, otherState.setValue(getProperty(dir.getAxis()), true).setValue(getProperty(dir), OsteumSideState.NORMAL).setValue(getProperty(dir.getOpposite()), OsteumSideState.NORMAL));
                    } else if (otherState.is(RevampTags.OSTEUM_REPLACEABLE.get()))
                        world.setBlockAndUpdate(relative, ((GrowingOsteumBlock) Registry.BlockRegistry.GROWING_OSTEUM.get()).getStateForDirection(dir));
                }
            }
    }

    public BlockState getStateForAxis(Direction.Axis axis) {
        if (stateMap.containsKey(axis)) return stateMap.get(axis);
        BlockState dbs = defaultBlockState().setValue(Y, false).setValue(getProperty(axis), true).setValue(UP, OsteumSideState.NONE).setValue(DOWN, OsteumSideState.NONE);
        for (Direction dir : Direction.values()) {
            if (dir.getAxis() == axis) dbs = dbs.setValue(getProperty(dir), OsteumSideState.NORMAL);
        }
        stateMap.put(axis, dbs);
        return dbs;
    }

    @Override
    public boolean canStickTo(BlockState state, BlockState other, BlockPos pos, BlockPos otherPos, Direction dir) {
        return state.getValue(getProperty(dir)) == OsteumSideState.STICKY;
    }

    public enum FluidState implements StringRepresentable {
        NONE("none", Fluids.EMPTY),
        WATER("water", Fluids.WATER),
        LAVA("lava", Fluids.LAVA);

        private String name;
        private Fluid fluid;

        FluidState(String name, Fluid fluid) {
            this.name = name;
            this.fluid = fluid;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public Fluid getFluid() {
            return fluid;
        }

        public Item getBucketItem() {
            if (fluid == Fluids.WATER) return Items.WATER_BUCKET;
            else if (fluid == Fluids.LAVA) return Items.LAVA_BUCKET;
            else return Items.BUCKET;
        }

        public static FluidState state(Fluid fluid) {
            if (fluid == Fluids.WATER) return WATER;
            else if (fluid == Fluids.LAVA) return LAVA;
            else return NONE;
        }
    }


    public boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        return state.getValue(FLUID_STATE) == FluidState.NONE;
    }

    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, net.minecraft.world.level.material.FluidState fluidState) {
        Fluid fluid = fluidState.getType();
        if (state.getValue(FLUID_STATE) == FluidState.NONE) {
            if (!world.isClientSide()) {
                world.setBlock(pos, state.setValue(FLUID_STATE, FluidState.state(fluid)), 3);
                world.scheduleTick(pos, fluid, fluid.getTickDelay(world));
            }
            return true;
        } else {
            return false;
        }
    }

    public ItemStack pickupBlock(LevelAccessor world, BlockPos pos, BlockState state) {
        FluidState fluidState = state.getValue(FLUID_STATE);
        if (state.getValue(FLUID_STATE) != FluidState.NONE) {
            world.setBlock(pos, state.setValue(FLUID_STATE, FluidState.NONE), 3);
            if (!state.canSurvive(world, pos)) {
                world.destroyBlock(pos, true);
            }

            return new ItemStack(fluidState.getBucketItem());
        } else {
            return ItemStack.EMPTY;
        }
    }

    public Optional<SoundEvent> getPickupSound() {
        return Optional.empty();
    }

    @Override
    public Optional<SoundEvent> getPickupSound(BlockState state) {
        return state.getValue(FLUID_STATE).getFluid().getPickupSound();
    }

    @Override
    public net.minecraft.world.level.material.FluidState getFluidState(BlockState state) {
        net.minecraft.world.level.material.FluidState s = state.getValue(FLUID_STATE).getFluid().defaultFluidState();
        return s;
    }

    public BlockState updateShape(BlockState state, Direction dir, BlockState other, LevelAccessor world, BlockPos pos, BlockPos pos2) {
        if (state.getValue(FLUID_STATE) != FluidState.NONE) {
            Fluid fluid = state.getValue(FLUID_STATE).getFluid();
            world.scheduleTick(pos, fluid, fluid.getTickDelay(world));
        }

        return super.updateShape(state, dir, other, world, pos, pos2);
    }

    public enum OsteumSideState implements StringRepresentable {
        NONE("none"),
        NORMAL("normal"),
        STICKY("sticky"),
        SHARPENED("sharpened");

        private final String name;
        OsteumSideState(String name){
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack = player.getItemInHand(hand);
        Direction dir = result.getDirection();
        if (stack.is(Items.SLIME_BALL) && state.getValue(getProperty(dir)) == OsteumSideState.NORMAL) {
            world.setBlockAndUpdate(pos, state.setValue(getProperty(dir), OsteumSideState.STICKY));
            world.playSound(null, pos, SoundEvents.SLIME_BLOCK_BREAK, SoundSource.BLOCKS, 1, 1);
            return InteractionResult.SUCCESS;
        } else if (stack.getItem() instanceof AxeItem) {
            OsteumSideState state1 = state.getValue(getProperty(dir));
            if (state1 == OsteumSideState.STICKY) {
                world.setBlockAndUpdate(pos, state.setValue(getProperty(dir), OsteumSideState.NORMAL));
                return InteractionResult.SUCCESS;
            } else if (state1 == OsteumSideState.NORMAL) {
                world.setBlockAndUpdate(pos, state.setValue(getProperty(dir), OsteumSideState.SHARPENED));
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, world, pos, player, hand, result);
    }

    @Override
    public boolean isStickyBlock(BlockState state) {
        for (Direction dir : Direction.values()) {
            if (state.getValue(getProperty(dir)) == OsteumSideState.STICKY) return true;
        }
        return false;
    }

    @Override
    public void fallOn(Level world, BlockState state, BlockPos pos, Entity mob, float f) {
        if (state.getValue(UP) == OsteumSideState.SHARPENED) {
            mob.causeFallDamage(f + 2.0F, 2.0F, DamageSource.STALAGMITE);
        } else {
            super.fallOn(world, state, pos, mob, f);
        }

    }
}
