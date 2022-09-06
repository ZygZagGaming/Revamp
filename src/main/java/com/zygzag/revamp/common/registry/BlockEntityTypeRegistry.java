package com.zygzag.revamp.common.registry;

import com.zygzag.revamp.common.block.entity.ChargeDetectorBlockEntity;
import com.zygzag.revamp.common.block.entity.CustomSignBlockEntity;
import com.zygzag.revamp.common.block.entity.ShulkstoneCannonBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.zygzag.revamp.common.Revamp.MODID;

public class BlockEntityTypeRegistry extends Registry<BlockEntityType<?>> {
    public static final BlockEntityTypeRegistry INSTANCE = new BlockEntityTypeRegistry(DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID), MODID);

    public static RegistryObject<BlockEntityType<ChargeDetectorBlockEntity>> CHARGE_DETECTOR = INSTANCE.register("charge_detector", () -> BlockEntityType.Builder.of(ChargeDetectorBlockEntity::new, BlockRegistry.CHARGE_DETECTOR.get()).build(null));
    public static RegistryObject<BlockEntityType<CustomSignBlockEntity>> CUSTOM_SIGN = INSTANCE.register("custom_sign", () -> BlockEntityType.Builder.of(CustomSignBlockEntity::new,
            Blocks.OAK_SIGN,
            Blocks.SPRUCE_SIGN,
            Blocks.BIRCH_SIGN,
            Blocks.ACACIA_SIGN,
            Blocks.JUNGLE_SIGN,
            Blocks.DARK_OAK_SIGN,
            Blocks.OAK_WALL_SIGN,
            Blocks.SPRUCE_WALL_SIGN,
            Blocks.BIRCH_WALL_SIGN,
            Blocks.ACACIA_WALL_SIGN,
            Blocks.JUNGLE_WALL_SIGN,
            Blocks.DARK_OAK_WALL_SIGN,
            Blocks.CRIMSON_SIGN,
            Blocks.CRIMSON_WALL_SIGN,
            Blocks.WARPED_SIGN,
            Blocks.WARPED_WALL_SIGN,
            Blocks.MANGROVE_SIGN,
            Blocks.MANGROVE_WALL_SIGN,
            BlockRegistry.MAGMA_SIGN.get(),
            BlockRegistry.MAGMA_WALL_SIGN.get()
    ).build(null));

    public static RegistryObject<BlockEntityType<ShulkstoneCannonBlockEntity>> SHULKSTONE_CANNON = INSTANCE.register("shulkstone_cannon", () -> BlockEntityType.Builder.of(ShulkstoneCannonBlockEntity::new, BlockRegistry.SHULKSTONE_CANNON.get()).build(null));

    public BlockEntityTypeRegistry(DeferredRegister<BlockEntityType<?>> register, String modid) {
        super(register, modid);
    }
}
