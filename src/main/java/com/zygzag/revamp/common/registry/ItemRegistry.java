package com.zygzag.revamp.common.registry;

import com.zygzag.revamp.common.item.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.zygzag.revamp.common.Revamp.MAIN_TAB;
import static com.zygzag.revamp.common.Revamp.MODID;

public class ItemRegistry extends Registry<Item> {
    public static final ItemRegistry INSTANCE = new ItemRegistry(DeferredRegister.create(ForgeRegistries.ITEMS, MODID), MODID);
    public static final RegistryObject<Item> LAVA_VINES_ITEM = registerBlockItem(BlockRegistry.LAVA_VINES_BLOCK);

    public static final RegistryObject<Item> ENDER_BOOK_ITEM = INSTANCE.register("ender_book", () -> new EnderBookItem(new Item.Properties().tab(MAIN_TAB).stacksTo(1)));

    public static final RegistryObject<Item> REVAMPED_BLAZE_SPAWN_EGG = INSTANCE.register("revamped_blaze_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.REVAMPED_BLAZE, 16167425, 16775294, (new Item.Properties()).tab(CreativeModeTab.TAB_MISC)));

    public static final RegistryObject<Item> OSTEUM_ITEM = registerBlockItem(BlockRegistry.OSTEUM, new Item.Properties().tab(MAIN_TAB));
    public static final RegistryObject<Item> EMPOWERMENT_STAR_ITEM = INSTANCE.register("empowerment_star", () -> new EmpowermentStar(new Item.Properties().tab(MAIN_TAB)));
    public static final RegistryObject<Item> IRIDIUM_PLATING = basicItem("iridium_plating");
    public static final RegistryObject<Item> RAW_IRIDIUM = basicItem("raw_iridium");
    public static final RegistryObject<Item> RAW_IRIDIUM_ROD = basicItem("raw_iridium_rod");
    public static final RegistryObject<Item> FORGED_IRIDIUM_ROD = basicItem("forged_iridium_rod");
    public static final RegistryObject<Item> IRIDIUM_RING_ITEM = basicItem("iridium_ring");
    public static final RegistryObject<Item> SHULKER_BOWL_ITEM = INSTANCE.register("shulker_bowl", ShulkerBowlItem::new);
    public static final RegistryObject<Item> GOLDEN_STEW_ITEM = INSTANCE.register("golden_stew", () -> new BowlFoodItem(
            new Item.Properties().stacksTo(1).craftRemainder(Items.BOWL).tab(CreativeModeTab.TAB_FOOD).rarity(Rarity.RARE).food(
                    new FoodProperties.Builder().alwaysEat().nutrition(6).saturationMod(0.6f)
                            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 600, 2), 1f)
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 200, 1), 1f)
                            .build())));

    public static final RegistryObject<Item> ENCHANTED_GOLDEN_STEW_ITEM = INSTANCE.register("enchanted_golden_stew", () -> new EnchantedBowlFoodItem(
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).craftRemainder(Items.BOWL).tab(CreativeModeTab.TAB_FOOD).food(
                    new FoodProperties.Builder().alwaysEat().nutrition(12).saturationMod(1.6f)
                            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 1200, 4), 1f)
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1), 1f)
                            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200), 1f)
                            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1), 1f)
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 600, 2), 1f)
                            .build())));

    public static final RegistryObject<Item> TRANSMUTATION_CHARGE = INSTANCE.register("transmutation_charge", () -> new TransmutationCharge(new Item.Properties().tab(MAIN_TAB)));

    public static final RegistryObject<Item> ROT_ESSENCE = basicEssenceItem("rot_essence");
    public static final RegistryObject<Item> BONE_ESSENCE = basicEssenceItem("bone_essence");
    public static final RegistryObject<Item> SAND_ESSENCE = basicEssenceItem("sand_essence");
    public static final RegistryObject<Item> WATER_ESSENCE = basicEssenceItem("water_essence");
    public static final RegistryObject<Item> ICE_ESSENCE = basicEssenceItem("ice_essence");
    public static final RegistryObject<Item> NIGHTMARE_ESSENCE = basicEssenceItem("nightmare_essence");
    public static final RegistryObject<Item> GREED_ESSENCE = basicEssenceItem("greed_essence");
    public static final RegistryObject<Item> POWER_ESSENCE = basicEssenceItem("power_essence");
    public static final RegistryObject<Item> DECAY_ESSENCE = basicEssenceItem("decay_essence");
    public static final RegistryObject<Item> ALLYSHIP_ESSENCE = basicEssenceItem("allyship_essence");
    public static final RegistryObject<Item> FERAL_ESSENCE = basicEssenceItem("feral_essence");
    public static final RegistryObject<Item> FIRE_ESSENCE = basicEssenceItem("fire_essence");

    public static final RegistryObject<Item> MAGMA_FUNGUS_ITEM = registerBlockItem(BlockRegistry.MAGMA_FUNGUS_BLOCK);
    public static final RegistryObject<Item> MAGMA_FUNGUS_CAP_ITEM = registerBlockItem(BlockRegistry.MAGMA_FUNGUS_CAP_BLOCK);
    public static final RegistryObject<Item> MAGMA_FUNGUS_EDGE_ITEM = registerBlockItem(BlockRegistry.MAGMA_FUNGUS_EDGE_BLOCK);
    public static final RegistryObject<Item> MAGMA_STEM_ITEM = registerBlockItem(BlockRegistry.MAGMA_STEM_BLOCK);
    public static final RegistryObject<Item> STRIPPED_MAGMA_STEM_ITEM = registerBlockItem(BlockRegistry.STRIPPED_MAGMA_STEM_BLOCK);
    public static final RegistryObject<Item> MAGMA_HYPHAE_ITEM = registerBlockItem(BlockRegistry.MAGMA_HYPHAE_BLOCK);
    public static final RegistryObject<Item> STRIPPED_MAGMA_HYPHAE_ITEM = registerBlockItem(BlockRegistry.STRIPPED_MAGMA_HYPHAE_BLOCK);

    public static final RegistryObject<Item> MAGMA_PLANKS_ITEM = registerBlockItem(BlockRegistry.MAGMA_PLANKS);
    public static final RegistryObject<Item> MAGMA_SLAB_ITEM = registerBlockItem(BlockRegistry.MAGMA_SLAB);
    public static final RegistryObject<Item> MAGMA_PRESSURE_PLATE_ITEM = registerBlockItem(BlockRegistry.MAGMA_PRESSURE_PLATE);
    public static final RegistryObject<Item> MAGMA_FENCE_ITEM = registerBlockItem(BlockRegistry.MAGMA_FENCE);
    public static final RegistryObject<Item> MAGMA_TRAPDOOR_ITEM = registerBlockItem(BlockRegistry.MAGMA_TRAPDOOR);
    public static final RegistryObject<Item> MAGMA_FENCE_GATE_ITEM = registerBlockItem(BlockRegistry.MAGMA_FENCE_GATE);
    public static final RegistryObject<Item> MAGMA_STAIRS_ITEM = registerBlockItem(BlockRegistry.MAGMA_STAIRS);
    public static final RegistryObject<Item> MAGMA_BUTTON_ITEM = registerBlockItem(BlockRegistry.MAGMA_BUTTON);
    public static final RegistryObject<Item> MAGMA_DOOR_ITEM = registerBlockItem(BlockRegistry.MAGMA_DOOR);
    public static final RegistryObject<Item> MAGMA_SIGN_ITEM = INSTANCE.register("magma_sign", () -> new SignItem((new Item.Properties()).stacksTo(16).tab(MAIN_TAB), BlockRegistry.MAGMA_SIGN.get(), BlockRegistry.MAGMA_WALL_SIGN.get()));

    public static final RegistryObject<Item> MAGMA_PUSTULE_ITEM = registerBlockItem(BlockRegistry.MAGMA_PUSTULE_BLOCK);

    public static final RegistryObject<Item> IRIDIUM_ORE_ITEM = registerBlockItem(BlockRegistry.IRIDIUM_ORE, new Item.Properties().tab(MAIN_TAB));
    public static final RegistryObject<Item> DEEPSLATE_IRIDIUM_ORE_ITEM = registerBlockItem(BlockRegistry.DEEPSLATE_IRIDIUM_ORE, new Item.Properties().tab(MAIN_TAB));
    public static final RegistryObject<Item> RAW_IRIDIUM_BLOCK_ITEM = registerBlockItem(BlockRegistry.RAW_IRIDIUM_BLOCK, new Item.Properties().tab(MAIN_TAB));
    //public static final RegistryObject<Item> IRIDIUM_GRATING_ITEM = registerBlockItem(BlockRegistry.IRIDIUM_GRATING, new Item.Properties().tab(Revamp.MAIN_TAB));
    public static final RegistryObject<Item> BLESSED_SOIL_ITEM = registerBlockItem(BlockRegistry.BLESSED_SOIL, new Item.Properties().tab(MAIN_TAB));

    public static final RegistryObject<Item> MAGMA_MYCELIUM_ITEM = registerBlockItem(BlockRegistry.MAGMA_NYLIUM_BLOCK);

    public static final RegistryObject<Item> CHARGE_CRYSTAL_NEGATIVE_ITEM = registerBlockItem(BlockRegistry.CHARGE_CRYSTAL_BLOCK_NEGATIVE);
    public static final RegistryObject<Item> CHARGE_CRYSTAL_POSITIVE_ITEM = registerBlockItem(BlockRegistry.CHARGE_CRYSTAL_BLOCK_POSITIVE);
    public static final RegistryObject<Item> CHARGE_DETECTOR_ITEM = registerBlockItem(BlockRegistry.CHARGE_DETECTOR);
    public static final RegistryObject<Item> ARC_CRYSTAL_ITEM = registerBlockItem(BlockRegistry.ARC_CRYSTAL);
    public static final RegistryObject<Item> SHULKSTONE_BLOCK = registerBlockItem(BlockRegistry.SHULKSTONE_BLOCK);
    public static final RegistryObject<Item> SHULKSTONE_CANNON = registerBlockItem(BlockRegistry.SHULKSTONE_CANNON);

    public ItemRegistry(DeferredRegister<Item> register, String modid) {
        super(register, modid);
    }

    private static RegistryObject<Item> basicItem(String id) {
        return INSTANCE.register(id, () -> new Item(new Item.Properties().tab(MAIN_TAB)));
    }

    private static RegistryObject<Item> basicFireImmuneItem(String id) {
        return INSTANCE.register(id, () -> new Item(new Item.Properties().tab(MAIN_TAB).fireResistant()));
    }

    private static RegistryObject<Item> basicEssenceItem(String id) {
        return INSTANCE.register(id, () -> new Item(new Item.Properties().fireResistant()));
    }

    public static RegistryObject<Item> registerBlockItem(RegistryObject<Block> block, Item.Properties properties) {
        return INSTANCE.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
    }

    private static RegistryObject<Item> registerBlockItem(RegistryObject<Block> block) {
        return registerBlockItem(block, new Item.Properties().tab(MAIN_TAB));
    }
}
