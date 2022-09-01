package com.zygzag.revamp.common.registry;

import com.zygzag.revamp.common.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.zygzag.revamp.common.Revamp.MODID;

public class EntityRegistry extends Registry<EntityType<?>> {
    public static final EntityRegistry INSTANCE = new EntityRegistry(DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID), MODID);
    public static final RegistryObject<EntityType<EmpoweredWither>> EMPOWERED_WITHER = INSTANCE.register("empowered_wither", () ->
            EntityType.Builder.of(EmpoweredWither::new, MobCategory.MONSTER)
                    .fireImmune()
                    .immuneTo(Blocks.WITHER_ROSE)
                    .sized(1.1F, 3.75F)
                    .clientTrackingRange(10)
                    .build("empowered_wither")
    );
    public static final RegistryObject<EntityType<RevampedBlaze>> REVAMPED_BLAZE = INSTANCE.register("revamped_blaze", () ->
            EntityType.Builder.<RevampedBlaze>of(RevampedBlaze::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.8F, 1.85F)
                    .clientTrackingRange(10)
                    .build("revamped_blaze")
    );

    public static final RegistryObject<EntityType<HomingWitherSkull>> HOMING_WITHER_SKULL = INSTANCE.register("homing_wither_skull", () -> EntityType.Builder.<HomingWitherSkull>of(HomingWitherSkull::new, MobCategory.MISC).sized(0.3125F, 0.3125F).clientTrackingRange(4).build("homing_wither_skull"));
    public static final RegistryObject<EntityType<ThrownTransmutationCharge>> TRANSMUTATION_BOTTLE_ENTITY = INSTANCE.register("transmutation_bottle", () -> EntityType.Builder.<ThrownTransmutationCharge>of(ThrownTransmutationCharge::new, MobCategory.MISC).sized(0.25f, 0.25f).build("transmutation_bottle"));
    public static final RegistryObject<EntityType<ThrownAxe>> THROWN_AXE = INSTANCE.register("thrown_axe", () -> EntityType.Builder.<ThrownAxe>of(ThrownAxe::new, MobCategory.MISC).build("thrown_axe"));

    public EntityRegistry(DeferredRegister<EntityType<?>> register, String modid) {
        super(register, modid);
    }
}
