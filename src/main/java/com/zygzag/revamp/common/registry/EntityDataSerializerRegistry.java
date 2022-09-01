package com.zygzag.revamp.common.registry;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.zygzag.revamp.common.Revamp.MODID;

public class EntityDataSerializerRegistry extends Registry<EntityDataSerializer<?>> {
    public static final EntityDataSerializerRegistry INSTANCE = new EntityDataSerializerRegistry(DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, MODID), MODID);
    private static final DeferredRegister<EntityDataSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, MODID);

    public EntityDataSerializerRegistry(DeferredRegister<EntityDataSerializer<?>> register, String modid) {
        super(register, modid);
    }

    public static <T> RegistryObject<EntityDataSerializer<T>> registerEntityDataSerializer(String id, Supplier<EntityDataSerializer<T>> supplier) {
        return REGISTER.register(id, supplier);
    }
}
