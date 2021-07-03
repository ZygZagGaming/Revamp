package com.zygzag.revamp;

import com.google.common.collect.Sets;
import com.zygzag.revamp.block.CustomCauldronBlock;
import com.zygzag.revamp.block.IridiumOreBlock;
import com.zygzag.revamp.block.tile.CustomCauldronTileEntity;
import com.zygzag.revamp.entity.CustomIronGolemEntity;
import com.zygzag.revamp.item.*;
import com.zygzag.revamp.recipe.EnrichmentRecipe;
import com.zygzag.revamp.tier.IridiumArmorMaterial;
import com.zygzag.revamp.tier.IridiumToolTier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.PortalInfo;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.BannerDuplicateRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.imageio.spi.RegisterableService;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Registry {
    public static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Revamp.MODID);
    public static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Revamp.MODID);
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Revamp.MODID);
    public static final DeferredRegister<Enchantment> ENCHANTMENT_REGISTER = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Revamp.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Revamp.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, Revamp.MODID);

    public static void register(IEventBus bus) {
        ITEM_REGISTER.register(bus);
        BLOCK_REGISTER.register(bus);
        RECIPE_REGISTER.register(bus);
        ENCHANTMENT_REGISTER.register(bus);
        TILE_ENTITY_REGISTER.register(bus);
        ENTITY_REGISTER.register(bus);
    }

    public static RegistryObject<Item> registerItem(String name, Supplier<Item> supplier) {
        return ITEM_REGISTER.register(name, supplier);
    }

    // Entities
    public static final RegistryObject<EntityType<CustomIronGolemEntity>> CUSTOM_IRON_GOLEM = ENTITY_REGISTER.register("custom_iron_golem",() -> EntityType.Builder.<CustomIronGolemEntity>of(CustomIronGolemEntity::new, EntityClassification.MISC).sized(1.4F, 2.7F).clientTrackingRange(10).build("custom_iron_golem"));

    // Blocks
    public static final RegistryObject<Block> IRIDIUM_ORE = BLOCK_REGISTER.register("iridium_ore", IridiumOreBlock::new);
    public static final RegistryObject<Block> RAW_IRIDIUM_BLOCK = BLOCK_REGISTER.register("raw_iridium_block", () -> new Block(AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE)));
    public static final RegistryObject<Block> CUSTOM_CAULDRON = BLOCK_REGISTER.register("custom_cauldron", CustomCauldronBlock::new);

    // Block Items
    public static final RegistryObject<Item> IRIDIUM_ORE_ITEM = ITEM_REGISTER.register("iridium_ore", () -> new BlockItem(IRIDIUM_ORE.get(), new Item.Properties().tab(Revamp.TAB)));
    public static final RegistryObject<Item> RAW_IRIDIUM_ITEM = ITEM_REGISTER.register("raw_iridium_block", () -> new BlockItem(RAW_IRIDIUM_BLOCK.get(), new Item.Properties().tab(Revamp.TAB)));
    public static final RegistryObject<Item> CUSTOM_CAULDRON_ITEM = ITEM_REGISTER.register("custom_cauldron", () -> new BlockItem(CUSTOM_CAULDRON.get(), new Item.Properties().tab(Revamp.TAB)));

    // Items
    public static final RegistryObject<Item> IRIDIUM_PLATING = ITEM_REGISTER.register("iridium_plating", () -> new Item(new Item.Properties().tab(Revamp.TAB)));
    public static final RegistryObject<Item> RAW_IRIDIUM = ITEM_REGISTER.register("raw_iridium", () -> new Item(new Item.Properties().tab(Revamp.TAB)));
    public static final RegistryObject<Item> DAMAGE_ENRICHMENT = ITEM_REGISTER.register("damage_enrichment", () -> new DamageEnrichmentItem(new Item.Properties().tab(Revamp.TAB)));
    public static final RegistryObject<Item> SHULKER_BOWL = ITEM_REGISTER.register("shulker_bowl", () -> new ShulkerBowlItem(new Item.Properties().tab(Revamp.TAB).durability(16)));
    public static final RegistryObject<Item> EMPTY_SHULKER_BOWL = ITEM_REGISTER.register("empty_shulker_bowl", () -> new Item(new Item.Properties().tab(Revamp.TAB)));

    // Tile Entities
    public static final RegistryObject<TileEntityType<CustomCauldronTileEntity>> CUSTOM_CAULDRON_TILE_ENTITY = TILE_ENTITY_REGISTER.register("custom_cauldron", () -> new TileEntityType<>(CustomCauldronTileEntity::new, Sets.newHashSet(CUSTOM_CAULDRON.get()), null));

    // Tool Items
    // public static final RegistryObject<Item> SHORTBOW = registerItem("shortbow", () -> new ShortbowItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(384)));
    // public static final RegistryObject<Item> LONGBOW = registerItem("longbow", () -> new LongbowItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(384)));

    public static final RegistryObject<Item> IRIDIUM_SWORD = registerItem("iridium_sword", () -> new SwordItem(IridiumToolTier.FULL, 3, -2.4F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));
    public static final RegistryObject<Item> IRIDIUM_SHOVEL = registerItem("iridium_shovel", () -> new ShovelItem(IridiumToolTier.FULL, 1.5F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS).fireResistant()));
    public static final RegistryObject<Item> IRIDIUM_PICKAXE = registerItem("iridium_pickaxe", () -> new PickaxeItem(IridiumToolTier.FULL, 1, -2.8F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS).fireResistant()));
    public static final RegistryObject<Item> IRIDIUM_AXE = registerItem("iridium_axe", () -> new AxeItem(IridiumToolTier.FULL, 5.0F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS).fireResistant()));
    public static final RegistryObject<Item> IRIDIUM_HOE = registerItem("iridium_hoe", () -> new HoeItem(IridiumToolTier.FULL, -1, 0.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS).fireResistant()));

    public static final RegistryObject<Item> IRIDIUM_HELMET = registerItem("iridium_helmet", () -> new ArmorItem(IridiumArmorMaterial.IRIDIUM, EquipmentSlotType.HEAD, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));
    public static final RegistryObject<Item> IRIDIUM_CHESTPLATE = registerItem("iridium_chestplate", () -> new ArmorItem(IridiumArmorMaterial.IRIDIUM, EquipmentSlotType.CHEST, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));
    public static final RegistryObject<Item> IRIDIUM_LEGGINGS = registerItem("iridium_leggings", () -> new ArmorItem(IridiumArmorMaterial.IRIDIUM, EquipmentSlotType.LEGS, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));
    public static final RegistryObject<Item> IRIDIUM_BOOTS = registerItem("iridium_boots", () -> new ArmorItem(IridiumArmorMaterial.IRIDIUM, EquipmentSlotType.FEET, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));

    // region Partial Iridium tools & armor
    // Here we go...

    public static final RegistryObject<Item> PARTIAL_IRIDIUM_SWORD_2_1 = registerItem("partial_iridium_sword_1", () -> new PartialIridiumSwordItem(IridiumToolTier.HALF, 3, -2.4F, (new Item.Properties()), 2, 1));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_PICKAXE_3_1 = registerItem("partial_iridium_pickaxe_1", () -> new PartialIridiumPickaxeItem(IridiumToolTier._3_1, 1, -2.8F, (new Item.Properties()), 3, 1));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_PICKAXE_3_2 = registerItem("partial_iridium_pickaxe_2", () -> new PartialIridiumPickaxeItem(IridiumToolTier._3_2, 1, -2.8F, (new Item.Properties()), 3, 2));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_AXE_4_1 = registerItem("partial_iridium_axe_1", () -> new PartialIridiumAxeItem(IridiumToolTier._3_1, 5.0F, -3.0F, (new Item.Properties()), 3, 1));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_AXE_4_2 = registerItem("partial_iridium_axe_2", () -> new PartialIridiumAxeItem(IridiumToolTier._3_2, 5.0F, -3.0F, (new Item.Properties()), 3, 2));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_HOE_2_1 = registerItem("partial_iridium_hoe_1", () -> new PartialIridiumHoeItem(IridiumToolTier.HALF, -1, 0.0F, (new Item.Properties()).fireResistant(), 2, 1));


    public static final RegistryObject<Item> PARTIAL_IRIDIUM_HELMET_5_1 = registerItem("partial_iridium_helmet_1", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._5_1, EquipmentSlotType.HEAD, (new Item.Properties()).fireResistant(), 5, 1));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_HELMET_5_2 = registerItem("partial_iridium_helmet_2", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._5_2, EquipmentSlotType.HEAD, (new Item.Properties()).fireResistant(), 5, 2));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_HELMET_5_3 = registerItem("partial_iridium_helmet_3", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._5_3, EquipmentSlotType.HEAD, (new Item.Properties()).fireResistant(), 5, 3));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_HELMET_5_4 = registerItem("partial_iridium_helmet_4", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._5_4, EquipmentSlotType.HEAD, (new Item.Properties()).fireResistant(), 5, 4));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_CHESTPLATE_8_1 = registerItem("partial_iridium_chestplate_1", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._8_1, EquipmentSlotType.CHEST, (new Item.Properties()).fireResistant(), 8, 1));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_CHESTPLATE_8_2 = registerItem("partial_iridium_chestplate_2", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._8_2, EquipmentSlotType.CHEST, (new Item.Properties()).fireResistant(), 8, 2));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_CHESTPLATE_8_3 = registerItem("partial_iridium_chestplate_3", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._8_3, EquipmentSlotType.CHEST, (new Item.Properties()).fireResistant(), 8, 3));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_CHESTPLATE_8_4 = registerItem("partial_iridium_chestplate_4", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._8_4, EquipmentSlotType.CHEST, (new Item.Properties()).fireResistant(), 8, 4));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_CHESTPLATE_8_5 = registerItem("partial_iridium_chestplate_5", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._8_5, EquipmentSlotType.CHEST, (new Item.Properties()).fireResistant(), 8, 5));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_CHESTPLATE_8_6 = registerItem("partial_iridium_chestplate_6", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._8_6, EquipmentSlotType.CHEST, (new Item.Properties()).fireResistant(), 8, 6));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_CHESTPLATE_8_7 = registerItem("partial_iridium_chestplate_7", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._8_7, EquipmentSlotType.CHEST, (new Item.Properties()).fireResistant(), 8, 7));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_LEGGINGS_7_1 = registerItem("partial_iridium_leggings_1", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._7_1, EquipmentSlotType.LEGS, (new Item.Properties()).fireResistant(), 7, 1));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_LEGGINGS_7_2 = registerItem("partial_iridium_leggings_2", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._7_2, EquipmentSlotType.LEGS, (new Item.Properties()).fireResistant(), 7, 2));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_LEGGINGS_7_3 = registerItem("partial_iridium_leggings_3", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._7_3, EquipmentSlotType.LEGS, (new Item.Properties()).fireResistant(), 7, 3));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_LEGGINGS_7_4 = registerItem("partial_iridium_leggings_4", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._7_4, EquipmentSlotType.LEGS, (new Item.Properties()).fireResistant(), 7, 4));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_LEGGINGS_7_5 = registerItem("partial_iridium_leggings_5", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._7_5, EquipmentSlotType.LEGS, (new Item.Properties()).fireResistant(), 7, 5));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_LEGGINGS_7_6 = registerItem("partial_iridium_leggings_6", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._7_6, EquipmentSlotType.LEGS, (new Item.Properties()).fireResistant(), 7, 6));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_BOOTS_4_1 = registerItem("partial_iridium_boots_1", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._4_1, EquipmentSlotType.FEET, (new Item.Properties()).fireResistant(), 4, 1));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_BOOTS_4_2 = registerItem("partial_iridium_boots_2", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._4_2, EquipmentSlotType.FEET, (new Item.Properties()).fireResistant(), 4, 2));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_BOOTS_4_3 = registerItem("partial_iridium_boots_3", () -> new PartialIridiumArmorItem(IridiumArmorMaterial._4_3, EquipmentSlotType.FEET, (new Item.Properties()).fireResistant(), 4, 3));
    // endregion

    // Recipes
    public static final RegistryObject<IRecipeSerializer<?>> ENRICHMENT_RECIPE = RECIPE_REGISTER.register("crafting_special_enrichment", EnrichmentRecipe.Serializer::new);
}
