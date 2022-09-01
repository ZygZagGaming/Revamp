package com.zygzag.revamp.common.registry;

import com.zygzag.revamp.common.world.feature.BetterFortressFeature;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.zygzag.revamp.common.Revamp.MODID;

public class StructureRegistry extends Registry<Structure> {
    public static final StructureRegistry INSTANCE = new StructureRegistry(DeferredRegister.create(net.minecraft.core.Registry.STRUCTURE_REGISTRY, MODID), MODID);
    public static final Supplier<WeightedRandomList<MobSpawnSettings.SpawnerData>> BETTER_FORTRESS_ENEMIES = () -> WeightedRandomList.create(new MobSpawnSettings.SpawnerData(EntityRegistry.REVAMPED_BLAZE.get(), 5, 2, 3), new MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4), new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 8, 5, 5), new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 2, 5, 5), new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 3, 4, 4));

    public static final Supplier<HolderSet<Biome>> BETTER_FORTRESS_BIOMES = Lazy.of(() -> HolderSet.direct(Stream.of(
            Biomes.CRIMSON_FOREST,
            Biomes.WARPED_FOREST,
            Biomes.SOUL_SAND_VALLEY,
            Biomes.BASALT_DELTAS,
            Biomes.NETHER_WASTES,
            BiomeRegistry.LAVA_GARDENS.getKey()
    ).map(ForgeRegistries.BIOMES::getHolder).filter(Optional::isPresent).map(Optional::get).toList())); // probably worst way to do this; TODO: fix later

    public static final RegistryObject<BetterFortressFeature> BETTER_FORTRESS = INSTANCE.register("better_fortress", () -> new BetterFortressFeature(structure(BETTER_FORTRESS_BIOMES.get(), Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, BETTER_FORTRESS_ENEMIES.get())), GenerationStep.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.NONE)));

    public StructureRegistry(DeferredRegister<Structure> register, String modid) {
        super(register, modid);
    }

    private static Structure.StructureSettings structure(TagKey<Biome> tag, Map<MobCategory, StructureSpawnOverride> spawns, GenerationStep.Decoration deco, TerrainAdjustment adjustment) {
        return new Structure.StructureSettings(biomes(tag), spawns, deco, adjustment);
    }

    private static Structure.StructureSettings structure(HolderSet<Biome> biomes, Map<MobCategory, StructureSpawnOverride> spawns, GenerationStep.Decoration deco, TerrainAdjustment adjustment) {
        return new Structure.StructureSettings(biomes, spawns, deco, adjustment);
    }

    private static HolderSet<Biome> biomes(TagKey<Biome> tag) {
        return HolderSet.direct(ForgeRegistries.BIOMES.tags().getTag(tag).stream().map(Holder::direct).toList());
    }
}
