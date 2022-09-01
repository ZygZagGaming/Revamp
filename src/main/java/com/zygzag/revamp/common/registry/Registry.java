package com.zygzag.revamp.common.registry;

import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Registry<T> {
    private final DeferredRegister<T> register;
    private final String modid;

    public Registry(DeferredRegister<T> register, String modid) {
        this.register = register;
        this.modid = modid;
    }

    public <P extends T> RegistryObject<P> register(String id, Supplier<P> supplier) {
        return register.register(id, supplier);
    }

    public void registerTo(IEventBus bus) {
        register.register(bus);
    }

    private static final Lazy<List<Registry<?>>> REGISTRIES = Lazy.of(() -> List.of(
            EntityRegistry.INSTANCE,
            ItemRegistry.INSTANCE,
            IridiumGearRegistry.INSTANCE,
            BlockRegistry.INSTANCE,
            RecipeSerializerRegistry.INSTANCE,
            EnchantmentRegistry.INSTANCE,
            PotionRegistry.INSTANCE,
            MobEffectRegistry.INSTANCE,
            BlockEntityTypeRegistry.INSTANCE,
            MenuTypeRegistry.INSTANCE,
            FeatureRegistry.INSTANCE,
            ConfiguredFeatureRegistry.INSTANCE,
            PlacedFeatureRegistry.INSTANCE,
            StructureRegistry.INSTANCE,
            BiomeRegistry.INSTANCE,
            BiomeSourceRegistry.INSTANCE,
            RuleSourceRegistry.INSTANCE,
            EntityDataSerializerRegistry.INSTANCE,
            ParticleTypeRegistry.INSTANCE,
            RecipeTypeRegistry.INSTANCE,
            StructureTypeRegistry.INSTANCE,
            PoiTypeRegistry.INSTANCE,
            GlobalLootModifierSerializerRegistry.INSTANCE
    ));

    public static void register(IEventBus bus) {
        for (Registry<?> registry : REGISTRIES.get()) {
            registry.registerTo(bus);
        }
    }
}
