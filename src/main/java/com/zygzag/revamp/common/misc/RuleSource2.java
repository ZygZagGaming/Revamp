package com.zygzag.revamp.common.misc;


import com.mojang.serialization.Codec;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.registries.RegistryObject;

public record RuleSource2(RegistryObject<Block> obj) implements SurfaceRules.RuleSource {

    @Override
    public Codec<? extends SurfaceRules.RuleSource> codec() {
        return Registry.RuleSourceRegistry.MAGMA_MYCELIUM_RULE_SOURCE.get();
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        return (a, b, c) -> obj.get().defaultBlockState(); // honestly no clue what the lambda parameters are supposed to be
    }
}