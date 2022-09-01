package com.zygzag.revamp.common.registry;

import com.zygzag.revamp.common.item.enchant.*;
import com.zygzag.revamp.util.Constants;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.zygzag.revamp.common.Revamp.MODID;

public class EnchantmentRegistry extends Registry<Enchantment> {
    public static final EnchantmentRegistry INSTANCE = new EnchantmentRegistry(DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID), MODID);
    public static final RegistryObject<Enchantment> COOLDOWN_ENCHANTMENT = INSTANCE.register("cooldown", () -> new CooldownEnchantment(Enchantment.Rarity.RARE));
    public static final RegistryObject<Enchantment> SURGE_PROTECTOR_ENCHANTMENT = INSTANCE.register("surge_protector", SurgeProtectorEnchantment::new);
    public static final RegistryObject<VoltageEnchantment> VOLTAGE_PLUS_ENCHANTMENT = INSTANCE.register("voltage_plus", () -> new VoltageEnchantment(Constants.VOLTAGE_ENCHANTMENT_CHARGE_PER_TICK));
    public static final RegistryObject<VoltageEnchantment> VOLTAGE_MINUS_ENCHANTMENT = INSTANCE.register("voltage_minus", () -> new VoltageEnchantment(-Constants.VOLTAGE_ENCHANTMENT_CHARGE_PER_TICK));
    public static final RegistryObject<DynamoEnchantment> DYNAMO_ENCHANTMENT = INSTANCE.register("dynamo", DynamoEnchantment::new);
    public static final RegistryObject<GroundedEnchantment> GROUNDED_ENCHANTMENT = INSTANCE.register("grounded", GroundedEnchantment::new);

    private EnchantmentRegistry(DeferredRegister<Enchantment> register, String modid) {
        super(register, modid);
    }
}
