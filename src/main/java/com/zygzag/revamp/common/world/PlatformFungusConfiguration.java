package com.zygzag.revamp.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class PlatformFungusConfiguration implements FeatureConfiguration {
    public static final Codec<PlatformFungusConfiguration> CODEC = RecordCodecBuilder.create(
            (builder) -> builder.group(
                    BlockState.CODEC.fieldOf("stem_state").forGetter((config) -> config.stemState),
                    BlockState.CODEC.fieldOf("hat_state").forGetter((config) -> config.hatState),
                    BlockState.CODEC.fieldOf("grass_state").forGetter((config) -> config.grassState),
                    BlockState.CODEC.fieldOf("sprout_state").forGetter((config) -> config.sproutState),
                    BlockState.CODEC.fieldOf("perimeter_state").forGetter((config) -> config.perimeterState),
                    BlockState.CODEC.fieldOf("ring_state").forGetter((config) -> config.ringState)
            ).apply(builder, PlatformFungusConfiguration::new)
    );

    public final BlockState stemState;
    public final BlockState hatState;
    public final BlockState grassState;
    public final BlockState sproutState;
    public final BlockState perimeterState;
    public final BlockState ringState;

    public PlatformFungusConfiguration(BlockState stem, BlockState hat, BlockState grass, BlockState sprout, BlockState perimeter, BlockState ring) {
        this.stemState = stem;
        this.hatState = hat;
        this.grassState = grass;
        this.sproutState = sprout;
        this.perimeterState = perimeter;
        this.ringState = ring;
    }

    public static PlatformFungusConfiguration defaultConfig() {
        return new PlatformFungusConfiguration(
                Registry.BlockRegistry.MAGMA_FUNGUS_STEM_BLOCK.get().defaultBlockState(),
                Registry.BlockRegistry.MAGMA_FUNGUS_CAP_BLOCK.get().defaultBlockState(),
                Blocks.GRASS.defaultBlockState(),
                Blocks.NETHER_SPROUTS.defaultBlockState(),
                Registry.BlockRegistry.MAGMA_FUNGUS_EDGE_BLOCK.get().defaultBlockState(),
                Registry.BlockRegistry.MAGMA_FUNGUS_BLOCK.get().defaultBlockState()
        );
    }
}
