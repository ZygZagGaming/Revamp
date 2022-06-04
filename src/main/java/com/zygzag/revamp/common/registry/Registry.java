package com.zygzag.revamp.common.registry;

import com.mojang.serialization.Codec;
import com.zygzag.revamp.common.block.*;
import com.zygzag.revamp.common.block.entity.ChargeDetectorBlockEntity;
import com.zygzag.revamp.common.block.entity.UpgradedBlastFurnaceBlockEntity;
import com.zygzag.revamp.common.block.menu.UpgradedBlastFurnaceMenu;
import com.zygzag.revamp.common.entity.*;
import com.zygzag.revamp.common.entity.effect.SightEffect;
import com.zygzag.revamp.common.item.EmpowermentStar;
import com.zygzag.revamp.common.item.EnchantedBowlFoodItem;
import com.zygzag.revamp.common.item.ShulkerBowlItem;
import com.zygzag.revamp.common.item.TransmutationCharge;
import com.zygzag.revamp.common.item.enchant.*;
import com.zygzag.revamp.common.item.iridium.*;
import com.zygzag.revamp.common.item.iridium.partial.*;
import com.zygzag.revamp.common.item.recipe.*;
import com.zygzag.revamp.common.item.tier.IridiumArmorMaterial;
import com.zygzag.revamp.common.item.tier.IridiumToolTier;
import com.zygzag.revamp.common.loot.AutosmeltModifier;
import com.zygzag.revamp.common.loot.ExecutionerModifier;
import com.zygzag.revamp.common.misc.RuleSource2;
import com.zygzag.revamp.common.tag.RevampTags;
import com.zygzag.revamp.common.world.PlatformFungusConfiguration;
import com.zygzag.revamp.common.world.feature.BetterFortressFeature;
import com.zygzag.revamp.common.world.feature.PlatformFungusFeature;
import com.zygzag.revamp.util.Constants;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
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
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static com.zygzag.revamp.common.Revamp.MAIN_TAB;
import static com.zygzag.revamp.common.Revamp.MODID;

@SuppressWarnings("unused")
public class Registry {
    private static final List<DeferredRegister<?>> REGISTERS = List.of(
            EntityRegistry.REGISTER,
            ItemRegistry.REGISTER,
            IridiumGearRegistry.REGISTER,
            BlockRegistry.REGISTER,
            RecipeSerializerRegistry.REGISTER,
            EnchantmentRegistry.REGISTER,
            PotionRegistry.REGISTER,
            GlobalLootModifierSerializerRegistry.REGISTER,
            MobEffectRegistry.REGISTER,
            BlockEntityTypeRegistry.REGISTER,
            MenuTypeRegistry.REGISTER,
            FeatureRegistry.REGISTER,
            ConfiguredFeatureRegistry.REGISTER,
            PlacedFeatureRegistry.REGISTER,
            StructureFeatureRegistry.REGISTER,
            ConfiguredStructureFeatureRegistry.REGISTER,
            BiomeRegistry.REGISTER,
            BiomeSourceRegistry.REGISTER,
            RuleSourceRegistry.REGISTER,
            EntityDataSerializerRegistry.REGISTER,
            ParticleTypeRegistry.REGISTER
    );

    public static class BlockRegistry {
        private static final DeferredRegister<Block> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
        public static final RegistryObject<Block> LAVA_VINES_BLOCK = registerBlock("lava_vines", () -> new LavaVinesBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2f).sound(SoundType.NETHER_SPROUTS)));

        public static final RegistryObject<Block> MAGMA_FUNGUS_BLOCK = registerBlock("magma_fungus_block", () -> new MagmaFungusBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2f).sound(SoundType.STEM)));
        public static final RegistryObject<Block> MAGMA_FUNGUS_CAP_BLOCK = registerBlock("magma_fungus_cap", () -> new MagmaFungusBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2f).sound(SoundType.STEM)));
        public static final RegistryObject<Block> MAGMA_FUNGUS_EDGE_BLOCK = registerBlock("magma_fungus_edge", () -> new MagmaFungusBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2f).sound(SoundType.STEM)));
        public static final RegistryObject<Block> MAGMA_FUNGUS_STEM_BLOCK = registerBlock("magma_fungus_stem", () -> new MagmaFungusStemBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2f).sound(SoundType.STEM)));

        public static final RegistryObject<Block> MAGMA_PUSTULE_BLOCK = registerBlock("magma_pustule", () -> new MagmaPustuleBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(0f).sound(SoundType.NETHER_SPROUTS)));

        public static final RegistryObject<Block> IRIDIUM_ORE = registerBlock("iridium_ore", IridiumOreBlock::new);
        public static final RegistryObject<Block> DEEPSLATE_IRIDIUM_ORE = registerBlock("deepslate_iridium_ore", () -> new IridiumOreBlock(BlockBehaviour.Properties.copy(IRIDIUM_ORE.get()).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
        public static final RegistryObject<Block> RAW_IRIDIUM_BLOCK = registerBlock("raw_iridium_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE)));
        //public static final RegistryObject<Block> IRIDIUM_GRATING = registerBlock("iridium_grating", () -> new GrateBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(1f, 6f).sound(SoundType.METAL)));
        public static final RegistryObject<Block> BLESSED_SOIL = registerBlock("blessed_soil", () -> new BlessedSoilBlock(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
        public static final RegistryObject<Block> UPGRADED_BLAST_FURNACE = registerBlock("upgraded_blast_furnace", () -> new UpgradedBlastFurnace(BlockBehaviour.Properties.copy(Blocks.BLAST_FURNACE)));
        public static final RegistryObject<Block> OSTEUM = registerBlock("osteum", () -> new OsteumBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK)));
        public static final RegistryObject<Block> GROWING_OSTEUM = registerBlock("growing_osteum", () -> new GrowingOsteumBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK)));

        public static final RegistryObject<Block> MAGMA_MYCELIUM_BLOCK = registerBlock("magma_mycelium", () -> new MagmaMyceliumBlock(BlockBehaviour.Properties.copy(Blocks.NETHERRACK)));

        public static final RegistryObject<Block> CHARGE_CRYSTAL_BLOCK_NEGATIVE = registerBlock("charge_crystal_negative", () -> new ChargeCrystalBlock(BlockBehaviour.Properties.of(Material.AMETHYST), -20f));
        public static final RegistryObject<Block> CHARGE_CRYSTAL_BLOCK_POSITIVE = registerBlock("charge_crystal_positive", () -> new ChargeCrystalBlock(BlockBehaviour.Properties.of(Material.AMETHYST), 20f));
        public static final RegistryObject<Block> CHARGE_DETECTOR = registerBlock("charge_detector", () -> new ChargeDetectorBlock(BlockBehaviour.Properties.of(Material.DECORATION).instabreak().sound(SoundType.WOOD)));
        public static final RegistryObject<Block> ARC_CRYSTAL = registerBlock("arc_crystal", () -> new ArcCrystalBlock(BlockBehaviour.Properties.of(Material.AMETHYST)));

        public static RegistryObject<Block> registerBlock(String id, Supplier<Block> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class ItemRegistry {
        private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
        public static final RegistryObject<Item> LAVA_VINES_ITEM = registerBlockItem(BlockRegistry.LAVA_VINES_BLOCK);

        public static final RegistryObject<Item> REVAMPED_BLAZE_SPAWN_EGG = registerItem("revamped_blaze_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.REVAMPED_BLAZE, 16167425, 16775294, (new Item.Properties()).tab(CreativeModeTab.TAB_MISC)));

        public static final RegistryObject<Item> OSTEUM_ITEM = registerBlockItem(BlockRegistry.OSTEUM, new Item.Properties().tab(MAIN_TAB));
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

        public static final RegistryObject<Item> MAGMA_FUNGUS_ITEM = registerBlockItem(BlockRegistry.MAGMA_FUNGUS_BLOCK);
        public static final RegistryObject<Item> MAGMA_FUNGUS_CAP_ITEM = registerBlockItem(BlockRegistry.MAGMA_FUNGUS_CAP_BLOCK);
        public static final RegistryObject<Item> MAGMA_FUNGUS_EDGE_ITEM = registerBlockItem(BlockRegistry.MAGMA_FUNGUS_EDGE_BLOCK);
        public static final RegistryObject<Item> MAGMA_FUNGUS_STEM_ITEM = registerBlockItem(BlockRegistry.MAGMA_FUNGUS_STEM_BLOCK);

        public static final RegistryObject<Item> MAGMA_PUSTULE_ITEM = registerBlockItem(BlockRegistry.MAGMA_PUSTULE_BLOCK);

        public static final RegistryObject<Item> IRIDIUM_ORE_ITEM = registerBlockItem(BlockRegistry.IRIDIUM_ORE, new Item.Properties().tab(MAIN_TAB));
        public static final RegistryObject<Item> DEEPSLATE_IRIDIUM_ORE_ITEM = registerBlockItem(BlockRegistry.DEEPSLATE_IRIDIUM_ORE, new Item.Properties().tab(MAIN_TAB));
        public static final RegistryObject<Item> RAW_IRIDIUM_BLOCK_ITEM = registerBlockItem(BlockRegistry.RAW_IRIDIUM_BLOCK, new Item.Properties().tab(MAIN_TAB));
        //public static final RegistryObject<Item> IRIDIUM_GRATING_ITEM = registerBlockItem(BlockRegistry.IRIDIUM_GRATING, new Item.Properties().tab(Revamp.MAIN_TAB));
        public static final RegistryObject<Item> BLESSED_SOIL_ITEM = registerBlockItem(BlockRegistry.BLESSED_SOIL, new Item.Properties().tab(MAIN_TAB));
        public static final RegistryObject<Item> UPGRADED_BLAST_FURNACE_ITEM = registerBlockItem(BlockRegistry.UPGRADED_BLAST_FURNACE, new Item.Properties().tab(MAIN_TAB));

        public static final RegistryObject<Item> MAGMA_MYCELIUM_ITEM = registerBlockItem(BlockRegistry.MAGMA_MYCELIUM_BLOCK);

        public static final RegistryObject<Item> CHARGE_CRYSTAL_NEGATIVE_ITEM = registerBlockItem(BlockRegistry.CHARGE_CRYSTAL_BLOCK_NEGATIVE);
        public static final RegistryObject<Item> CHARGE_CRYSTAL_POSITIVE_ITEM = registerBlockItem(BlockRegistry.CHARGE_CRYSTAL_BLOCK_POSITIVE);
        public static final RegistryObject<Item> CHARGE_DETECTOR_ITEM = registerBlockItem(BlockRegistry.CHARGE_DETECTOR);
        public static final RegistryObject<Item> ARC_CRYSTAL_ITEM = registerBlockItem(BlockRegistry.ARC_CRYSTAL);

        private static RegistryObject<Item> basicItem(String id) {
            return REGISTER.register(id, () -> new Item(new Item.Properties().tab(MAIN_TAB)));
        }

        private static RegistryObject<Item> basicFireImmuneItem(String id) {
            return REGISTER.register(id, () -> new Item(new Item.Properties().tab(MAIN_TAB).fireResistant()));
        }

        private static RegistryObject<Item> basicEssenceItem(String id) {
            return REGISTER.register(id, () -> new Item(new Item.Properties().fireResistant()));
        }

        private static RegistryObject<Item> registerItem(String id, Supplier<Item> supplier) {
            return REGISTER.register(id, supplier);
        }

        public static RegistryObject<Item> registerBlockItem(RegistryObject<Block> block, Item.Properties properties) {
            return REGISTER.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties));
        }

        public static RegistryObject<Item> registerBlockItem(RegistryObject<Block> block) {
            return registerBlockItem(block, new Item.Properties().tab(MAIN_TAB));
        }
    }

    public static class IridiumGearRegistry {
        private static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
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

        public static final RegistryObject<Item> DIAMOND_SOCKETED_IRIDIUM_SWORD = registerItem("diamond_socketed_iridium_sword", () -> new IridiumSwordItem(IridiumToolTier.DIAMOND_SOCKETED, 3, -2.4F, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.DIAMOND), Socket.DIAMOND));
        public static final RegistryObject<Item> DIAMOND_SOCKETED_IRIDIUM_SHOVEL = registerItem("diamond_socketed_iridium_shovel", () -> new IridiumShovelItem(IridiumToolTier.DIAMOND_SOCKETED, 1.5F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.DIAMOND), Socket.DIAMOND));
        public static final RegistryObject<Item> DIAMOND_SOCKETED_IRIDIUM_PICKAXE = registerItem("diamond_socketed_iridium_pickaxe", () -> new IridiumPickaxeItem(IridiumToolTier.DIAMOND_SOCKETED_PICK, 1, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.DIAMOND), Socket.DIAMOND));
        public static final RegistryObject<Item> DIAMOND_SOCKETED_IRIDIUM_AXE = registerItem("diamond_socketed_iridium_axe", () -> new IridiumAxeItem(IridiumToolTier.DIAMOND_SOCKETED, 5.0F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.DIAMOND), Socket.DIAMOND));
        public static final RegistryObject<Item> DIAMOND_SOCKETED_IRIDIUM_HOE = registerItem("diamond_socketed_iridium_hoe", () -> new IridiumHoeItem(IridiumToolTier.DIAMOND_SOCKETED, -1, 0.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.DIAMOND), Socket.DIAMOND));

        public static final RegistryObject<Item> DIAMOND_SOCKETED_IRIDIUM_CHESTPLATE = registerItem("diamond_socketed_iridium_chestplate", () -> new IridiumChestplateItem(IridiumArmorMaterial.DIAMOND_SOCKETED, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.DIAMOND), Socket.DIAMOND));

        public static final RegistryObject<Item> DIAMOND_SOCKETED_IRIDIUM_SCEPTER = registerItem("diamond_socketed_iridium_scepter", () -> new IridiumScepterItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(IRIDIUM_SCEPTER.get()).craftRemainder(Items.DIAMOND), Socket.DIAMOND));

        public static final RegistryObject<Item> EMERALD_SOCKETED_IRIDIUM_SWORD = registerItem("emerald_socketed_iridium_sword", () -> new IridiumSwordItem(IridiumToolTier.EMERALD_SOCKETED, 3, -2.4F, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.EMERALD), Socket.EMERALD));
        public static final RegistryObject<Item> EMERALD_SOCKETED_IRIDIUM_SHOVEL = registerItem("emerald_socketed_iridium_shovel", () -> new IridiumShovelItem(IridiumToolTier.EMERALD_SOCKETED, 1.5F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.EMERALD), Socket.EMERALD));
        public static final RegistryObject<Item> EMERALD_SOCKETED_IRIDIUM_PICKAXE = registerItem("emerald_socketed_iridium_pickaxe", () -> new IridiumPickaxeItem(IridiumToolTier.EMERALD_SOCKETED, 1, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.EMERALD), Socket.EMERALD));
        public static final RegistryObject<Item> EMERALD_SOCKETED_IRIDIUM_AXE = registerItem("emerald_socketed_iridium_axe", () -> new IridiumAxeItem(IridiumToolTier.EMERALD_SOCKETED, 5.0F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.EMERALD), Socket.EMERALD));
        public static final RegistryObject<Item> EMERALD_SOCKETED_IRIDIUM_HOE = registerItem("emerald_socketed_iridium_hoe", () -> new IridiumHoeItem(IridiumToolTier.EMERALD_SOCKETED, -1, 0.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.EMERALD), Socket.EMERALD));

        public static final RegistryObject<Item> EMERALD_SOCKETED_IRIDIUM_CHESTPLATE = registerItem("emerald_socketed_iridium_chestplate", () -> new IridiumChestplateItem(IridiumArmorMaterial.EMERALD_SOCKETED, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.EMERALD), Socket.EMERALD));

        public static final RegistryObject<Item> EMERALD_SOCKETED_IRIDIUM_SCEPTER = registerItem("emerald_socketed_iridium_scepter", () -> new IridiumScepterItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.EMERALD), Socket.EMERALD));

        public static final RegistryObject<Item> SKULL_SOCKETED_IRIDIUM_SWORD = registerItem("skull_socketed_iridium_sword", () -> new IridiumSwordItem(IridiumToolTier.FULL, 3, -2.4F, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.SKELETON_SKULL), Socket.SKULL));
        public static final RegistryObject<Item> SKULL_SOCKETED_IRIDIUM_SHOVEL = registerItem("skull_socketed_iridium_shovel", () -> new IridiumShovelItem(IridiumToolTier.FULL, 1.5F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.SKELETON_SKULL), Socket.SKULL));
        public static final RegistryObject<Item> SKULL_SOCKETED_IRIDIUM_PICKAXE = registerItem("skull_socketed_iridium_pickaxe", () -> new IridiumPickaxeItem(IridiumToolTier.FULL, 1, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.SKELETON_SKULL), Socket.SKULL));
        public static final RegistryObject<Item> SKULL_SOCKETED_IRIDIUM_AXE = registerItem("skull_socketed_iridium_axe", () -> new IridiumAxeItem(IridiumToolTier.FULL, 5.0F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.SKELETON_SKULL), Socket.SKULL));
        public static final RegistryObject<Item> SKULL_SOCKETED_IRIDIUM_HOE = registerItem("skull_socketed_iridium_hoe", () -> new IridiumHoeItem(IridiumToolTier.FULL, -1, 0.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.SKELETON_SKULL), Socket.SKULL));

        public static final RegistryObject<Item> SKULL_SOCKETED_IRIDIUM_CHESTPLATE = registerItem("skull_socketed_iridium_chestplate", () -> new IridiumChestplateItem(IridiumArmorMaterial.IRIDIUM, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.SKELETON_SKULL), Socket.SKULL));

        public static final RegistryObject<Item> SKULL_SOCKETED_IRIDIUM_SCEPTER = registerItem("skull_socketed_iridium_scepter", () -> new IridiumScepterItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.SKELETON_SKULL), Socket.SKULL));

        public static final RegistryObject<Item> WITHER_SKULL_SOCKETED_IRIDIUM_SWORD = registerItem("wither_skull_socketed_iridium_sword", () -> new IridiumSwordItem(IridiumToolTier.FULL, 3, -2.4F, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.WITHER_SKELETON_SKULL), Socket.WITHER_SKULL));
        public static final RegistryObject<Item> WITHER_SKULL_SOCKETED_IRIDIUM_SHOVEL = registerItem("wither_skull_socketed_iridium_shovel", () -> new IridiumShovelItem(IridiumToolTier.FULL, 1.5F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.WITHER_SKELETON_SKULL), Socket.WITHER_SKULL));
        public static final RegistryObject<Item> WITHER_SKULL_SOCKETED_IRIDIUM_PICKAXE = registerItem("wither_skull_socketed_iridium_pickaxe", () -> new IridiumPickaxeItem(IridiumToolTier.WITHER_SOCKETED_PICK, 1, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.WITHER_SKELETON_SKULL), Socket.WITHER_SKULL));
        public static final RegistryObject<Item> WITHER_SKULL_SOCKETED_IRIDIUM_AXE = registerItem("wither_skull_socketed_iridium_axe", () -> new IridiumAxeItem(IridiumToolTier.FULL, 5.0F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).craftRemainder(Items.WITHER_SKELETON_SKULL)
                .fireResistant().stacksTo(1), Socket.WITHER_SKULL));
        public static final RegistryObject<Item> WITHER_SKULL_SOCKETED_IRIDIUM_HOE = registerItem("wither_skull_socketed_iridium_hoe", () -> new IridiumHoeItem(IridiumToolTier.FULL, 6, 0.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.WITHER_SKELETON_SKULL), Socket.WITHER_SKULL));

        public static final RegistryObject<Item> WITHER_SKULL_SOCKETED_IRIDIUM_CHESTPLATE = registerItem("wither_skull_socketed_iridium_chestplate", () -> new IridiumChestplateItem(IridiumArmorMaterial.IRIDIUM, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.WITHER_SKELETON_SKULL), Socket.WITHER_SKULL));

        public static final RegistryObject<Item> WITHER_SKULL_SOCKETED_IRIDIUM_SCEPTER = registerItem("wither_skull_socketed_iridium_scepter", () -> new IridiumScepterItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.WITHER_SKELETON_SKULL), Socket.WITHER_SKULL));

        public static final RegistryObject<Item> AMETHYST_SOCKETED_IRIDIUM_SWORD = registerItem("amethyst_socketed_iridium_sword", () -> new IridiumSwordItem(IridiumToolTier.FULL, 3, -2.4F, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.AMETHYST_SHARD), Socket.AMETHYST));
        public static final RegistryObject<Item> AMETHYST_SOCKETED_IRIDIUM_SHOVEL = registerItem("amethyst_socketed_iridium_shovel", () -> new IridiumShovelItem(IridiumToolTier.FULL, 1.5F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.AMETHYST_SHARD), Socket.AMETHYST));
        public static final RegistryObject<Item> AMETHYST_SOCKETED_IRIDIUM_PICKAXE = registerItem("amethyst_socketed_iridium_pickaxe", () -> new IridiumPickaxeItem(IridiumToolTier.FULL, 1, -2.8F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.AMETHYST_SHARD), Socket.AMETHYST));
        public static final RegistryObject<Item> AMETHYST_SOCKETED_IRIDIUM_AXE = registerItem("amethyst_socketed_iridium_axe", () -> new IridiumAxeItem(IridiumToolTier.FULL, 5.0F, -3.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).craftRemainder(Items.AMETHYST_SHARD)
                .fireResistant().stacksTo(1), Socket.AMETHYST));
        public static final RegistryObject<Item> AMETHYST_SOCKETED_IRIDIUM_HOE = registerItem("amethyst_socketed_iridium_hoe", () -> new IridiumHoeItem(IridiumToolTier.FULL, -1, 0.0F, (new Item.Properties()).tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.AMETHYST_SHARD), Socket.AMETHYST));

        public static final RegistryObject<Item> AMETHYST_SOCKETED_IRIDIUM_CHESTPLATE = registerItem("amethyst_socketed_iridium_chestplate", () -> new IridiumChestplateItem(IridiumArmorMaterial.IRIDIUM, (new Item.Properties()).tab(CreativeModeTab.TAB_COMBAT).fireResistant().stacksTo(1).craftRemainder(Items.AMETHYST_SHARD), Socket.AMETHYST));

        public static final RegistryObject<Item> AMETHYST_SOCKETED_IRIDIUM_SCEPTER = registerItem("amethyst_socketed_iridium_scepter", () -> new IridiumScepterItem(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).fireResistant().stacksTo(1).craftRemainder(Items.AMETHYST_SHARD), Socket.AMETHYST));

        public static final RegistryObject<Item> PARTIAL_IRIDIUM_SWORD_2_1 = registerItem("partial_iridium_sword_1", () -> new PartialIridiumSword(IridiumToolTier.HALF, 3, -2.4F, (new Item.Properties().stacksTo(1)), 2, 1));
        public static final RegistryObject<Item> PARTIAL_IRIDIUM_PICKAXE_3_1 = registerItem("partial_iridium_pickaxe_1", () -> new PartialIridiumPickaxe(IridiumToolTier._3_1, 1, -2.8F, (new Item.Properties().stacksTo(1)), 3, 1));
        public static final RegistryObject<Item> PARTIAL_IRIDIUM_PICKAXE_3_2 = registerItem("partial_iridium_pickaxe_2", () -> new PartialIridiumPickaxe(IridiumToolTier._3_2, 1, -2.8F, (new Item.Properties().stacksTo(1)), 3, 2));
        public static final RegistryObject<Item> PARTIAL_IRIDIUM_AXE_4_1 = registerItem("partial_iridium_axe_1", () -> new PartialIridiumAxe(IridiumToolTier._3_1, 5, -3.0F, (new Item.Properties().stacksTo(1)), 3, 1));
        public static final RegistryObject<Item> PARTIAL_IRIDIUM_AXE_4_2 = registerItem("partial_iridium_axe_2", () -> new PartialIridiumAxe(IridiumToolTier._3_2, 5, -3.0F, (new Item.Properties().stacksTo(1)), 3, 2));
        public static final RegistryObject<Item> PARTIAL_IRIDIUM_HOE_2_1 = registerItem("partial_iridium_hoe_1", () -> new PartialIridiumHoe(IridiumToolTier.HALF, -1, 0.0F, (new Item.Properties()).fireResistant().stacksTo(1), 2, 1));

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

        private static RegistryObject<Item> registerItem(String id, Supplier<Item> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class EnchantmentRegistry {
        private static final DeferredRegister<Enchantment> REGISTER = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);
        public static final RegistryObject<Enchantment> COOLDOWN_ENCHANTMENT = registerEnchant("cooldown", () -> new CooldownEnchantment(Enchantment.Rarity.RARE));
        public static final RegistryObject<Enchantment> SURGE_PROTECTOR_ENCHANTMENT = registerEnchant("surge_protector", SurgeProtectorEnchantment::new);
        public static final RegistryObject<VoltageEnchantment> VOLTAGE_PLUS_ENCHANTMENT = registerEnchant("voltage_plus", () -> new VoltageEnchantment(Constants.VOLTAGE_ENCHANTMENT_CHARGE_PER_TICK));
        public static final RegistryObject<VoltageEnchantment> VOLTAGE_MINUS_ENCHANTMENT = registerEnchant("voltage_minus", () -> new VoltageEnchantment(-Constants.VOLTAGE_ENCHANTMENT_CHARGE_PER_TICK));
        public static final RegistryObject<DynamoEnchantment> DYNAMO_ENCHANTMENT = registerEnchant("dynamo", DynamoEnchantment::new);
        public static final RegistryObject<GroundedEnchantment> GROUNDED_ENCHANTMENT = registerEnchant("grounded", GroundedEnchantment::new);
        public static <T extends Enchantment> RegistryObject<T> registerEnchant(String id, Supplier<T> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class RecipeSerializerRegistry {
        private static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
        public static final RegistryObject<RecipeSerializer<ShulkerBowlRecipe>> SHULKER_BOWL_CRAFTING = registerRecipeSerializer("crafting_special_shulker_bowl", () -> new SimpleRecipeSerializer<>(ShulkerBowlRecipe::new));
        public static final RegistryObject<RecipeSerializer<TransmutationRecipe>> TRANSMUTATION_SERIALIZER = registerRecipeSerializer("transmutation", TransmutationRecipe.TransmutationSerializer::new);
        public static final RegistryObject<RecipeSerializer<EmpowermentRecipe>> EMPOWERMENT_SERIALIZER = registerRecipeSerializer("empowerment", EmpowermentRecipe.EmpowermentSerializer::new);
        public static final RegistryObject<RecipeSerializer<SocketRemoveRecipe>> SOCKET_REMOVE_CRAFTING = registerRecipeSerializer("crafting_special_socket_remove", () -> new SimpleRecipeSerializer<>(SocketRemoveRecipe::new));
        public static final RegistryObject<RecipeSerializer<UpgradedBlastFurnaceRecipe>> UPGRADED_BLAST_FURNACE_SERIALIZER = registerRecipeSerializer("upgraded_blasting", UpgradedBlastFurnaceRecipe.UpgradedBlastingSerializer::new);

        public static <T extends Recipe<?>> RegistryObject<RecipeSerializer<T>> registerRecipeSerializer(String id, Supplier<RecipeSerializer<T>> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class EntityRegistry {
        private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);
        public static final RegistryObject<EntityType<EmpoweredWither>> EMPOWERED_WITHER = registerEntity("empowered_wither", () ->
                EntityType.Builder.of(EmpoweredWither::new, MobCategory.MONSTER)
                        .fireImmune()
                        .immuneTo(Blocks.WITHER_ROSE)
                        .sized(1.1F, 3.75F)
                        .clientTrackingRange(10));
        public static final RegistryObject<EntityType<RevampedBlaze>> REVAMPED_BLAZE = registerEntity("revamped_blaze", () ->
                EntityType.Builder.of(RevampedBlaze::new, MobCategory.MONSTER)
                        .fireImmune()
                        .sized(0.8F, 1.85F)
                        .clientTrackingRange(10));

        public static final RegistryObject<EntityType<HomingWitherSkull>> HOMING_WITHER_SKULL = registerEntity("homing_wither_skull", () -> EntityType.Builder.<HomingWitherSkull>of(HomingWitherSkull::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4));
        public static final RegistryObject<EntityType<ThrownTransmutationCharge>> TRANSMUTATION_BOTTLE_ENTITY = registerEntity("transmutation_bottle", () -> EntityType.Builder.<ThrownTransmutationCharge>of(ThrownTransmutationCharge::new, MobCategory.MISC).sized(0.25f, 0.25f));
        public static final RegistryObject<EntityType<ThrownAxe>> THROWN_AXE = registerEntity("thrown_axe", () -> EntityType.Builder.of(ThrownAxe::new, MobCategory.MISC));

        public static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String id, Supplier<EntityType.Builder<T>> supplier) {
            return REGISTER.register(id, () -> supplier.get().build(id));
        }
    }

    public static class MobEffectRegistry {
        private static final DeferredRegister<MobEffect> REGISTER = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
        public static RegistryObject<MobEffect> SIGHT_EFFECT = registerEffect("sight", () -> new SightEffect(MobEffectCategory.BENEFICIAL, Constants.SIGHT_EFFECT_COLOR, (b) -> b.is(Tags.Blocks.ORES), GeneralUtil::getColor));
        public static RegistryObject<MobEffect> GREEN_THUMB_EFFECT = registerEffect("green_thumb", () -> new SightEffect(MobEffectCategory.BENEFICIAL, Constants.SIGHT_EFFECT_COLOR, (b) -> (b.getBlock() instanceof FarmBlock && b.hasProperty(FarmBlock.MOISTURE) && b.getValue(FarmBlock.MOISTURE) != 7) || (b.getBlock() instanceof CropBlock c && c.isMaxAge(b)), (b) -> b.getBlock() instanceof CropBlock ? Constants.CROP_COLOR : Constants.UNWATERED_SOIL_HIGHLIGHT_COLOR));
        public static RegistryObject<MobEffect> REACH_EFFECT = registerEffect("reach", () -> new MobEffect(MobEffectCategory.BENEFICIAL, Constants.REACH_EFFECT_COLOR).addAttributeModifier(ForgeMod.REACH_DISTANCE.get(), UUID.randomUUID().toString(), 2.0, AttributeModifier.Operation.ADDITION));

        public static RegistryObject<MobEffect> registerEffect(String id, Supplier<MobEffect> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class PotionRegistry {
        private static final DeferredRegister<Potion> REGISTER = DeferredRegister.create(ForgeRegistries.POTIONS, MODID);
        public static final RegistryObject<Potion> RAGE_POTION = registerPotion("rage", () -> new Potion("rage", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 800, 1), new MobEffectInstance(MobEffects.DAMAGE_BOOST, 800, 1)));
        public static final RegistryObject<Potion> LONG_RAGE_POTION = registerPotion("long_rage", () -> new Potion("long_rage", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1600, 1), new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1600, 1)));
        public static final RegistryObject<Potion> STRONG_RAGE_POTION = registerPotion("strong_rage", () -> new Potion("strong_rage", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 750, 2), new MobEffectInstance(MobEffects.DAMAGE_BOOST, 750, 2)));

        public static final RegistryObject<Potion> SIGHT_POTION = registerPotion("sight", () -> new Potion("sight", new MobEffectInstance(MobEffectRegistry.SIGHT_EFFECT.get(), 100, 0)));
        public static final RegistryObject<Potion> LONG_SIGHT_POTION = registerPotion("long_sight", () -> new Potion("long_sight", new MobEffectInstance(MobEffectRegistry.SIGHT_EFFECT.get(), 200, 0)));
        public static final RegistryObject<Potion> STRONG_SIGHT_POTION = registerPotion("strong_sight", () -> new Potion("strong_sight", new MobEffectInstance(MobEffectRegistry.SIGHT_EFFECT.get(), 100, 1)));

        public static RegistryObject<Potion> registerPotion(String id, Supplier<Potion> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class GlobalLootModifierSerializerRegistry {
        private static final DeferredRegister<GlobalLootModifierSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.Keys.LOOT_MODIFIER_SERIALIZERS, MODID);
        public static RegistryObject<GlobalLootModifierSerializer<?>> EXECUTIONER_LOOT_MOD = register("executioner", ExecutionerModifier.Serializer::new);
        public static RegistryObject<GlobalLootModifierSerializer<?>> AUTOSMELT_LOOT_MOD = register("autosmelt", AutosmeltModifier.Serializer::new);

        private static RegistryObject<GlobalLootModifierSerializer<?>> register(String id, Supplier<GlobalLootModifierSerializer<?>> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class BlockEntityTypeRegistry {
        private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
        public static RegistryObject<BlockEntityType<UpgradedBlastFurnaceBlockEntity>> UPGRADED_BLAST_FURNACE_BLOCK_ENTITY = registerBlockEntity("upgraded_blast_furnace", () -> BlockEntityType.Builder.of(UpgradedBlastFurnaceBlockEntity::new, BlockRegistry.UPGRADED_BLAST_FURNACE.get()).build(null));
        public static RegistryObject<BlockEntityType<ChargeDetectorBlockEntity>> CHARGE_DETECTOR_BLOCK_ENTITY = registerBlockEntity("charge_detector", () -> BlockEntityType.Builder.of(ChargeDetectorBlockEntity::new, BlockRegistry.CHARGE_DETECTOR.get()).build(null));

        public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(String id, Supplier<BlockEntityType<T>> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class MenuTypeRegistry {
        private static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);
        public static final RegistryObject<MenuType<UpgradedBlastFurnaceMenu>> UPGRADED_BLAST_FURNACE_MENU = registerMenu("upgraded_blast_furnace", () -> new MenuType<>(UpgradedBlastFurnaceMenu::new));

        public static <T extends AbstractFurnaceMenu> RegistryObject<MenuType<T>> registerMenu(String id, Supplier<MenuType<T>> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class FeatureRegistry {
        private static final DeferredRegister<Feature<?>> REGISTER = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
        public static final RegistryObject<Feature<PlatformFungusConfiguration>> PLATFORM_FUNGUS_FEATURE = registerFeature("platform_fungus", () -> new PlatformFungusFeature(PlatformFungusConfiguration.CODEC));

        public static <F extends Feature<FC>, FC extends FeatureConfiguration> RegistryObject<F> registerFeature(String id, Supplier<F> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class StructureFeatureRegistry {
        private static final DeferredRegister<StructureFeature<?>> REGISTER = DeferredRegister.create(net.minecraft.core.Registry.STRUCTURE_FEATURE_REGISTRY, MODID);
        public static final RegistryObject<BetterFortressFeature> BETTER_FORTRESS = registerStructureFeature("better_fortress", () -> new BetterFortressFeature(NoneFeatureConfiguration.CODEC));

        public static <S extends StructureFeature<FC>, FC extends FeatureConfiguration> RegistryObject<S> registerStructureFeature(String id, Supplier<S> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class ConfiguredFeatureRegistry {
        private static final DeferredRegister<ConfiguredFeature<?, ?>> REGISTER = DeferredRegister.create(net.minecraft.core.Registry.CONFIGURED_FEATURE_REGISTRY, MODID);
        public static final Lazy<List<OreConfiguration.TargetBlockState>> ORE_IRIDIUM_TARGET_LIST = Lazy.of(() -> List.of(OreConfiguration.target(OreFeatures.STONE_ORE_REPLACEABLES, BlockRegistry.IRIDIUM_ORE.get().defaultBlockState()), OreConfiguration.target(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, BlockRegistry.DEEPSLATE_IRIDIUM_ORE.get().defaultBlockState())));

        public static final RegistryObject<ConfiguredFeature<?, ?>> IRIDIUM_SMALL_CONFIGURED = registerConfiguredFeature("ore_iridium_small", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ORE_IRIDIUM_TARGET_LIST.get(), 16, 0.4f)));
        public static final RegistryObject<ConfiguredFeature<?, ?>> IRIDIUM_LARGE_CONFIGURED = registerConfiguredFeature("ore_iridium_large", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ORE_IRIDIUM_TARGET_LIST.get(), 24, 0.6f)));
        public static final RegistryObject<ConfiguredFeature<?, ?>> IRIDIUM_BURIED_CONFIGURED = registerConfiguredFeature("ore_iridium_buried", () -> new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(ORE_IRIDIUM_TARGET_LIST.get(), 20, 1f)));

        public static final RegistryObject<ConfiguredFeature<?, ?>> PLATFORM_FUNGUS_CONFIGURED = registerConfiguredFeature("platform_fungus", () -> new ConfiguredFeature<>(FeatureRegistry.PLATFORM_FUNGUS_FEATURE.get(), PlatformFungusConfiguration.defaultConfig()));

        public static <C extends ConfiguredFeature<FC, F>, FC extends FeatureConfiguration, F extends Feature<FC>> RegistryObject<ConfiguredFeature<?, ?>> registerConfiguredFeature(String id, Supplier<C> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class ConfiguredStructureFeatureRegistry {
        private static final DeferredRegister<ConfiguredStructureFeature<?, ?>> REGISTER = DeferredRegister.create(net.minecraft.core.Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, MODID);
        public static final RegistryObject<ConfiguredStructureFeature<?, ?>> BETTER_FORTRESS_CONFIGURED = registerConfiguredStructureFeature("better_fortress", () ->
                StructureFeatureRegistry.BETTER_FORTRESS.get().configured(
                        NoneFeatureConfiguration.INSTANCE,
                        RevampTags.HAS_BETTER_FORTRESS.get(),
                        false,
                        Map.of(
                                MobCategory.MONSTER,
                                new StructureSpawnOverride(
                                        StructureSpawnOverride.BoundingBoxType.PIECE,
                                        BetterFortressFeature.FORTRESS_ENEMIES
                                )
                        )
                )
        );

        public static <FC extends FeatureConfiguration, F extends StructureFeature<FC>> RegistryObject<ConfiguredStructureFeature<?, ?>> registerConfiguredStructureFeature(String id, Supplier<ConfiguredStructureFeature<FC, F>> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class PlacedFeatureRegistry {
        private static final DeferredRegister<PlacedFeature> REGISTER = DeferredRegister.create(net.minecraft.core.Registry.PLACED_FEATURE_REGISTRY, MODID);
        public static final RegistryObject<PlacedFeature> IRIDIUM_SMALL_PLACED = registerPlacedFeature("ore_iridium_small", () -> new PlacedFeature(ConfiguredFeatureRegistry.IRIDIUM_SMALL_CONFIGURED.getHolder().get(), List.of(HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(10)), RarityFilter.onAverageOnceEvery(8))));
        public static final RegistryObject<PlacedFeature> IRIDIUM_LARGE_PLACED = registerPlacedFeature("ore_iridium_large", () -> new PlacedFeature(ConfiguredFeatureRegistry.IRIDIUM_LARGE_CONFIGURED.getHolder().get(), List.of(HeightRangePlacement.triangle(VerticalAnchor.absolute(-48), VerticalAnchor.absolute(-16)), RarityFilter.onAverageOnceEvery(20))));
        public static final RegistryObject<PlacedFeature> IRIDIUM_BURIED_PLACED = registerPlacedFeature("ore_iridium_buried", () -> new PlacedFeature(ConfiguredFeatureRegistry.IRIDIUM_BURIED_CONFIGURED.getHolder().get(), List.of(HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(10)), RarityFilter.onAverageOnceEvery(2))));

        public static final RegistryObject<PlacedFeature> PLATFORM_FUNGUS_PLACED = registerPlacedFeature("platform_fungus", () -> new PlacedFeature(ConfiguredFeatureRegistry.PLATFORM_FUNGUS_CONFIGURED.getHolder().get(), List.of(CountOnEveryLayerPlacement.of(12), BiomeFilter.biome())));

        public static <P extends PlacedFeature> RegistryObject<PlacedFeature> registerPlacedFeature(String id, Supplier<P> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class BiomeRegistry {
        private static final DeferredRegister<Biome> REGISTER = DeferredRegister.create(ForgeRegistries.BIOMES, MODID);
        public static final RegistryObject<Biome> LAVA_GARDENS = registerBiome("lava_gardens", () -> new Biome.BiomeBuilder()
                .precipitation(Biome.Precipitation.NONE)
                .biomeCategory(Biome.BiomeCategory.NETHER)
                .temperature(1f)
                .temperatureAdjustment(Biome.TemperatureModifier.NONE)
                .downfall(0f)
                .specialEffects(
                        new BiomeSpecialEffects.Builder()
                                .fogColor(0xa86032)
                                .waterColor(9122495)
                                .waterFogColor(0xa86032)
                                .skyColor(13949762)
                                .build()
                )
                .mobSpawnSettings(
                        new MobSpawnSettings.Builder()
                                .addSpawn(MobCategory.MONSTER,
                                        new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 10, 3, 5)
                                )
                                .build()
                )
                .generationSettings(
                        new BiomeGenerationSettings.Builder()
                                .addFeature(GenerationStep.Decoration.UNDERGROUND_DECORATION, PlacedFeatureRegistry.PLATFORM_FUNGUS_PLACED.getHolder().get())
                                .build()
                )
                .build()
        );

        public static RegistryObject<Biome> registerBiome(String id, Supplier<Biome> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class RuleSourceRegistry {
        private static final DeferredRegister<Codec<? extends SurfaceRules.RuleSource>> REGISTER = DeferredRegister.create(net.minecraft.core.Registry.RULE_REGISTRY, MODID);
        public static final RegistryObject<Codec<SurfaceRules.RuleSource>> MAGMA_MYCELIUM_RULE_SOURCE = registerRuleSource("magma_mycelium", () -> Codec.unit(new RuleSource2(BlockRegistry.MAGMA_MYCELIUM_BLOCK)));

        public static <K extends Codec<S>, S extends SurfaceRules.RuleSource> RegistryObject<K> registerRuleSource(String id, Supplier<K> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class EntityDataSerializerRegistry {
        private static final DeferredRegister<DataSerializerEntry> REGISTER = DeferredRegister.create(ForgeRegistries.Keys.DATA_SERIALIZERS, MODID);
        public static final RegistryObject<DataSerializerEntry> ROD_SERIALIZER_ENTRY = registerDataSerializerEntry("rods", () -> new DataSerializerEntry(
                new EntityDataSerializer<RevampedBlaze.Rods>() {
                    @Override
                    public void write(FriendlyByteBuf buf, RevampedBlaze.Rods rods) {
                        buf.writeInt(rods.numRods());
                        buf.writeUUID(rods.blazeUUID);
                        for (RevampedBlaze.Rod rod : rods.rods) {
                            buf.writeUtf(rod.type().toString());
                            buf.writeDouble(rod.pos().x());
                            buf.writeDouble(rod.pos().y());
                            buf.writeDouble(rod.pos().z());
                        }
                    }

                    @Override
                    public RevampedBlaze.Rods read(FriendlyByteBuf buf) {
                        int n = buf.readInt();
                        UUID uuid = buf.readUUID();
                        List<RevampedBlaze.Rod> r = new ArrayList<>();
                        for (int i = 0; i < n; i++) {
                            RevampedBlaze.RodType type = RevampedBlaze.RodType.valueOf(buf.readUtf());
                            Vec3 pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
                            r.add(new RevampedBlaze.Rod(type, pos));
                        }
                        return new RevampedBlaze.Rods(r, uuid);
                    }

                    @Override
                    public RevampedBlaze.Rods copy(RevampedBlaze.Rods rods) {
                        return new RevampedBlaze.Rods(rods.rods, rods.blazeUUID);
                    }
                }
        ));

        public static RegistryObject<DataSerializerEntry> registerDataSerializerEntry(String id, Supplier<DataSerializerEntry> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class ParticleTypeRegistry {
        private static final DeferredRegister<ParticleType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
        public static RegistryObject<SimpleParticleType> CHARGE_PARTICLE_TYPE_POSITIVE = registerParticleType("charge_positive", () -> new SimpleParticleType(false));
        public static RegistryObject<SimpleParticleType> CHARGE_PARTICLE_TYPE_NEGATIVE = registerParticleType("charge_negative", () -> new SimpleParticleType(false));

        public static <T extends ParticleType<?>> RegistryObject<T> registerParticleType(String id, Supplier<T> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static class BiomeSourceRegistry {
        private static final DeferredRegister<Codec<? extends BiomeSource>> REGISTER = DeferredRegister.create(net.minecraft.core.Registry.BIOME_SOURCE_REGISTRY, MODID);

        public static <K extends Codec<S>, S extends BiomeSource> RegistryObject<K> registerBiomeSource(String id, Supplier<K> supplier) {
            return REGISTER.register(id, supplier);
        }
    }

    public static void register(IEventBus bus) {
        for (DeferredRegister<?> register : REGISTERS) {
            register.register(bus);
        }
    }
}
