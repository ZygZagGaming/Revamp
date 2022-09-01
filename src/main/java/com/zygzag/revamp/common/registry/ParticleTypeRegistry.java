package com.zygzag.revamp.common.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.zygzag.revamp.common.Revamp.MODID;

public class ParticleTypeRegistry extends Registry<ParticleType<?>> {
    public static final ParticleTypeRegistry INSTANCE = new ParticleTypeRegistry(DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID), MODID);
    public static RegistryObject<SimpleParticleType> CHARGE_PARTICLE_TYPE_POSITIVE = INSTANCE.register("charge_positive", () -> new SimpleParticleType(false));
    public static RegistryObject<SimpleParticleType> CHARGE_PARTICLE_TYPE_NEGATIVE = INSTANCE.register("charge_negative", () -> new SimpleParticleType(false));

    public ParticleTypeRegistry(DeferredRegister<ParticleType<?>> register, String modid) {
        super(register, modid);
    }
}
