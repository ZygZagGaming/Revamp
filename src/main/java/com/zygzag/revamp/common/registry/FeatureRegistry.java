package com.zygzag.revamp.common.registry;

import com.zygzag.revamp.common.world.PlatformFungusConfiguration;
import com.zygzag.revamp.common.world.feature.PlatformFungusFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.zygzag.revamp.common.Revamp.MODID;

public class FeatureRegistry extends Registry<Feature<?>> {
    public static final FeatureRegistry INSTANCE = new FeatureRegistry(DeferredRegister.create(ForgeRegistries.FEATURES, MODID), MODID);
    public static final RegistryObject<Feature<PlatformFungusConfiguration>> PLATFORM_FUNGUS_FEATURE = INSTANCE.register("platform_fungus", () -> new PlatformFungusFeature(PlatformFungusConfiguration.CODEC));

    private FeatureRegistry(DeferredRegister<Feature<?>> register, String modid) {
        super(register, modid);
    }
}
