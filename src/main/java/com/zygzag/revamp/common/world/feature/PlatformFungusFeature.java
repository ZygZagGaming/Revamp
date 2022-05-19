package com.zygzag.revamp.common.world.feature;

import com.mojang.serialization.Codec;
import com.zygzag.revamp.common.block.LavaVinesBlock;
import com.zygzag.revamp.common.registry.Registry;
import com.zygzag.revamp.common.tag.RevampTags;
import com.zygzag.revamp.common.world.PlatformFungusConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PlatformFungusFeature extends Feature<PlatformFungusConfiguration> {
    public PlatformFungusFeature(Codec<PlatformFungusConfiguration> codec) {
        super(codec);
    }

    public BlockPos correctOrigin(LevelAccessor world, BlockPos old, int maxDistance) {
        int i;
        for (i = 0; i <= maxDistance; i++) if (world.getBlockState(old.below(i)).getFluidState().is(Fluids.LAVA) && world.getBlockState(old.below(i - 1)).isAir()) return old.below(i);
        for (i = 0; i <= maxDistance; i++) if (world.getBlockState(old.above(i)).getFluidState().is(Fluids.LAVA) && world.getBlockState(old.above(i + 1)).isAir()) return old.above(i);
        return old;
    }

    @Override
    public boolean place(FeaturePlaceContext<PlatformFungusConfiguration> ctx) {
        WorldGenLevel world = ctx.level();
        BlockPos origin = correctOrigin(world, ctx.origin(), 7); // origin (should be) 1 block above mycelium
        while (world.getBlockState(origin).is(Blocks.LAVA)) origin = origin.above();
        PlatformFungusConfiguration config = ctx.config();
        HashMap<BlockPos, BlockState> toPlace = new HashMap<>();
        Random rng = world.getRandom();
        int rootDepth = 2;
        while (world.getBlockState(origin.below(rootDepth)).getFluidState().is(Fluids.LAVA)) rootDepth++;

        generateMushroom(
                toPlace,
                config,
                origin,
                rootDepth,
                (i) -> rng.nextInt(6 - 2 * i, 12 - 2 * i),
                (i) -> Math.max(rng.nextInt(6 - i, 12 - i), 3),
                (i) -> rng.nextInt(-2, 3),
                (i) -> rng.nextInt(-2, 3),
                rng.nextInt(1, 6),
                rng
        );

        generateVegetation(toPlace, rng);

        for (Map.Entry<BlockPos, BlockState> entry : toPlace.entrySet()) {
            BlockState s = world.getBlockState(entry.getKey());
            boolean flag = !s.is(RevampTags.MAGMA_FUNGUS_REPLACEABLE.get());
            if (flag) return false;
        }
        for (Map.Entry<BlockPos, BlockState> entry : toPlace.entrySet()) world.setBlock(entry.getKey(), entry.getValue(), 3);

        return true;
    }

    public void generateVegetation(HashMap<BlockPos, BlockState> toPlace, Random rng) {
        HashMap<BlockPos, BlockState> copy = new HashMap<>(toPlace);
        for (Map.Entry<BlockPos, BlockState> entry : copy.entrySet()) {
            if (entry.getValue().is(Registry.BlockRegistry.MAGMA_FUNGUS_CAP_BLOCK.get()) || entry.getValue().is(Registry.BlockRegistry.MAGMA_FUNGUS_EDGE_BLOCK.get())) {
                for (Direction dir : Direction.values()) {
                    if (dir != Direction.DOWN) {
                        BlockPos bp2 = entry.getKey().relative(dir);
                        BlockState k = toPlace.getOrDefault(bp2, null);
                        if (k == null || k.isAir()) {
                            if (dir == Direction.UP) {
                                if (rng.nextDouble() < 0.125) toPlace.put(bp2, Registry.BlockRegistry.MAGMA_PUSTULE_BLOCK.get().defaultBlockState());
                            } else {
                                int n = 0;
                                while (rng.nextDouble() < 0.25 && n < 5) n++;
                                for (int i = 0; i < n; i++) {
                                    BlockState k2 =  Registry.BlockRegistry.LAVA_VINES_BLOCK.get().defaultBlockState().setValue(LavaVinesBlock.FACING, dir);
                                    if (i != n - 1) k2 = k2.setValue(LavaVinesBlock.TYPE, LavaVinesBlock.Type.MIDDLE);
                                    toPlace.put(bp2.relative(dir, i), k2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @FunctionalInterface
    public interface IntProvider {
        int provide(int i);
    }

    public void generateMushroom(
            HashMap<BlockPos, BlockState> toPlace,
            PlatformFungusConfiguration config,
            BlockPos origin,
            int rootDepth,
            IntProvider heightProv,
            IntProvider radiusProv,
            IntProvider xOffsetProv,
            IntProvider zOffsetProv,
            int stackSize,
            Random rng
    ) {
        generateMushroom(toPlace, config, origin, rootDepth, heightProv, radiusProv, xOffsetProv, zOffsetProv, stackSize, 0, rng);
    }

    private void generateMushroom(
            HashMap<BlockPos, BlockState> toPlace,
            PlatformFungusConfiguration config,
            BlockPos origin,
            int rootDepth,
            IntProvider heightProv,
            IntProvider radiusProv,
            IntProvider xOffsetProv,
            IntProvider zOffsetProv,
            int stackSize,
            int i,
            Random rng
    ) {
        int height = heightProv.provide(i);
        int radius = radiusProv.provide(i);
        int xOffset = xOffsetProv.provide(i);
        int zOffset = zOffsetProv.provide(i);
        placeCircle(toPlace, origin.offset(xOffset * 0.4, height - 2, zOffset * 0.4), (radius * 0.4), config.ringState);
        placeCircle(toPlace, origin.offset(xOffset, height - 1, zOffset), radius, config.hatState);
        for (int k = 0; k < rootDepth; k++) toPlace.put(origin.below(k), config.stemState);
        for (int k = 1; k < height; k++) toPlace.put(origin.above(k), config.stemState);
        if (stackSize > i + 1) generateMushroom(toPlace, config, origin.above(height - 1), 0, heightProv, radiusProv, xOffsetProv, zOffsetProv, stackSize, i + 1, rng);
    }

    private void placeCircle(HashMap<BlockPos, BlockState> toPlace, BlockPos origin, double radius, BlockState state) {
        for (int i = 0; i < 2 * radius + 1; i++) for (int j = 0; j < 2 * radius + 1; j++) {
            BlockPos pos = origin.offset(i - radius, 0, j - radius);
            if (pos.distSqr(new Vec3i(origin.getX(), origin.getY(), origin.getZ())) <= radius * radius) {
                toPlace.put(pos, state);
            }
        }
    }

    private void placeHollowCircle(HashMap<BlockPos, BlockState> toPlace, BlockPos origin, double radius, BlockState state) {
        placeHollowCircle(toPlace, origin, radius, state, 360);
    }

    private void placeHollowCircle(HashMap<BlockPos, BlockState> toPlace, BlockPos origin, double radius, BlockState state, int steps) {
        for (int d = 0; d < steps; d++) {
            double rad = ((double) d / steps) * Math.PI * 2;
            BlockPos p = origin.offset(radius * Math.cos(rad) + 0.5, 0, radius * Math.sin(rad) + 0.5);
            toPlace.put(p, state);
        }
    }
}
