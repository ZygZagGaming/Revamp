package com.zygzag.revamp.common.registry;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.block.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.zygzag.revamp.common.Revamp.MODID;

public class BlockRegistry extends Registry<Block> {
    public static final BlockRegistry INSTANCE = new BlockRegistry(DeferredRegister.create(ForgeRegistries.BLOCKS, MODID), MODID);
    public static final RegistryObject<Block> LAVA_VINES_BLOCK = INSTANCE.register("lava_vines", () -> new LavaVinesBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2f).sound(SoundType.NETHER_SPROUTS)));

    public static final RegistryObject<Block> MAGMA_FUNGUS_BLOCK = INSTANCE.register("magma_fungus_block", () -> new MagmaFungusBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2f).sound(SoundType.STEM)));
    public static final RegistryObject<Block> MAGMA_FUNGUS_CAP_BLOCK = INSTANCE.register("magma_fungus_cap", () -> new MagmaFungusBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2f).sound(SoundType.STEM)));
    public static final RegistryObject<Block> MAGMA_FUNGUS_EDGE_BLOCK = INSTANCE.register("magma_fungus_edge", () -> new MagmaFungusBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2f).sound(SoundType.STEM)));
    public static final RegistryObject<Block> MAGMA_STEM_BLOCK = INSTANCE.register("magma_stem", () -> new MagmaStemBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2f).sound(SoundType.STEM), false));
    public static final RegistryObject<Block> STRIPPED_MAGMA_STEM_BLOCK = INSTANCE.register("stripped_magma_stem", () -> new MagmaStemBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2f).sound(SoundType.STEM), true));
    public static final RegistryObject<Block> MAGMA_HYPHAE_BLOCK = INSTANCE.register("magma_hyphae", () -> new MagmaStemBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2f).sound(SoundType.STEM), false));
    public static final RegistryObject<Block> STRIPPED_MAGMA_HYPHAE_BLOCK = INSTANCE.register("stripped_magma_hyphae", () -> new MagmaStemBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2f).sound(SoundType.STEM), true));

    public static final RegistryObject<Block> MAGMA_PLANKS = INSTANCE.register("magma_planks", () -> new Block(BlockBehaviour.Properties.of(Material.NETHER_WOOD, MaterialColor.CRIMSON_STEM).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> MAGMA_SLAB = INSTANCE.register("magma_slab", () -> new SlabBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD, MAGMA_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> MAGMA_PRESSURE_PLATE = INSTANCE.register("magma_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.of(Material.NETHER_WOOD, MAGMA_PLANKS.get().defaultMaterialColor()).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> MAGMA_FENCE = INSTANCE.register("magma_fence", () -> new FenceBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD, MAGMA_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> MAGMA_TRAPDOOR = INSTANCE.register("magma_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD, MAGMA_PLANKS.get().defaultMaterialColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn((a, b, c, d) -> false)));
    public static final RegistryObject<Block> MAGMA_FENCE_GATE = INSTANCE.register("magma_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD, MAGMA_PLANKS.get().defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> MAGMA_STAIRS = INSTANCE.register("magma_stairs", () -> new StairBlock(() -> MAGMA_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(MAGMA_PLANKS.get())));
    public static final RegistryObject<Block> MAGMA_BUTTON = INSTANCE.register("magma_button", () -> new WoodButtonBlock(BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> MAGMA_DOOR = INSTANCE.register("magma_door", () -> new DoorBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD, MAGMA_PLANKS.get().defaultMaterialColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<Block> MAGMA_SIGN = INSTANCE.register("magma_sign", () -> new CustomStandingSignBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD, MAGMA_PLANKS.get().defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD), Revamp.MAGMA_WOOD_TYPE));
    public static final RegistryObject<Block> MAGMA_WALL_SIGN = INSTANCE.register("magma_wall_sign", () -> new CustomWallSignBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD, MAGMA_PLANKS.get().defaultMaterialColor()).noCollission().strength(1.0F).sound(SoundType.WOOD).lootFrom(MAGMA_SIGN), Revamp.MAGMA_WOOD_TYPE));

    public static final RegistryObject<Block> MAGMA_PUSTULE_BLOCK = INSTANCE.register("magma_pustule", () -> new MagmaPustuleBlock(BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(0f).sound(SoundType.NETHER_SPROUTS)));

    public static final RegistryObject<Block> IRIDIUM_ORE = INSTANCE.register("iridium_ore", IridiumOreBlock::new);
    public static final RegistryObject<Block> DEEPSLATE_IRIDIUM_ORE = INSTANCE.register("deepslate_iridium_ore", () -> new IridiumOreBlock(BlockBehaviour.Properties.copy(IRIDIUM_ORE.get()).strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
    public static final RegistryObject<Block> RAW_IRIDIUM_BLOCK = INSTANCE.register("raw_iridium_block", () -> new Block(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.STONE)));
    //public static final RegistryObject<Block> IRIDIUM_GRATING = INSTANCE.register()("iridium_grating", () -> new GrateBlock(BlockBehaviour.Properties.of(Material.METAL, MaterialColor.METAL).requiresCorrectToolForDrops().strength(1f, 6f).sound(SoundType.METAL)));
    public static final RegistryObject<Block> BLESSED_SOIL = INSTANCE.register("blessed_soil", () -> new BlessedSoilBlock(BlockBehaviour.Properties.copy(Blocks.FARMLAND)));
    public static final RegistryObject<Block> OSTEUM = INSTANCE.register("osteum", () -> new OsteumBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK)));
    public static final RegistryObject<Block> GROWING_OSTEUM = INSTANCE.register("growing_osteum", () -> new GrowingOsteumBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK)));

    public static final RegistryObject<Block> MAGMA_NYLIUM_BLOCK = INSTANCE.register("magma_nylium", () -> new MagmaNyliumBlock(BlockBehaviour.Properties.copy(Blocks.NETHERRACK)));

    public static final RegistryObject<Block> CHARGE_CRYSTAL_BLOCK_NEGATIVE = INSTANCE.register("charge_crystal_negative", () -> new ChargeCrystalBlock(BlockBehaviour.Properties.of(Material.AMETHYST), -20f));
    public static final RegistryObject<Block> CHARGE_CRYSTAL_BLOCK_POSITIVE = INSTANCE.register("charge_crystal_positive", () -> new ChargeCrystalBlock(BlockBehaviour.Properties.of(Material.AMETHYST), 20f));
    public static final RegistryObject<Block> CHARGE_DETECTOR = INSTANCE.register("charge_detector", () -> new ChargeDetectorBlock(BlockBehaviour.Properties.of(Material.DECORATION).instabreak().sound(SoundType.WOOD)));
    public static final RegistryObject<Block> ARC_CRYSTAL = INSTANCE.register("arc_crystal", () -> new ArcCrystalBlock(BlockBehaviour.Properties.of(Material.AMETHYST)));
    public static final RegistryObject<Block> SHULKSTONE_BLOCK = INSTANCE.register("shulkstone_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.PURPUR_BLOCK).strength(200f, 1200f)));
    public static final RegistryObject<Block> SHULKSTONE_CANNON = INSTANCE.register("shulkstone_cannon", () -> new ShulkstoneCannonBlock(BlockBehaviour.Properties.copy(Blocks.PURPUR_BLOCK).strength(200f, 1200f)));

    private BlockRegistry(DeferredRegister<Block> register, String modid) {
        super(register, modid);
    }
}
