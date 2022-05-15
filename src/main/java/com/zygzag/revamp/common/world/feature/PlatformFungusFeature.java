package com.zygzag.revamp.common.world.feature;

import com.mojang.serialization.Codec;
import com.zygzag.revamp.common.world.PlatformFungusConfiguration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
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
        BlockPos origin = correctOrigin(world, ctx.origin(), 7); // origin (should be) 1 block above lava
        PlatformFungusConfiguration config = ctx.config();
        HashMap<BlockPos, BlockState> toPlace = new HashMap<>();
        boolean isDouble = Math.random() < 0.2;
        boolean isTriple = Math.random() < 0.075;
        if (!world.getBlockState(origin.below()).getFluidState().is(Fluids.LAVA)) return false;
        int height = (int) (Math.random() * 4) + 3; // [3, 7)
        int radius = (int) (Math.random() * 5) + 5; // [5, 10)
        int rootDepth = 1;
        while (world.getBlockState(origin.below(rootDepth)).getFluidState().is(Fluids.LAVA)) rootDepth++;
        for (int i = 0; i < rootDepth; i++) toPlace.put(origin.below(i), config.stemState); // place stems below lava
        for (int i = 0; i < height; i++) toPlace.put(origin.above(i), config.stemState); // place stems above lava
        placeCenterlessCircle(toPlace, origin.above(height - 2), (radius * 0.4), config.ringState, world);
        placeCenterlessCircle(toPlace, origin.above(height - 1), radius, config.hatState, world);
        placeHollowCircle(toPlace, origin.above(height - 1), radius, config.perimeterState, world);
        if (isDouble || isTriple) {
            int radius2 = (int) (Math.random() * 2) + 4; // [4, 6)
            int height2 = (int) (Math.random() * 3) + 4; // [4, 7)
            for (int i = 0; i < height2; i++) toPlace.put(origin.above(i + height), config.stemState);
            placeCenterlessCircle(toPlace, origin.above(height2 + height - 2), (radius2 * 0.4), config.ringState, world);
            placeCenterlessCircle(toPlace, origin.above(height2 + height - 1), radius2, config.hatState, world);
            placeHollowCircle(toPlace, origin.above(height2 + height - 1), radius2, config.perimeterState, world);
            if (isTriple) {
                int radius3 = (int) (Math.random() * 3) + 3; // [3, 6)
                int height3 = (int) (Math.random() * 3) + 3; // [3, 6]
                for (int i = 0; i < height3; i++) toPlace.put(origin.above(i + height + height2), config.stemState);
                placeCenterlessCircle(toPlace, origin.above(height3 + height2 + height - 2), (radius3 * 0.4), config.ringState, world);
                placeCenterlessCircle(toPlace, origin.above(height3 + height2 + height - 1), radius3, config.hatState, world);
                placeHollowCircle(toPlace, origin.above(height3 + height2 + height - 1), radius3, config.perimeterState, world);
            }
        }

        for (Map.Entry<BlockPos, BlockState> entry : toPlace.entrySet()) {
            BlockState s = world.getBlockState(entry.getKey());
            boolean flag = !world.getBlockState(entry.getKey()).isAir() && !world.getBlockState(entry.getKey()).is(Blocks.LAVA);
            if (flag) return false;
        }
        for (Map.Entry<BlockPos, BlockState> entry : toPlace.entrySet()) world.setBlock(entry.getKey(), entry.getValue(), 3);

        return true;
    }

    private void placeCenterlessCircle(HashMap<BlockPos, BlockState> toPlace, BlockPos origin, double radius, BlockState state, LevelAccessor world) {
        for (int i = 0; i < 2 * radius; i++) for (int j = 0; j < 2 * radius + 1; j++) {
            if (i != j || i != radius) { // if it's not the center block
                BlockPos pos = origin.offset(i - radius, 0, j - radius);
                if (pos.distSqr(new Vec3i(origin.getX(), origin.getY(), origin.getZ())) <= radius * radius) {
                    toPlace.put(pos, state);
                }
            }
        }
    }

    private void placeCircle(HashMap<BlockPos, BlockState> toPlace, BlockPos origin, double radius, BlockState state, LevelAccessor world) {
        for (int i = 0; i < 2 * (radius - 3) + 1; i++) for (int j = 0; j < 2 * (radius - 3) + 1; j++) {
            BlockPos pos = origin.offset(i - radius, 0, j - radius);
            if (pos.distSqr(new Vec3i(origin.getX(), origin.getY(), origin.getZ())) <= radius * radius) {
                toPlace.put(pos, state);
            }
        }
    }

    private void placeHollowCircle(HashMap<BlockPos, BlockState> toPlace, BlockPos origin, double radius, BlockState state, LevelAccessor world) {
        placeHollowCircle(toPlace, origin, radius, state, world, 360);
    }

    private void placeHollowCircle(HashMap<BlockPos, BlockState> toPlace, BlockPos origin, double radius, BlockState state, LevelAccessor world, int steps) {
        for (int d = 0; d < steps; d++) {
            double rad = ((double) d / steps) * Math.PI * 2;
            BlockPos p = origin.offset(radius * Math.cos(rad) + 0.5, 0, radius * Math.sin(rad) + 0.5);
            toPlace.put(p, state);
        }
    }
}
