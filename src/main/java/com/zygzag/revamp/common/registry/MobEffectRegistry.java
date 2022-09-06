package com.zygzag.revamp.common.registry;

import com.zygzag.revamp.common.entity.effect.SightEffect;
import com.zygzag.revamp.common.entity.effect.WeightlessnessEffect;
import com.zygzag.revamp.util.Constants;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.UUID;

import static com.zygzag.revamp.common.Revamp.MODID;

public class MobEffectRegistry extends Registry<MobEffect> {
    public static final MobEffectRegistry INSTANCE = new MobEffectRegistry(DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID), MODID);
    public static RegistryObject<MobEffect> SIGHT_EFFECT = INSTANCE.register("sight", () -> new SightEffect(MobEffectCategory.BENEFICIAL, Constants.SIGHT_EFFECT_COLOR, (b) -> b.is(Tags.Blocks.ORES), GeneralUtil::getColor));
    public static RegistryObject<MobEffect> GREEN_THUMB_EFFECT = INSTANCE.register("green_thumb", () -> new SightEffect(MobEffectCategory.BENEFICIAL, Constants.SIGHT_EFFECT_COLOR, (b) -> (b.getBlock() instanceof FarmBlock && b.hasProperty(FarmBlock.MOISTURE) && b.getValue(FarmBlock.MOISTURE) != 7) || (b.getBlock() instanceof CropBlock c && c.isMaxAge(b)), (b) -> b.getBlock() instanceof CropBlock ? Constants.CROP_COLOR : Constants.UNWATERED_SOIL_HIGHLIGHT_COLOR));
    public static RegistryObject<MobEffect> REACH_EFFECT = INSTANCE.register("reach", () -> new MobEffect(MobEffectCategory.BENEFICIAL, Constants.REACH_EFFECT_COLOR).addAttributeModifier(ForgeMod.REACH_DISTANCE.get(), UUID.randomUUID().toString(), 2.0, AttributeModifier.Operation.ADDITION));
    public static RegistryObject<MobEffect> WEIGHTLESSNESS_EFFECT = INSTANCE.register("weightlessness", WeightlessnessEffect::new);

    public MobEffectRegistry(DeferredRegister<MobEffect> register, String modid) {
        super(register, modid);
    }
}
