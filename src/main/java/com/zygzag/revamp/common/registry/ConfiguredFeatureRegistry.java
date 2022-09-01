package com.zygzag.revamp.common.registry;

import com.zygzag.revamp.common.world.PlatformFungusConfiguration;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

import static com.zygzag.revamp.common.Revamp.MODID;

public class ConfiguredFeatureRegistry extends Registry<ConfiguredFeature<?, ?>> {
    public static final ConfiguredFeatureRegistry INSTANCE = new ConfiguredFeatureRegistry(DeferredRegister.create(net.minecraft.core.Registry.CONFIGURED_FEATURE_REGISTRY, MODID), MODID);
    public static final Lazy<List<OreConfiguration.TargetBlockState>> ORE_IRIDIUM_TARGET_LIST = Lazy.of(() -> List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, BlockRegistry.IRIDIUM_ORE.get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, BlockRegistry.DEEPSLATE_IRIDIUM_ORE.get().defaultBlockState())));

    public static final RegistryObject<ConfiguredFeature<?, ?>> IRIDIUM_SMALL_CONFIGURED = INSTANCE.register("ore_iridium_small", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ORE_IRIDIUM_TARGET_LIST.get(), 16, 0.4f)));
    public static final RegistryObject<ConfiguredFeature<?, ?>> IRIDIUM_LARGE_CONFIGURED = INSTANCE.register("ore_iridium_large", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ORE_IRIDIUM_TARGET_LIST.get(), 24, 0.6f)));
    public static final RegistryObject<ConfiguredFeature<?, ?>> IRIDIUM_BURIED_CONFIGURED = INSTANCE.register("ore_iridium_buried", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ORE_IRIDIUM_TARGET_LIST.get(), 20, 1f)));

    public static final RegistryObject<ConfiguredFeature<?, ?>> PLATFORM_FUNGUS_CONFIGURED = INSTANCE.register("platform_fungus", () -> new ConfiguredFeature<>(FeatureRegistry.PLATFORM_FUNGUS_FEATURE.get(), PlatformFungusConfiguration.defaultConfig()));

    public ConfiguredFeatureRegistry(DeferredRegister<ConfiguredFeature<?, ?>> register, String modid) {
        super(register, modid);
    }
}
