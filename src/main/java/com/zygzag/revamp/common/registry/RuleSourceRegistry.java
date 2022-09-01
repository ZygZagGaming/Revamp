package com.zygzag.revamp.common.registry;

import com.mojang.serialization.Codec;
import com.zygzag.revamp.common.misc.RuleSource2;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.zygzag.revamp.common.Revamp.MODID;

public class RuleSourceRegistry extends Registry<Codec<? extends SurfaceRules.RuleSource>> {
    public static final RuleSourceRegistry INSTANCE = new RuleSourceRegistry(DeferredRegister.create(net.minecraft.core.Registry.RULE_REGISTRY, MODID), MODID);
    public static final RegistryObject<Codec<SurfaceRules.RuleSource>> MAGMA_MYCELIUM_RULE_SOURCE = INSTANCE.register("magma_mycelium", () -> Codec.unit(new RuleSource2(BlockRegistry.MAGMA_NYLIUM_BLOCK)));

    public RuleSourceRegistry(DeferredRegister<Codec<? extends SurfaceRules.RuleSource>> register, String modid) {
        super(register, modid);
    }
}
