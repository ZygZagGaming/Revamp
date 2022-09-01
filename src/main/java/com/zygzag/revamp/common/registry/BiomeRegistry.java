package com.zygzag.revamp.common.registry;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.zygzag.revamp.common.Revamp.MODID;

public class BiomeRegistry extends Registry<Biome> {
    public static final BiomeRegistry INSTANCE = new BiomeRegistry(DeferredRegister.create(ForgeRegistries.BIOMES, MODID), MODID);
    public static final RegistryObject<Biome> LAVA_GARDENS = INSTANCE.register("lava_gardens", () -> new Biome.BiomeBuilder()
            .precipitation(Biome.Precipitation.NONE)
            .temperature(1f)
            .temperatureAdjustment(Biome.TemperatureModifier.NONE)
            .downfall(0f)
            .specialEffects(
                    new BiomeSpecialEffects.Builder()
                            .fogColor(0xa86032)
                            .waterColor(9122495)
                            .waterFogColor(0xa86032)
                            .skyColor(13949762)
                            .build()
            )
            .mobSpawnSettings(
                    new MobSpawnSettings.Builder()
                            .addSpawn(MobCategory.MONSTER,
                                    new MobSpawnSettings.SpawnerData(EntityRegistry.REVAMPED_BLAZE.get(), 1, 1, 1)
                            )
                            .build()
            )
            .generationSettings(
                    new BiomeGenerationSettings.Builder()
                            .addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, PlacedFeatureRegistry.PLATFORM_FUNGUS_PLACED.getHolder().get())
                            .build()
            )
            .build()
    );

    public BiomeRegistry(DeferredRegister<Biome> register, String modid) {
        super(register, modid);
    }
}
