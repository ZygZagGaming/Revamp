package com.zygzag.revamp.common.registry;

import com.mojang.serialization.Codec;
import com.zygzag.revamp.common.loot.AutosmeltModifier;
import com.zygzag.revamp.common.loot.ExecutionerModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.zygzag.revamp.common.Revamp.MODID;

public class GlobalLootModifierSerializerRegistry extends Registry<Codec<? extends IGlobalLootModifier>> {
    public static final GlobalLootModifierSerializerRegistry INSTANCE = new GlobalLootModifierSerializerRegistry(DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID), MODID);

    public static RegistryObject<Codec<ExecutionerModifier>> EXECUTIONER = INSTANCE.register("executioner", () -> ExecutionerModifier.CODEC);
    public static RegistryObject<Codec<AutosmeltModifier>> AUTOSMELT = INSTANCE.register("autosmelt", () -> AutosmeltModifier.CODEC);

    public GlobalLootModifierSerializerRegistry(DeferredRegister<Codec<? extends IGlobalLootModifier>> register, String modid) {
        super(register, modid);
    }
}
