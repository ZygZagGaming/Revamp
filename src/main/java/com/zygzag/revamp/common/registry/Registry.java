package com.zygzag.revamp.common.registry;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.block.*;
import com.zygzag.revamp.common.block.entity.UpgradedBlastFurnaceBlockEntity;
import com.zygzag.revamp.common.block.menu.UpgradedBlastFurnaceMenu;
import com.zygzag.revamp.common.entity.EmpoweredWither;
import com.zygzag.revamp.common.entity.HomingWitherSkull;
import com.zygzag.revamp.common.entity.ThrownAxe;
import com.zygzag.revamp.common.entity.ThrownTransmutationCharge;
import com.zygzag.revamp.common.entity.effect.SightEffect;
import com.zygzag.revamp.common.item.EmpowermentStar;
import com.zygzag.revamp.common.item.EnchantedBowlFoodItem;
import com.zygzag.revamp.common.item.ShulkerBowlItem;
import com.zygzag.revamp.common.item.TransmutationCharge;
import com.zygzag.revamp.common.item.enchant.CooldownEnchantment;
import com.zygzag.revamp.common.item.iridium.*;
import com.zygzag.revamp.common.item.iridium.partial.*;
import com.zygzag.revamp.common.item.recipe.*;
import com.zygzag.revamp.common.item.tier.IridiumArmorMaterial;
import com.zygzag.revamp.common.item.tier.IridiumToolTier;
import com.zygzag.revamp.common.loot.AutosmeltModifier;
import com.zygzag.revamp.common.loot.ExecutionerModifier;
import com.zygzag.revamp.util.Constants;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;
import java.util.function.Supplier;

import static com.zygzag.revamp.common.Revamp.MAIN_TAB;

@SuppressWarnings("unused")
public class Registry {
    public static DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Revamp.MODID);
    public static DeferredRegister<Block> BLOCK_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, Revamp.MODID);
    public static DeferredRegister<RecipeSerializer<?>> RECIPE_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Revamp.MODID);
    public static DeferredRegister<Enchantment> ENCHANT_REGISTER = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Revamp.MODID);
    public static DeferredRegister<EntityType<?>> ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, Revamp.MODID);
    public static DeferredRegister<Potion> POTION_REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, Revamp.MODID);
    public static DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_REGISTER = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, Revamp.MODID);
    public static DeferredRegister<MobEffect> EFFECT_REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Revamp.MODID);
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Revamp.MODID);
    public static DeferredRegister<MenuType<?>> MENU_REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, Revamp.MODID);
    public static DeferredRegister<?>[] REGISTERS = {ITEM_REGISTER, BLOCK_REGISTER, RECIPE_REGISTER, ENCHANT_REGISTER, ENTITY_REGISTER, POTION_REGISTER, LOOT_REGISTER, EFFECT_REGISTER, BLOCK_ENTITY_REGISTER, MENU_REGISTER};

    public static final RegistryObject<Block> IRIDIUM_ORE = registerBlock("iridium_ore", IridiumOreBlock::new);
    public static final RegistryObject<Block> DEEPSLATE_IRIDIUM_ORE = registerBlock("deepslate_iridium_ore", () -> new IridiumOreBlock(BlockBehaviour.Properties.copy(IRIDIUM_ORE.get()).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
    public static final RegistryObject<Block> RAW_IRIDIUM_BLOCK = registerBlock("raw_iridium_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE)));
    //public static final RegistryObject<Block> IRIDIUM_GRATING = registerBlock("iridium_grating", () -> new GrateBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(1f, 6f).sound(SoundType.METAL)));
    public static final RegistryObject<Block> BLESSED_SOIL = registerBlock("blessed_soil", () -> new BlessedSoilBlock(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> UPGRADED_BLAST_FURNACE = registerBlock("upgraded_blast_furnace", () -> new UpgradedBlastFurnace(BlockBehaviour.Properties.copy(Blocks.BLAST_FURNACE)));
    public static final RegistryObject<Block> OSTEUM = registerBlock("osteum", () -> new OsteumBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK)));
    public static final RegistryObject<Block> GROWING_OSTEUM = registerBlock("growing_osteum", () -> new GrowingOsteumBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK)));

    public static final RegistryObject<Item> OSTEUM_ITEM = registerBlockItem(OSTEUM, new Item.Properties().tab(MAIN_TAB));
    public static final RegistryObject<Item> EMPOWERMENT_STAR_ITEM = registerItem("empowerment_star", () -> new EmpowermentStar(new Item.Properties().tab(MAIN_TAB)));
    public static final RegistryObject<Item> IRIDIUM_PLATING = basicItem("iridium_plating");
    public static final RegistryObject<Item> RAW_IRIDIUM = basicItem("raw_iridium");
    public static final RegistryObject<Item> RAW_IRIDIUM_ROD = basicItem("raw_iridium_rod");
    public static final RegistryObject<Item> FORGED_IRIDIUM_ROD = basicItem("forged_iridium_rod");
    public static final RegistryObject<Item> IRIDIUM_RING_ITEM = basicItem("iridium_ring");
    public static final RegistryObject<Item> SHULKER_BOWL_ITEM = registerItem("shulker_bowl", ShulkerBowlItem::new);
    public static final RegistryObject<Item> GOLDEN_STEW_ITEM = registerItem("golden_stew", () -> new BowlFoodItem(
            new Item.Properties().stacksTo(1).craftRemainder(Items.BOWL).tab(CreativeModeTab.TAB_FOOD).rarity(Rarity.RARE).food(
                    new FoodProperties.Builder().alwaysEat().nutrition(6).saturationMod(0.6f)
                            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 600, 2), 1f)
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 200, 1), 1f)
                            .build())));

    public static final RegistryObject<Item> ENCHANTED_GOLDEN_STEW_ITEM = registerItem("enchanted_golden_stew", () -> new EnchantedBowlFoodItem(
            new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).craftRemainder(Items.BOWL).tab(CreativeModeTab.TAB_FOOD).food(
                    new FoodProperties.Builder().alwaysEat().nutrition(12).saturationMod(1.6f)
                            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 1200, 4), 1f)
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1), 1f)
                            .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200), 1f)
                            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1), 1f)
                            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 600, 2), 1f)
                            .build())));

    public static final RegistryObject<Item> TRANSMUTATION_CHARGE = registerItem("transmutation_charge", () -> new TransmutationCharge(new Item.Properties().tab(MAIN_TAB)));

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

    public static final RegistryObject<Item> IRIDIUM_ORE_ITEM = registerBlockItem(IRIDIUM_ORE, new Item.Properties().tab(MAIN_TAB));
    public static final RegistryObject<Item> DEEPSLATE_IRIDIUM_ORE_ITEM = registerBlockItem(DEEPSLATE_IRIDIUM_ORE, new Item.Properties().tab(MAIN_TAB));
    public static final RegistryObject<Item> RAW_IRIDIUM_BLOCK_ITEM = registerBlockItem(RAW_IRIDIUM_BLOCK, new Item.Properties().tab(MAIN_TAB));
    //public static final RegistryObject<Item> IRIDIUM_GRATING_ITEM = registerBlockItem(IRIDIUM_GRATING, new Item.Properties().tab(Revamp.MAIN_TAB));
    public static final RegistryObject<Item> BLESSED_SOIL_ITEM = registerBlockItem(BLESSED_SOIL, new Item.Properties().tab(MAIN_TAB));
    public static final RegistryObject<Item> UPGRADED_BLAST_FURNACE_ITEM = registerBlockItem(UPGRADED_BLAST_FURNACE, new Item.Properties().tab(MAIN_TAB));

    // region iridium gear
    public static final RegistryObject<Item> IRIDIUM_SWORD = registerItem("iridium_sword", () -> new IridiumSwordItem(IridiumToolTier.FULL, 3, -2.4F, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1), Socket.NONE));
    public static final RegistryObject<Item> IRIDIUM_SHOVEL = registerItem("iridium_shovel", () -> new IridiumShovelItem(IridiumToolTier.FULL, 1.5F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1), Socket.NONE));
    public static final RegistryObject<Item> IRIDIUM_PICKAXE = registerItem("iridium_pickaxe", () -> new IridiumPickaxeItem(IridiumToolTier.FULL, 1, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1), Socket.NONE));
    public static final RegistryObject<Item> IRIDIUM_AXE = registerItem("iridium_axe", () -> new IridiumAxeItem(IridiumToolTier.FULL, 5.0F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1), Socket.NONE));
    public static final RegistryObject<Item> IRIDIUM_HOE = registerItem("iridium_hoe", () -> new IridiumHoeItem(IridiumToolTier.FULL, -1, 0.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1), Socket.NONE));

    public static final RegistryObject<Item> IRIDIUM_HELMET = registerItem("iridium_helmet", () -> new ArmorItem(IridiumArmorMaterial.IRIDIUM, EquipmentSlot.HEAD, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1)));
    public static final RegistryObject<Item> IRIDIUM_CHESTPLATE = registerItem("iridium_chestplate", () -> new IridiumChestplateItem(IridiumArmorMaterial.IRIDIUM, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1), Socket.NONE));
    public static final RegistryObject<Item> IRIDIUM_LEGGINGS = registerItem("iridium_leggings", () -> new ArmorItem(IridiumArmorMaterial.IRIDIUM, EquipmentSlot.LEGS, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1)));
    public static final RegistryObject<Item> IRIDIUM_BOOTS = registerItem("iridium_boots", () -> new ArmorItem(IridiumArmorMaterial.IRIDIUM, EquipmentSlot.FEET, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1)));

    public static final RegistryObject<Item> IRIDIUM_SCEPTER = registerItem("iridium_scepter", () -> new IridiumScepterItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1), Socket.NONE));

    // region diamond socketed gear
    public static final RegistryObject<Item> DIAMOND_SOCKETED_IRIDIUM_SWORD = registerItem("diamond_socketed_iridium_sword", () -> new IridiumSwordItem(IridiumToolTier.DIAMOND_SOCKETED, 3, -2.4F, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.DIAMOND), Socket.DIAMOND));
    public static final RegistryObject<Item> DIAMOND_SOCKETED_IRIDIUM_SHOVEL = registerItem("diamond_socketed_iridium_shovel", () -> new IridiumShovelItem(IridiumToolTier.DIAMOND_SOCKETED, 1.5F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.DIAMOND), Socket.DIAMOND));
    public static final RegistryObject<Item> DIAMOND_SOCKETED_IRIDIUM_PICKAXE = registerItem("diamond_socketed_iridium_pickaxe", () -> new IridiumPickaxeItem(IridiumToolTier.DIAMOND_SOCKETED_PICK, 1, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.DIAMOND), Socket.DIAMOND));
    public static final RegistryObject<Item> DIAMOND_SOCKETED_IRIDIUM_AXE = registerItem("diamond_socketed_iridium_axe", () -> new IridiumAxeItem(IridiumToolTier.DIAMOND_SOCKETED, 5.0F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.DIAMOND), Socket.DIAMOND));
    public static final RegistryObject<Item> DIAMOND_SOCKETED_IRIDIUM_HOE = registerItem("diamond_socketed_iridium_hoe", () -> new IridiumHoeItem(IridiumToolTier.DIAMOND_SOCKETED, -1, 0.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.DIAMOND), Socket.DIAMOND));

    public static final RegistryObject<Item> DIAMOND_SOCKETED_IRIDIUM_CHESTPLATE = registerItem("diamond_socketed_iridium_chestplate", () -> new IridiumChestplateItem(IridiumArmorMaterial.DIAMOND_SOCKETED, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.DIAMOND), Socket.DIAMOND));

    public static final RegistryObject<Item> DIAMOND_SOCKETED_IRIDIUM_SCEPTER = registerItem("diamond_socketed_iridium_scepter", () -> new IridiumScepterItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(IRIDIUM_SCEPTER.get()).craftRemainder(Items.DIAMOND), Socket.DIAMOND));
    // endregion

    // region emerald socketed gear
    public static final RegistryObject<Item> EMERALD_SOCKETED_IRIDIUM_SWORD = registerItem("emerald_socketed_iridium_sword", () -> new IridiumSwordItem(IridiumToolTier.EMERALD_SOCKETED, 3, -2.4F, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.EMERALD), Socket.EMERALD));
    public static final RegistryObject<Item> EMERALD_SOCKETED_IRIDIUM_SHOVEL = registerItem("emerald_socketed_iridium_shovel", () -> new IridiumShovelItem(IridiumToolTier.EMERALD_SOCKETED, 1.5F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.EMERALD), Socket.EMERALD));
    public static final RegistryObject<Item> EMERALD_SOCKETED_IRIDIUM_PICKAXE = registerItem("emerald_socketed_iridium_pickaxe", () -> new IridiumPickaxeItem(IridiumToolTier.EMERALD_SOCKETED, 1, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.EMERALD), Socket.EMERALD));
    public static final RegistryObject<Item> EMERALD_SOCKETED_IRIDIUM_AXE = registerItem("emerald_socketed_iridium_axe", () -> new IridiumAxeItem(IridiumToolTier.EMERALD_SOCKETED, 5.0F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.EMERALD), Socket.EMERALD));
    public static final RegistryObject<Item> EMERALD_SOCKETED_IRIDIUM_HOE = registerItem("emerald_socketed_iridium_hoe", () -> new IridiumHoeItem(IridiumToolTier.EMERALD_SOCKETED, -1, 0.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.EMERALD), Socket.EMERALD));

    public static final RegistryObject<Item> EMERALD_SOCKETED_IRIDIUM_CHESTPLATE = registerItem("emerald_socketed_iridium_chestplate", () -> new IridiumChestplateItem(IridiumArmorMaterial.EMERALD_SOCKETED, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.EMERALD), Socket.EMERALD));

    public static final RegistryObject<Item> EMERALD_SOCKETED_IRIDIUM_SCEPTER = registerItem("emerald_socketed_iridium_scepter", () -> new IridiumScepterItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.EMERALD), Socket.EMERALD));
    // endregion

    // region skull socketed gear
    public static final RegistryObject<Item> SKULL_SOCKETED_IRIDIUM_SWORD = registerItem("skull_socketed_iridium_sword", () -> new IridiumSwordItem(IridiumToolTier.FULL, 3, -2.4F, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.SKELETON_SKULL), Socket.SKULL));
    public static final RegistryObject<Item> SKULL_SOCKETED_IRIDIUM_SHOVEL = registerItem("skull_socketed_iridium_shovel", () -> new IridiumShovelItem(IridiumToolTier.FULL, 1.5F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.SKELETON_SKULL), Socket.SKULL));
    public static final RegistryObject<Item> SKULL_SOCKETED_IRIDIUM_PICKAXE = registerItem("skull_socketed_iridium_pickaxe", () -> new IridiumPickaxeItem(IridiumToolTier.FULL, 1, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.SKELETON_SKULL), Socket.SKULL));
    public static final RegistryObject<Item> SKULL_SOCKETED_IRIDIUM_AXE = registerItem("skull_socketed_iridium_axe", () -> new IridiumAxeItem(IridiumToolTier.FULL, 5.0F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.SKELETON_SKULL), Socket.SKULL));
    public static final RegistryObject<Item> SKULL_SOCKETED_IRIDIUM_HOE = registerItem("skull_socketed_iridium_hoe", () -> new IridiumHoeItem(IridiumToolTier.FULL, -1, 0.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.SKELETON_SKULL), Socket.SKULL));

    public static final RegistryObject<Item> SKULL_SOCKETED_IRIDIUM_CHESTPLATE = registerItem("skull_socketed_iridium_chestplate", () -> new IridiumChestplateItem(IridiumArmorMaterial.IRIDIUM, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.SKELETON_SKULL), Socket.SKULL));

    public static final RegistryObject<Item> SKULL_SOCKETED_IRIDIUM_SCEPTER = registerItem("skull_socketed_iridium_scepter", () -> new IridiumScepterItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.SKELETON_SKULL), Socket.SKULL));
    // endregion

    // region wither skull socketed gear
    public static final RegistryObject<Item> WITHER_SKULL_SOCKETED_IRIDIUM_SWORD = registerItem("wither_skull_socketed_iridium_sword", () -> new IridiumSwordItem(IridiumToolTier.FULL, 3, -2.4F, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.WITHER_SKELETON_SKULL), Socket.WITHER_SKULL));
    public static final RegistryObject<Item> WITHER_SKULL_SOCKETED_IRIDIUM_SHOVEL = registerItem("wither_skull_socketed_iridium_shovel", () -> new IridiumShovelItem(IridiumToolTier.FULL, 1.5F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.WITHER_SKELETON_SKULL), Socket.WITHER_SKULL));
    public static final RegistryObject<Item> WITHER_SKULL_SOCKETED_IRIDIUM_PICKAXE = registerItem("wither_skull_socketed_iridium_pickaxe", () -> new IridiumPickaxeItem(IridiumToolTier.WITHER_SOCKETED_PICK, 1, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.WITHER_SKELETON_SKULL), Socket.WITHER_SKULL));
    public static final RegistryObject<Item> WITHER_SKULL_SOCKETED_IRIDIUM_AXE = registerItem("wither_skull_socketed_iridium_axe", () -> new IridiumAxeItem(IridiumToolTier.FULL, 5.0F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).craftRemainder(Items.WITHER_SKELETON_SKULL)
            .fireResistant().stacksTo(1), Socket.WITHER_SKULL));
    public static final RegistryObject<Item> WITHER_SKULL_SOCKETED_IRIDIUM_HOE = registerItem("wither_skull_socketed_iridium_hoe", () -> new IridiumHoeItem(IridiumToolTier.FULL, 6, 0.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.WITHER_SKELETON_SKULL), Socket.WITHER_SKULL));

    public static final RegistryObject<Item> WITHER_SKULL_SOCKETED_IRIDIUM_CHESTPLATE = registerItem("wither_skull_socketed_iridium_chestplate", () -> new IridiumChestplateItem(IridiumArmorMaterial.IRIDIUM, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.WITHER_SKELETON_SKULL), Socket.WITHER_SKULL));

    public static final RegistryObject<Item> WITHER_SKULL_SOCKETED_IRIDIUM_SCEPTER = registerItem("wither_skull_socketed_iridium_scepter", () -> new IridiumScepterItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.WITHER_SKELETON_SKULL), Socket.WITHER_SKULL));
    // endregion

    // region amethyst socketed gear
    public static final RegistryObject<Item> AMETHYST_SOCKETED_IRIDIUM_SWORD = registerItem("amethyst_socketed_iridium_sword", () -> new IridiumSwordItem(IridiumToolTier.FULL, 3, -2.4F, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.AMETHYST_SHARD), Socket.AMETHYST));
    public static final RegistryObject<Item> AMETHYST_SOCKETED_IRIDIUM_SHOVEL = registerItem("amethyst_socketed_iridium_shovel", () -> new IridiumShovelItem(IridiumToolTier.FULL, 1.5F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.AMETHYST_SHARD), Socket.AMETHYST));
    public static final RegistryObject<Item> AMETHYST_SOCKETED_IRIDIUM_PICKAXE = registerItem("amethyst_socketed_iridium_pickaxe", () -> new IridiumPickaxeItem(IridiumToolTier.FULL, 1, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.AMETHYST_SHARD), Socket.AMETHYST));
    public static final RegistryObject<Item> AMETHYST_SOCKETED_IRIDIUM_AXE = registerItem("amethyst_socketed_iridium_axe", () -> new IridiumAxeItem(IridiumToolTier.FULL, 5.0F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).craftRemainder(Items.AMETHYST_SHARD)
            .fireResistant().stacksTo(1), Socket.AMETHYST));
    public static final RegistryObject<Item> AMETHYST_SOCKETED_IRIDIUM_HOE = registerItem("amethyst_socketed_iridium_hoe", () -> new IridiumHoeItem(IridiumToolTier.FULL, -1, 0.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.AMETHYST_SHARD), Socket.AMETHYST));

    public static final RegistryObject<Item> AMETHYST_SOCKETED_IRIDIUM_CHESTPLATE = registerItem("amethyst_socketed_iridium_chestplate", () -> new IridiumChestplateItem(IridiumArmorMaterial.IRIDIUM, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.AMETHYST_SHARD), Socket.AMETHYST));

    public static final RegistryObject<Item> AMETHYST_SOCKETED_IRIDIUM_SCEPTER = registerItem("amethyst_socketed_iridium_scepter", () -> new IridiumScepterItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.AMETHYST_SHARD), Socket.AMETHYST));
    // endregion

    // endregion

    // region partial iridium tools
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_SWORD_2_1 = registerItem("partial_iridium_sword_1", () -> new PartialIridiumSword(IridiumToolTier.HALF, 3, -2.4F, (new Item.Properties().stacksTo(1)), 2, 1));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_PICKAXE_3_1 = registerItem("partial_iridium_pickaxe_1", () -> new PartialIridiumPickaxe(IridiumToolTier._3_1, 1, -2.8F, (new Item.Properties().stacksTo(1)), 3, 1));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_PICKAXE_3_2 = registerItem("partial_iridium_pickaxe_2", () -> new PartialIridiumPickaxe(IridiumToolTier._3_2, 1, -2.8F, (new Item.Properties().stacksTo(1)), 3, 2));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_AXE_4_1 = registerItem("partial_iridium_axe_1", () -> new PartialIridiumAxe(IridiumToolTier._3_1, 5, -3.0F, (new Item.Properties().stacksTo(1).craftRemainder(Registry.IRIDIUM_AXE.get())), 3, 1));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_AXE_4_2 = registerItem("partial_iridium_axe_2", () -> new PartialIridiumAxe(IridiumToolTier._3_2, 5, -3.0F, (new Item.Properties().stacksTo(1)), 3, 2));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_HOE_2_1 = registerItem("partial_iridium_hoe_1", () -> new PartialIridiumHoe(IridiumToolTier.HALF, -1, 0.0F, (new Item.Properties()).fireResistant().stacksTo(1), 2, 1));
    // endregion

    // region partial iridium armors
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_HELMET_5_1 = registerItem("partial_iridium_helmet_1", () -> new PartialIridiumArmor(IridiumArmorMaterial._5_1, EquipmentSlot.HEAD, (new Item.Properties()).fireResistant(), 5, 1));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_HELMET_5_2 = registerItem("partial_iridium_helmet_2", () -> new PartialIridiumArmor(IridiumArmorMaterial._5_2, EquipmentSlot.HEAD, (new Item.Properties()).fireResistant(), 5, 2));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_HELMET_5_3 = registerItem("partial_iridium_helmet_3", () -> new PartialIridiumArmor(IridiumArmorMaterial._5_3, EquipmentSlot.HEAD, (new Item.Properties()).fireResistant(), 5, 3));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_HELMET_5_4 = registerItem("partial_iridium_helmet_4", () -> new PartialIridiumArmor(IridiumArmorMaterial._5_4, EquipmentSlot.HEAD, (new Item.Properties()).fireResistant(), 5, 4));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_CHESTPLATE_8_1 = registerItem("partial_iridium_chestplate_1", () -> new PartialIridiumArmor(IridiumArmorMaterial._8_1, EquipmentSlot.CHEST, (new Item.Properties()).fireResistant(), 8, 1));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_CHESTPLATE_8_2 = registerItem("partial_iridium_chestplate_2", () -> new PartialIridiumArmor(IridiumArmorMaterial._8_2, EquipmentSlot.CHEST, (new Item.Properties()).fireResistant(), 8, 2));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_CHESTPLATE_8_3 = registerItem("partial_iridium_chestplate_3", () -> new PartialIridiumArmor(IridiumArmorMaterial._8_3, EquipmentSlot.CHEST, (new Item.Properties()).fireResistant(), 8, 3));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_CHESTPLATE_8_4 = registerItem("partial_iridium_chestplate_4", () -> new PartialIridiumArmor(IridiumArmorMaterial._8_4, EquipmentSlot.CHEST, (new Item.Properties()).fireResistant(), 8, 4));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_CHESTPLATE_8_5 = registerItem("partial_iridium_chestplate_5", () -> new PartialIridiumArmor(IridiumArmorMaterial._8_5, EquipmentSlot.CHEST, (new Item.Properties()).fireResistant(), 8, 5));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_CHESTPLATE_8_6 = registerItem("partial_iridium_chestplate_6", () -> new PartialIridiumArmor(IridiumArmorMaterial._8_6, EquipmentSlot.CHEST, (new Item.Properties()).fireResistant(), 8, 6));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_CHESTPLATE_8_7 = registerItem("partial_iridium_chestplate_7", () -> new PartialIridiumArmor(IridiumArmorMaterial._8_7, EquipmentSlot.CHEST, (new Item.Properties()).fireResistant(), 8, 7));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_LEGGINGS_7_1 = registerItem("partial_iridium_leggings_1", () -> new PartialIridiumArmor(IridiumArmorMaterial._7_1, EquipmentSlot.LEGS, (new Item.Properties()).fireResistant(), 7, 1));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_LEGGINGS_7_2 = registerItem("partial_iridium_leggings_2", () -> new PartialIridiumArmor(IridiumArmorMaterial._7_2, EquipmentSlot.LEGS, (new Item.Properties()).fireResistant(), 7, 2));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_LEGGINGS_7_3 = registerItem("partial_iridium_leggings_3", () -> new PartialIridiumArmor(IridiumArmorMaterial._7_3, EquipmentSlot.LEGS, (new Item.Properties()).fireResistant(), 7, 3));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_LEGGINGS_7_4 = registerItem("partial_iridium_leggings_4", () -> new PartialIridiumArmor(IridiumArmorMaterial._7_4, EquipmentSlot.LEGS, (new Item.Properties()).fireResistant(), 7, 4));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_LEGGINGS_7_5 = registerItem("partial_iridium_leggings_5", () -> new PartialIridiumArmor(IridiumArmorMaterial._7_5, EquipmentSlot.LEGS, (new Item.Properties()).fireResistant(), 7, 5));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_LEGGINGS_7_6 = registerItem("partial_iridium_leggings_6", () -> new PartialIridiumArmor(IridiumArmorMaterial._7_6, EquipmentSlot.LEGS, (new Item.Properties()).fireResistant(), 7, 6));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_BOOTS_4_1 = registerItem("partial_iridium_boots_1", () -> new PartialIridiumArmor(IridiumArmorMaterial._4_1, EquipmentSlot.FEET, (new Item.Properties()).fireResistant(), 4, 1));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_BOOTS_4_2 = registerItem("partial_iridium_boots_2", () -> new PartialIridiumArmor(IridiumArmorMaterial._4_2, EquipmentSlot.FEET, (new Item.Properties()).fireResistant(), 4, 2));
    public static final RegistryObject<Item> PARTIAL_IRIDIUM_BOOTS_4_3 = registerItem("partial_iridium_boots_3", () -> new PartialIridiumArmor(IridiumArmorMaterial._4_3, EquipmentSlot.FEET, (new Item.Properties()).fireResistant(), 4, 3));
    // endregion

    // region enchants
    public static final RegistryObject<Enchantment> COOLDOWN_ENCHANTMENT = registerEnchant("cooldown", () -> new CooldownEnchantment(Enchantment.Rarity.RARE));
    // endregion

    // region recipes
    public static final RegistryObject<RecipeSerializer<ShulkerBowlRecipe>> SHULKER_BOWL_CRAFTING = registerRecipeSerializer("crafting_special_shulker_bowl", () -> new SimpleRecipeSerializer<>(ShulkerBowlRecipe::new));
    public static final RegistryObject<RecipeSerializer<TransmutationRecipe>> TRANSMUTATION_SERIALIZER = registerRecipeSerializer("transmutation", TransmutationRecipe.TransmutationSerializer::new);
    public static final RegistryObject<RecipeSerializer<EmpowermentRecipe>> EMPOWERMENT_SERIALIZER = registerRecipeSerializer("empowerment", EmpowermentRecipe.EmpowermentSerializer::new);
    public static final RegistryObject<RecipeSerializer<SocketRemoveRecipe>> SOCKET_REMOVE_CRAFTING = registerRecipeSerializer("crafting_special_socket_remove", () -> new SimpleRecipeSerializer<>(SocketRemoveRecipe::new));
    public static final RegistryObject<RecipeSerializer<UpgradedBlastFurnaceRecipe>> UPGRADED_BLAST_FURNACE_SERIALIZER = registerRecipeSerializer("upgraded_blasting", UpgradedBlastFurnaceRecipe.UpgradedBlastingSerializer::new);
    // endregion

    // region entities
    public static final RegistryObject<EntityType<EmpoweredWither>> EMPOWERED_WITHER = registerEntity("empowered_wither", () ->
            EntityType.Builder.of(EmpoweredWither::new, MobCategory.MONSTER)
                    .fireImmune()
                    .immuneTo(Blocks.WITHER_ROSE)
                    .sized(1.1F, 3.75F)
                    .clientTrackingRange(10));

    public static final RegistryObject<EntityType<HomingWitherSkull>> HOMING_WITHER_SKULL = registerEntity("homing_wither_skull", () -> EntityType.Builder.<HomingWitherSkull>of(HomingWitherSkull::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4));
    public static final RegistryObject<EntityType<ThrownTransmutationCharge>> TRANSMUTATION_BOTTLE_ENTITY = registerEntity("transmutation_bottle", () -> EntityType.Builder.of(ThrownTransmutationCharge::new, MobCategory.MISC));
    public static final RegistryObject<EntityType<ThrownAxe>> THROWN_AXE = registerEntity("thrown_axe", () -> EntityType.Builder.of(ThrownAxe::new, MobCategory.MISC));
    // endregion

    // region effects
    public static RegistryObject<MobEffect> SIGHT_EFFECT = registerEffect("sight", () -> new SightEffect(MobEffectCategory.BENEFICIAL, Constants.SIGHT_EFFECT_COLOR, (b) -> b.is(Tags.Blocks.ORES), GeneralUtil::getColor));
    public static RegistryObject<MobEffect> GREEN_THUMB_EFFECT = registerEffect("green_thumb", () -> new SightEffect(MobEffectCategory.BENEFICIAL, Constants.SIGHT_EFFECT_COLOR, (b) -> (b.getBlock() instanceof FarmBlock && b.hasProperty(FarmBlock.MOISTURE) && b.getValue(FarmBlock.MOISTURE) != 7) || (b.getBlock() instanceof CropBlock c && c.isMaxAge(b)), (b) -> b.getBlock() instanceof CropBlock ? Constants.CROP_COLOR : Constants.UNWATERED_SOIL_HIGHLIGHT_COLOR));
    public static RegistryObject<MobEffect> REACH_EFFECT = registerEffect("reach", () -> new MobEffect(MobEffectCategory.BENEFICIAL, Constants.REACH_EFFECT_COLOR).addAttributeModifier(ForgeMod.REACH_DISTANCE.get(), UUID.randomUUID().toString(), 2.0, AttributeModifier.Operation.ADDITION));
    // endregion

    // region potions
    public static final RegistryObject<Potion> RAGE_POTION = registerPotion("rage", () -> new Potion("rage", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 800, 1), new MobEffectInstance(MobEffects.DAMAGE_BOOST, 800, 1)));
    public static final RegistryObject<Potion> LONG_RAGE_POTION = registerPotion("long_rage", () -> new Potion("long_rage", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1600, 1), new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1600, 1)));
    public static final RegistryObject<Potion> STRONG_RAGE_POTION = registerPotion("strong_rage", () -> new Potion("strong_rage", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 750, 2), new MobEffectInstance(MobEffects.DAMAGE_BOOST, 750, 2)));

    public static final RegistryObject<Potion> SIGHT_POTION = registerPotion("sight", () -> new Potion("sight", new MobEffectInstance(SIGHT_EFFECT.get(), 100, 0)));
    public static final RegistryObject<Potion> LONG_SIGHT_POTION = registerPotion("long_sight", () -> new Potion("long_sight", new MobEffectInstance(SIGHT_EFFECT.get(), 200, 0)));
    public static final RegistryObject<Potion> STRONG_SIGHT_POTION = registerPotion("strong_sight", () -> new Potion("strong_sight", new MobEffectInstance(SIGHT_EFFECT.get(), 100, 1)));
    // endregion

    // region loot mods
    public static RegistryObject<GlobalLootModifierSerializer<?>> EXECUTIONER_LOOT_MOD = LOOT_REGISTER.register("executioner", ExecutionerModifier.Serializer::new);
    public static RegistryObject<GlobalLootModifierSerializer<?>> AUTOSMELT_LOOT_MOD = LOOT_REGISTER.register("autosmelt", AutosmeltModifier.Serializer::new);
    // endregion

    // region block entities
    public static RegistryObject<BlockEntityType<UpgradedBlastFurnaceBlockEntity>> UPGRADED_BLAST_FURNACE_BLOCK_ENTITY = registerBlockEntity("upgraded_blast_furnace", () -> BlockEntityType.Builder.of(UpgradedBlastFurnaceBlockEntity::new, Registry.UPGRADED_BLAST_FURNACE.get()).build(null));
    // endregion

    // region menus
    public static final RegistryObject<MenuType<UpgradedBlastFurnaceMenu>> UPGRADED_BLAST_FURNACE_MENU = registerMenu("upgraded_blast_furnace", () -> new MenuType<>(UpgradedBlastFurnaceMenu::new));
    // endregion

    // region shorthand methods
    private static RegistryObject<Item> basicItem(String id) {
        return ITEM_REGISTER.register(id, () -> new Item(new Item.Properties().tab(MAIN_TAB)));
    }

    private static RegistryObject<Item> basicFireImmuneItem(String id) {
        return ITEM_REGISTER.register(id, () -> new Item(new Item.Properties().tab(MAIN_TAB).fireResistant()));
    }

    private static RegistryObject<Item> basicEssenceItem(String id) {
        return ITEM_REGISTER.register(id, () -> new Item(new Item.Properties().fireResistant()));
    }

    private static RegistryObject<Item> registerItem(String id, Supplier<Item> supplier) {
        return ITEM_REGISTER.register(id, supplier);
    }

    public static RegistryObject<Item> registerBlockItem(RegistryObject<Block> block, Item.Properties properties) {
        return ITEM_REGISTER.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
    }

    public static RegistryObject<Item> registerBlockItem(RegistryObject<Block> block) {
        return registerBlockItem(block, new Item.Properties());
    }

    public static RegistryObject<Block> registerBlock(String id, Supplier<Block> supplier) {
        return BLOCK_REGISTER.register(id, supplier);
    }

    public static RegistryObject<Enchantment> registerEnchant(String id, Supplier<Enchantment> supplier) {
        return ENCHANT_REGISTER.register(id, supplier);
    }

    public static <T extends Recipe<?>> RegistryObject<RecipeSerializer<T>> registerRecipeSerializer(String id, Supplier<RecipeSerializer<T>> supplier) {
        return RECIPE_REGISTER.register(id, supplier);
    }

    public static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String id, Supplier<EntityType.Builder<T>> supplier) {
        return ENTITY_REGISTER.register(id, () -> supplier.get().build(id));
    }

    public static RegistryObject<Potion> registerPotion(String id, Supplier<Potion> supplier) {
        return POTION_REGISTER.register(id, supplier);
    }

    public static RegistryObject<MobEffect> registerEffect(String id, Supplier<MobEffect> supplier) {
        return EFFECT_REGISTER.register(id, supplier);
    }

    public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(String id, Supplier<BlockEntityType<T>> supplier) {
        return BLOCK_ENTITY_REGISTER.register(id, supplier);
    }

    public static <T extends AbstractFurnaceMenu> RegistryObject<MenuType<T>> registerMenu(String id, Supplier<MenuType<T>> supplier) {
        return MENU_REGISTER.register(id, supplier);
    }

    public static void register(IEventBus bus) {
        for (DeferredRegister<?> register : REGISTERS) {
            register.register(bus);
        }
    }
    // endregion
}
