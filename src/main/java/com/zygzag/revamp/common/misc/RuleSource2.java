package com.zygzag.revamp.common.misc;


import com.zygzag.revamp.common.registry.RuleSourceRegistry;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraftforge.registries.RegistryObject;

public record RuleSource2(RegistryObject<Block> obj) implements SurfaceRules.RuleSource {

    @Override
    public KeyDispatchDataCodec<? extends SurfaceRules.RuleSource> codec() {
        return new KeyDispatchDataCodec<>(RuleSourceRegistry.MAGMA_MYCELIUM_RULE_SOURCE.get());
    }

    @Override
    public SurfaceRules.SurfaceRule apply(SurfaceRules.Context context) {
        return (a, b, c) -> obj.get().defaultBlockState(); // honestly no clue what the lambda parameters are supposed to be
    }
}