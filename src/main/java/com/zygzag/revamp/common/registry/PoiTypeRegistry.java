package com.zygzag.revamp.common.registry;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.function.Supplier;

import static com.zygzag.revamp.common.Revamp.MODID;

public class PoiTypeRegistry extends Registry<PoiType> {
    public static final com.zygzag.revamp.common.registry.PoiTypeRegistry INSTANCE = new com.zygzag.revamp.common.registry.PoiTypeRegistry(DeferredRegister.create(net.minecraft.core.Registry.POINT_OF_INTEREST_TYPE_REGISTRY, MODID), MODID);


    public PoiTypeRegistry(DeferredRegister<PoiType> register, String modid) {
        super(register, modid);
    }

    private static Set<BlockState> getBlockStates(Supplier<Block> block) {
        return ImmutableSet.copyOf(block.get().getStateDefinition().getPossibleStates());
    }
}
