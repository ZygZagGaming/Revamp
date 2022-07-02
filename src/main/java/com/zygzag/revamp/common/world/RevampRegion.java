package com.zygzag.revamp.common.world;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ParameterUtils;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class RevampRegion extends Region {
    public RevampRegion(ResourceLocation name, int weight) {
        super(name, RegionType.NETHER, weight);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        addBiome(
                mapper,
                ParameterUtils.Temperature.HOT,
                ParameterUtils.Humidity.ARID,
                ParameterUtils.Continentalness.FULL_RANGE,
                ParameterUtils.Erosion.EROSION_5,
                ParameterUtils.Weirdness.FULL_RANGE,
                ParameterUtils.Depth.FULL_RANGE,
                0,
                com.zygzag.revamp.common.registry.Registry.BiomeRegistry.LAVA_GARDENS.getKey()
        );
    }
}
