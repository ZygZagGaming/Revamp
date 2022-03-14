package com.zygzag.revamp.common.world.feature;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.common.util.Lazy;

import java.util.List;

public class PlacedFeatures {
    public static final Lazy<Holder<PlacedFeature>> IRIDIUM_SMALL = Lazy.of(() -> PlacementUtils.register("ore_iridium_small", Features.IRIDIUM_SMALL.get(), List.of(HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(10)), RarityFilter.onAverageOnceEvery(4))));
    public static final Lazy<Holder<PlacedFeature>> IRIDIUM_LARGE = Lazy.of(() -> PlacementUtils.register("ore_iridium_large", Features.IRIDIUM_LARGE.get(), HeightRangePlacement.triangle(VerticalAnchor.absolute(-48), VerticalAnchor.absolute(-16)), RarityFilter.onAverageOnceEvery(12)));
    public static final Lazy<Holder<PlacedFeature>> IRIDIUM_BURIED = Lazy.of(() -> PlacementUtils.register("ore_iridium_buried", Features.IRIDIUM_BURIED.get(), HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(10)), CountPlacement.of(1)));
}
