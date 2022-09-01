package com.zygzag.revamp.common.registry;

import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.zygzag.revamp.common.Revamp.MODID;

public class MenuTypeRegistry extends Registry<MenuType<?>> {
    public static final MenuTypeRegistry INSTANCE = new MenuTypeRegistry(DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID), MODID);

    public MenuTypeRegistry(DeferredRegister<MenuType<?>> register, String modid) {
        super(register, modid);
    }
}
