package com.zygzag.revamp.common.mixin;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(DimensionType.class)
public class DimensionTypeMixin {
   private static MultiNoiseBiomeSource.Preset NETHER = new MultiNoiseBiomeSource.Preset(
            new ResourceLocation("nether"),
            (registry) ->
                    new Climate.ParameterList<>(
                            ImmutableList.of(
                                    Pair.of(
                                            Climate.parameters(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                                            registry.getOrCreateHolder(Biomes.NETHER_WASTES)
                                    ), Pair.of(
                                            Climate.parameters(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                                            registry.getOrCreateHolder(Biomes.SOUL_SAND_VALLEY)
                                    ), Pair.of(
                                            Climate.parameters(0.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F),
                                            registry.getOrCreateHolder(Biomes.CRIMSON_FOREST)
                                    ), Pair.of(
                                            Climate.parameters(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.375F),
                                            registry.getOrCreateHolder(Biomes.WARPED_FOREST)
                                    ), Pair.of(
                                            Climate.parameters(-0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.175F),
                                            registry.getOrCreateHolder(Biomes.BASALT_DELTAS)
                                    ), Pair.of(
                                            Climate.parameters(-0.5F, 0.0F, 0.3F, 0.0F, 0.0F, 0.0F, 0.175F),
                                            registry.getOrCreateHolder(com.zygzag.revamp.common.registry.Registry.BiomeRegistry.LAVA_GARDENS.getKey())
                                    )
                            )
                    )
    );

    @Inject(at = @At("HEAD"), cancellable = true, method = "defaultDimensions(Lnet/minecraft/core/RegistryAccess;JZ)Lnet/minecraft/core/Registry;")
    private static void defaultDimensionsMixin(RegistryAccess access, long l, boolean b, CallbackInfoReturnable<Registry<LevelStem>> callback) {
        WritableRegistry<LevelStem> writableregistry = new MappedRegistry<>(Registry.LEVEL_STEM_REGISTRY, Lifecycle.experimental(), (Function<LevelStem, Holder.Reference<LevelStem>>)null);
        Registry<DimensionType> registry = access.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
        Registry<Biome> registry1 = access.registryOrThrow(Registry.BIOME_REGISTRY);
        Registry<StructureSet> registry2 = access.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
        Registry<NoiseGeneratorSettings> registry3 = access.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
        Registry<NormalNoise.NoiseParameters> registry4 = access.registryOrThrow(Registry.NOISE_REGISTRY);
        writableregistry.register(
                LevelStem.NETHER,
                new LevelStem(registry.getOrCreateHolder(DimensionType.NETHER_LOCATION),
                    new NoiseBasedChunkGenerator(
                            registry2,
                            registry4,
                            NETHER.biomeSource(registry1),
                            l,
                            registry3.getOrCreateHolder(NoiseGeneratorSettings.NETHER)
                    )
                ),
                Lifecycle.stable()
        );
        writableregistry.register(LevelStem.END, new LevelStem(registry.getOrCreateHolder(DimensionType.END_LOCATION), new NoiseBasedChunkGenerator(registry2, registry4, new TheEndBiomeSource(registry1, l), l, registry3.getOrCreateHolder(NoiseGeneratorSettings.END))), Lifecycle.stable());
        callback.setReturnValue(writableregistry);
        callback.cancel();
    }
}
