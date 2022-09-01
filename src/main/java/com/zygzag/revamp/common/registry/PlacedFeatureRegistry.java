package com.zygzag.revamp.common.registry;

import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

import static com.zygzag.revamp.common.Revamp.MODID;

public class PlacedFeatureRegistry extends Registry<PlacedFeature> {
    public static final PlacedFeatureRegistry INSTANCE = new PlacedFeatureRegistry(DeferredRegister.create(net.minecraft.core.Registry.PLACED_FEATURE_REGISTRY, MODID), MODID);
    public static final RegistryObject<PlacedFeature> IRIDIUM_SMALL_PLACED = INSTANCE.register("ore_iridium_small", () -> new PlacedFeature(ConfiguredFeatureRegistry.IRIDIUM_SMALL_CONFIGURED.getHolder().get(), List.of(HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(10)), RarityFilter.onAverageOnceEvery(8))));
    public static final RegistryObject<PlacedFeature> IRIDIUM_LARGE_PLACED = INSTANCE.register("ore_iridium_large", () -> new PlacedFeature(ConfiguredFeatureRegistry.IRIDIUM_LARGE_CONFIGURED.getHolder().get(), List.of(HeightRangePlacement.triangle(VerticalAnchor.absolute(-48), VerticalAnchor.absolute(-16)), RarityFilter.onAverageOnceEvery(20))));
    public static final RegistryObject<PlacedFeature> IRIDIUM_BURIED_PLACED = INSTANCE.register("ore_iridium_buried", () -> new PlacedFeature(ConfiguredFeatureRegistry.IRIDIUM_BURIED_CONFIGURED.getHolder().get(), List.of(HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(10)), RarityFilter.onAverageOnceEvery(2))));

    public static final RegistryObject<PlacedFeature> PLATFORM_FUNGUS_PLACED = INSTANCE.register("platform_fungus", () -> new PlacedFeature(ConfiguredFeatureRegistry.PLATFORM_FUNGUS_CONFIGURED.getHolder().get(), List.of(CountOnEveryLayerPlacement.of(12), BiomeFilter.biome())));

    public PlacedFeatureRegistry(DeferredRegister<PlacedFeature> register, String modid) {
        super(register, modid);
    }
}
