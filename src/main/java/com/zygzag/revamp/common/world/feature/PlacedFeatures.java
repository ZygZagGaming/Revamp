package com.zygzag.revamp.common.world.feature;

import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.common.util.Lazy;

public class PlacedFeatures {
    public static final Lazy<PlacedFeature> IRIDIUM_SMALL = Lazy.of(() -> Features.IRIDIUM_SMALL.get().placed(HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(10)), RarityFilter.onAverageOnceEvery(2)));
    public static final Lazy<PlacedFeature> IRIDIUM_LARGE = Lazy.of(() -> Features.IRIDIUM_LARGE.get().placed(HeightRangePlacement.triangle(VerticalAnchor.absolute(-48), VerticalAnchor.absolute(-16)), RarityFilter.onAverageOnceEvery(6)));
    public static final Lazy<PlacedFeature> IRIDIUM_BURIED = Lazy.of(() -> Features.IRIDIUM_BURIED.get().placed(HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(10)), CountPlacement.of(2)));
}
