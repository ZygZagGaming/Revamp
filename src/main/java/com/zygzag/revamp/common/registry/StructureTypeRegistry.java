package com.zygzag.revamp.common.registry;

import com.zygzag.revamp.common.world.feature.BetterFortressFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.zygzag.revamp.common.Revamp.MODID;

public class StructureTypeRegistry extends Registry<StructureType<?>> {
    public static final StructureTypeRegistry INSTANCE = new StructureTypeRegistry(DeferredRegister.create(net.minecraft.core.Registry.STRUCTURE_TYPE_REGISTRY, MODID), MODID);

    public static final RegistryObject<StructureType<BetterFortressFeature>> BETTER_FORTRESS = INSTANCE.register("better_fortress", () -> () -> BetterFortressFeature.CODEC);

    public StructureTypeRegistry(DeferredRegister<StructureType<?>> register, String modid) {
        super(register, modid);
    }
}
