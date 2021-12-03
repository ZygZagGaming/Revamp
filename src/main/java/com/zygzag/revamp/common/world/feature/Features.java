package com.zygzag.revamp.common.world.feature;

import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraftforge.common.util.Lazy;

import java.util.List;

@SuppressWarnings("SameParameterValue")
public class Features {
    public static final Lazy<List<OreConfiguration.TargetBlockState>> ORE_IRIDIUM_TARGET_LIST = Lazy.of(() -> List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, Registry.IRIDIUM_ORE.get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, Registry.DEEPSLATE_IRIDIUM_ORE.get().defaultBlockState())));

    public static final Lazy<ConfiguredFeature<?, ?>> IRIDIUM_SMALL = Lazy.of(() -> ore(ORE_IRIDIUM_TARGET_LIST.get(), 16, 0.4f));
    public static final Lazy<ConfiguredFeature<?, ?>> IRIDIUM_LARGE = Lazy.of(() -> ore(ORE_IRIDIUM_TARGET_LIST.get(), 24, 0.6f));
    public static final Lazy<ConfiguredFeature<?, ?>> IRIDIUM_BURIED = Lazy.of(() -> ore(ORE_IRIDIUM_TARGET_LIST.get(), 20, 1f));

    private static ConfiguredFeature<OreConfiguration, ?> ore(List<OreConfiguration.TargetBlockState> test, int size) {
        return Feature.ORE.configured(new OreConfiguration(test, size));
    }

    private static ConfiguredFeature<OreConfiguration, ?> ore(List<OreConfiguration.TargetBlockState> test, int size, float chanceToDisappearIfExposed) {
        return Feature.ORE.configured(new OreConfiguration(test, size, chanceToDisappearIfExposed));
    }
}
