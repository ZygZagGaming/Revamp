package com.zygzag.revamp.common.block;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.FungusBlock;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;

import java.util.function.Supplier;

public class PlatformShroomSapling extends FungusBlock {
    public PlatformShroomSapling(Properties properties, Supplier<Holder<ConfiguredFeature<HugeFungusConfiguration, ?>>> feature) {
        super(properties, feature);
    }
}
