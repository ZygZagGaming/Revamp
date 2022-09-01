package com.zygzag.revamp.common.registry;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.zygzag.revamp.common.Revamp.MODID;

public class BiomeSourceRegistry extends Registry<Codec<? extends BiomeSource>> {
    public static final BiomeSourceRegistry INSTANCE = new BiomeSourceRegistry(DeferredRegister.create(net.minecraft.core.Registry.BIOME_SOURCE_REGISTRY, MODID), MODID);

    public BiomeSourceRegistry(DeferredRegister<Codec<? extends BiomeSource>> register, String modid) {
        super(register, modid);
    }
}
