package com.zygzag.revamp.common;

import com.zygzag.revamp.common.block.IridiumOreBlock;
import com.zygzag.revamp.common.entity.DrifterEggEntity;
import com.zygzag.revamp.common.item.*;
import com.zygzag.revamp.common.item.socketables.*;
import com.zygzag.revamp.common.recipe.EnrichmentRecipe;
import com.zygzag.revamp.common.recipe.SocketRecipe;
import com.zygzag.revamp.common.tier.IridiumArmorMaterial;
import com.zygzag.revamp.common.tier.IridiumToolTier;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Registry {
    public static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Revamp.MODID);
    public static final DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Revamp.MODID);
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Revamp.MODID);
    public static final DeferredRegister<Enchantment> ENCHANTMENT_REGISTER = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Revamp.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Revamp.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, Revamp.MODID);
    public static final DeferredRegister<Effect> EFFECT_REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, Revamp.MODID);
    public static final DeferredRegister<SoundEvent> SOUND_REGISTER = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Revamp.MODID);

    public static void register(IEventBus bus) {
        ITEM_REGISTER.register(bus);
        BLOCK_REGISTER.register(bus);
        RECIPE_REGISTER.register(bus);
        ENCHANTMENT_REGISTER.register(bus);
        TILE_ENTITY_REGISTER.register(bus);
        ENTITY_REGISTER.register(bus);
        EFFECT_REGISTER.register(bus);
        SOUND_REGISTER.register(bus);
    }

    public static RegistryObject<Item> registerItem(String name, Supplier<Item> supplier) {
        return ITEM_REGISTER.register(name, supplier);
    }

    public static RegistryObject<Item> registerBlockItem(RegistryObject<Block> block, Item.Properties properties) {
        return ITEM_REGISTER.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
    }

    public static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUND_REGISTER.register(name, () -> new SoundEvent(new ResourceLocation(Revamp.MODID, name)));
    }

    //Sounds
    public static final RegistryObject<SoundEvent> DRIFTER_EGG_HIT = registerSound("entity.drifter_egg.hit");
    public static final RegistryObject<SoundEvent> DRIFTER_EGG_POP = registerSound("entity.drifter_egg.pop");
    public static final RegistryObject<SoundEvent> DRIFTER_EGG_SPAWN = registerSound("entity.drifter_egg.spawn");


    // Entities
    // public static final RegistryObject<EntityType<CustomIronGolemEntity>> CUSTOM_IRON_GOLEM = ENTITY_REGISTER.register("custom_iron_golem",() -> EntityType.Builder.of(CustomIronGolemEntity::new, EntityClassification.MISC).sized(1.4F, 2.7F).clientTrackingRange(10).build("custom_iron_golem"));
    // public static final RegistryObject<EntityType<AbominationWitherEntity>> ABOMINATION_WITHER = ENTITY_REGISTER.register("abomination_wither",() -> EntityType.Builder.of(AbominationWitherEntity::new, EntityClassification.MONSTER).fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.7F, 2.4F).clientTrackingRange(8).build("abomination_wither"));
    public static final RegistryObject<EntityType<DrifterEggEntity>> DRIFTER_EGG = ENTITY_REGISTER.register("drifter_egg",() -> EntityType.Builder.of(DrifterEggEntity::new, EntityClassification.MISC).sized(0.5F, 0.5F).build("drifter_egg"));

    // Blocks
    public static final RegistryObject<Block> IRIDIUM_ORE = BLOCK_REGISTER.register("iridium_ore", IridiumOreBlock::new);
    public static final RegistryObject<Block> RAW_IRIDIUM_BLOCK = BLOCK_REGISTER.register("raw_iridium_block", () -> new Block(AbstractBlock.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE)));
    // public static final RegistryObject<Block> CUSTOM_CAULDRON = BLOCK_REGISTER.register("custom_cauldron", CustomCauldronBlock::new);
    // public static final RegistryObject<Block> ALCHEMY_TABLE_BLOCK = BLOCK_REGISTER.register("alchemy_table", AlchemyTableBlock::new);

    // Block Items
    public static final RegistryObject<Item> IRIDIUM_ORE_ITEM = registerBlockItem(IRIDIUM_ORE, new Item.Properties().tab(Revamp.TAB));
    public static final RegistryObject<Item> RAW_IRIDIUM_BLOCK_ITEM = registerBlockItem(RAW_IRIDIUM_BLOCK, new Item.Properties().tab(Revamp.TAB));
    // public static final RegistryObject<Item> CUSTOM_CAULDRON_ITEM = registerBlockItem(CUSTOM_CAULDRON, new Item.Properties().tab(Revamp.TAB));

    // Items
    public static final RegistryObject<Item> GLOID_BUCKET = registerItem("gloid_bucket", () -> new Item(new Item.Properties().tab(Revamp.TAB)));
    public static final RegistryObject<Item> DRIFTER_EGG_BUCKET = registerItem("drifter_egg_bucket", () -> new Item(new Item.Properties().tab(Revamp.TAB)));
    public static final RegistryObject<Item> GLOID_BALL = registerItem("gloid_ball", () -> new Item(new Item.Properties().tab(Revamp.TAB)));
    public static final RegistryObject<Item> IRIDIUM_PLATING = registerItem("iridium_plating", () -> new Item(new Item.Properties().tab(Revamp.TAB)));
    public static final RegistryObject<Item> RAW_IRIDIUM = registerItem("raw_iridium", () -> new Item(new Item.Properties().tab(Revamp.TAB)));
    public static final RegistryObject<Item> SOCKET_TEST = registerItem("socket_test", () -> new SocketItem(new Item.Properties().tab(Revamp.TAB)));
    /*public static final RegistryObject<Item> DAMAGE_ENRICHMENT = ITEM_REGISTER.register("damage_enrichment", () -> new DamageEnrichmentItem(new Item.Properties().tab(Revamp.TAB)));
    public static final RegistryObject<Item> SHULKER_BOWL = ITEM_REGISTER.register("shulker_bowl", () -> new ShulkerBowlItem(new Item.Properties().tab(Revamp.TAB).durability(16)));
    public static final RegistryObject<Item> EMPTY_SHULKER_BOWL = registerItem("empty_shulker_bowl", () -> new Item(new Item.Properties().tab(Revamp.TAB)));
    public static final RegistryObject<Item> EMPOWERMENT_STAR = registerItem("empowerment_star", () -> new Item(new Item.Properties().tab(Revamp.TAB)));*/

    // Tile Entities
    /*public static final RegistryObject<TileEntityType<CustomCauldronTileEntity>> CUSTOM_CAULDRON_TILE_ENTITY = TILE_ENTITY_REGISTER.register("custom_cauldron", () -> new TileEntityType<>(CustomCauldronTileEntity::new, Sets.newHashSet(CUSTOM_CAULDRON.get()), null));
    public static final RegistryObject<TileEntityType<AlchemyTableTileEntity>> ALCHEMY_TABLE_TILE_ENTITY = TILE_ENTITY_REGISTER.register("alchemy_table", () -> new TileEntityType<>(AlchemyTableTileEntity::new, Sets.newHashSet(ALCHEMY_TABLE_BLOCK.get()), null));*/

    // Tool Items
    // public static final RegistryObject<Item> SHORTBOW = registerItem("shortbow", () -> new ShortbowItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(384)));
    // public static final RegistryObject<Item> LONGBOW = registerItem("longbow", () -> new LongbowItem(new Item.Properties().tab(ItemGroup.TAB_COMBAT).durability(384)));

    public static final RegistryObject<Item> IRIDIUM_SWORD = registerItem("iridium_sword", () -> new IridiumSwordItem(IridiumToolTier.FULL, 3, -2.4F, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));
    public static final RegistryObject<Item> IRIDIUM_SHOVEL = registerItem("iridium_shovel", () -> new IridiumShovelItem(IridiumToolTier.FULL, 1.5F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS).fireResistant()));
    public static final RegistryObject<Item> IRIDIUM_PICKAXE = registerItem("iridium_pickaxe", () -> new IridiumPickaxeItem(IridiumToolTier.FULL, 1, -2.8F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS).fireResistant()));
    public static final RegistryObject<Item> IRIDIUM_AXE = registerItem("iridium_axe", () -> new IridiumAxeItem(IridiumToolTier.FULL, 5.0F, -3.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS).fireResistant()));
    public static final RegistryObject<Item> IRIDIUM_HOE = registerItem("iridium_hoe", () -> new IridiumHoeItem(IridiumToolTier.FULL, -1, 0.0F, (new Item.Properties()).tab(ItemGroup.TAB_TOOLS).fireResistant()));

    public static final RegistryObject<Item> IRIDIUM_HELMET = registerItem("iridium_helmet", () -> new ArmorItem(IridiumArmorMaterial.IRIDIUM, EquipmentSlotType.HEAD, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));
    public static final RegistryObject<Item> IRIDIUM_CHESTPLATE = registerItem("iridium_chestplate", () -> new IridiumChestplateItem(IridiumArmorMaterial.IRIDIUM, (new Item.Properties()).tab(ItemGroup.TAB_COMBAT).fireResistant()));
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
    // public static final RegistryObject<IRecipeSerializer<?>> ENRICHMENT_RECIPE = RECIPE_REGISTER.register("crafting_special_enrichment", EnrichmentRecipe.Serializer::new);
    public static final RegistryObject<IRecipeSerializer<?>> SOCKET_RECIPE = RECIPE_REGISTER.register("crafting_special_socket", SocketRecipe.Serializer::new);

    // Effects
    // public static final RegistryObject<Effect> EMPOWERMENT = EFFECT_REGISTER.register("empowerment", EmpowermentEffect::new);
}
