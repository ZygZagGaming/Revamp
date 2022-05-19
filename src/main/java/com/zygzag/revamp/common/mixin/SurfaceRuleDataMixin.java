package com.zygzag.revamp.common.mixin;

import com.zygzag.revamp.common.misc.RuleSource2;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraftforge.registries.RegistryObject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings({"unused", "ConstantConditions"})
@Mixin(SurfaceRuleData.class)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SurfaceRuleDataMixin {
    private static final SurfaceRules.RuleSource AIR = makeStateRule(Blocks.AIR);
    private static final SurfaceRules.RuleSource BEDROCK = makeStateRule(Blocks.BEDROCK);
    private static final SurfaceRules.RuleSource WHITE_TERRACOTTA = makeStateRule(Blocks.WHITE_TERRACOTTA);
    private static final SurfaceRules.RuleSource ORANGE_TERRACOTTA = makeStateRule(Blocks.ORANGE_TERRACOTTA);
    private static final SurfaceRules.RuleSource TERRACOTTA = makeStateRule(Blocks.TERRACOTTA);
    private static final SurfaceRules.RuleSource RED_SAND = makeStateRule(Blocks.RED_SAND);
    private static final SurfaceRules.RuleSource RED_SANDSTONE = makeStateRule(Blocks.RED_SANDSTONE);
    private static final SurfaceRules.RuleSource STONE = makeStateRule(Blocks.STONE);
    private static final SurfaceRules.RuleSource DEEPSLATE = makeStateRule(Blocks.DEEPSLATE);
    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource PODZOL = makeStateRule(Blocks.PODZOL);
    private static final SurfaceRules.RuleSource COARSE_DIRT = makeStateRule(Blocks.COARSE_DIRT);
    private static final SurfaceRules.RuleSource MYCELIUM = makeStateRule(Blocks.MYCELIUM);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource CALCITE = makeStateRule(Blocks.CALCITE);
    private static final SurfaceRules.RuleSource GRAVEL = makeStateRule(Blocks.GRAVEL);
    private static final SurfaceRules.RuleSource SAND = makeStateRule(Blocks.SAND);
    private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);
    private static final SurfaceRules.RuleSource PACKED_ICE = makeStateRule(Blocks.PACKED_ICE);
    private static final SurfaceRules.RuleSource SNOW_BLOCK = makeStateRule(Blocks.SNOW_BLOCK);
    private static final SurfaceRules.RuleSource POWDER_SNOW = makeStateRule(Blocks.POWDER_SNOW);
    private static final SurfaceRules.RuleSource ICE = makeStateRule(Blocks.ICE);
    private static final SurfaceRules.RuleSource WATER = makeStateRule(Blocks.WATER);
    private static final SurfaceRules.RuleSource LAVA = makeStateRule(Blocks.LAVA);
    private static final SurfaceRules.RuleSource NETHERRACK = makeStateRule(Blocks.NETHERRACK);
    private static final SurfaceRules.RuleSource SOUL_SAND = makeStateRule(Blocks.SOUL_SAND);
    private static final SurfaceRules.RuleSource SOUL_SOIL = makeStateRule(Blocks.SOUL_SOIL);
    private static final SurfaceRules.RuleSource BASALT = makeStateRule(Blocks.BASALT);
    private static final SurfaceRules.RuleSource BLACKSTONE = makeStateRule(Blocks.BLACKSTONE);
    private static final SurfaceRules.RuleSource WARPED_WART_BLOCK = makeStateRule(Blocks.WARPED_WART_BLOCK);
    private static final SurfaceRules.RuleSource WARPED_NYLIUM = makeStateRule(Blocks.WARPED_NYLIUM);
    private static final SurfaceRules.RuleSource NETHER_WART_BLOCK = makeStateRule(Blocks.NETHER_WART_BLOCK);
    private static final SurfaceRules.RuleSource CRIMSON_NYLIUM = makeStateRule(Blocks.CRIMSON_NYLIUM);
    private static final SurfaceRules.RuleSource ENDSTONE = makeStateRule(Blocks.END_STONE);

    private static final SurfaceRules.RuleSource MAGMA_BLOCK = makeStateRule(Blocks.MAGMA_BLOCK);
    private static final SurfaceRules.RuleSource MAGMA_MYCELIUM = makeStateRule2(Registry.BlockRegistry.MAGMA_MYCELIUM_BLOCK);

    @Inject(cancellable = true, at = @At("HEAD"), method = "nether()Lnet/minecraft/world/level/levelgen/SurfaceRules$RuleSource;")
    private static void nether(CallbackInfoReturnable<SurfaceRules.RuleSource> callback) {
        boolean mixin = true;
        if (mixin) {
            SurfaceRules.ConditionSource y31Check = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(31), 0);
            SurfaceRules.ConditionSource y32Check = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(32), 0);
            SurfaceRules.ConditionSource y30StartCheck = SurfaceRules.yStartCheck(VerticalAnchor.absolute(30), 0);
            SurfaceRules.ConditionSource y35InvStartCheck = SurfaceRules.not(SurfaceRules.yStartCheck(VerticalAnchor.absolute(35), 0));
            SurfaceRules.ConditionSource y5Check = SurfaceRules.yBlockCheck(VerticalAnchor.belowTop(5), 0);
            SurfaceRules.ConditionSource holeNoise = SurfaceRules.hole();
            SurfaceRules.ConditionSource soulSandNoise = SurfaceRules.noiseCondition(Noises.SOUL_SAND_LAYER, -0.012D);
            SurfaceRules.ConditionSource gravelNoise = SurfaceRules.noiseCondition(Noises.GRAVEL_LAYER, -0.012D);
            SurfaceRules.ConditionSource patchNoise = SurfaceRules.noiseCondition(Noises.PATCH, -0.012D);
            SurfaceRules.ConditionSource netherrackNoise = SurfaceRules.noiseCondition(Noises.NETHERRACK, 0.54D);
            SurfaceRules.ConditionSource netherWartNoise = SurfaceRules.noiseCondition(Noises.NETHER_WART, 1.17D);
            SurfaceRules.ConditionSource stateNoise = SurfaceRules.noiseCondition(Noises.NETHER_STATE_SELECTOR, 0.0D);
            SurfaceRules.RuleSource gravelPatches = SurfaceRules.ifTrue(patchNoise, SurfaceRules.ifTrue(y30StartCheck, SurfaceRules.ifTrue(y35InvStartCheck, GRAVEL)));
            SurfaceRules.RuleSource source = SurfaceRules.sequence(
                    SurfaceRules.ifTrue(
                            SurfaceRules.verticalGradient(
                                    "bedrock_floor",
                                    VerticalAnchor.bottom(),
                                    VerticalAnchor.aboveBottom(5)
                            ),
                            BEDROCK
                    ),
                    SurfaceRules.ifTrue(
                            SurfaceRules.not(
                                    SurfaceRules.verticalGradient(
                                            "bedrock_roof",
                                            VerticalAnchor.belowTop(5),
                                            VerticalAnchor.top()
                                    )
                            ),
                            BEDROCK
                    ),
                    SurfaceRules.ifTrue(
                            y5Check,
                            NETHERRACK
                    ),
                    SurfaceRules.ifTrue(
                            SurfaceRules.isBiome(Biomes.BASALT_DELTAS),
                            SurfaceRules.sequence(
                                    SurfaceRules.ifTrue(
                                            SurfaceRules.UNDER_CEILING,
                                            BASALT
                                    ),
                                    SurfaceRules.ifTrue(
                                            SurfaceRules.UNDER_FLOOR,
                                            SurfaceRules.sequence(
                                                    gravelPatches,
                                                    SurfaceRules.ifTrue(
                                                            stateNoise,
                                                            BASALT
                                                    ),
                                                    BLACKSTONE
                                            )
                                    )
                            )
                    ),
                    SurfaceRules.ifTrue(
                            SurfaceRules.isBiome(
                                    Biomes.SOUL_SAND_VALLEY
                            ),
                            SurfaceRules.sequence(
                                    SurfaceRules.ifTrue(
                                            SurfaceRules.UNDER_CEILING,
                                            SurfaceRules.sequence(
                                                    SurfaceRules.ifTrue(
                                                            stateNoise,
                                                            SOUL_SAND
                                                    ),
                                                    SOUL_SOIL
                                            )
                                    ),
                                    SurfaceRules.ifTrue(
                                            SurfaceRules.UNDER_FLOOR,
                                            SurfaceRules.sequence(
                                                    gravelPatches,
                                                    SurfaceRules.ifTrue(
                                                            stateNoise,
                                                            SOUL_SAND
                                                    ),
                                                    SOUL_SOIL
                                            )
                                    )
                            )
                    ),
                    SurfaceRules.ifTrue(
                            SurfaceRules.ON_FLOOR,
                            SurfaceRules.sequence(
                                    SurfaceRules.ifTrue(
                                            SurfaceRules.not(
                                                    y32Check
                                            ),
                                            SurfaceRules.ifTrue(
                                                    holeNoise,
                                                    LAVA
                                            )
                                    ),
                                    SurfaceRules.ifTrue(
                                            SurfaceRules.isBiome(Biomes.WARPED_FOREST),
                                            SurfaceRules.ifTrue(
                                                    SurfaceRules.not(netherrackNoise),
                                                    SurfaceRules.ifTrue(
                                                            y31Check,
                                                            SurfaceRules.sequence(
                                                                    SurfaceRules.ifTrue(
                                                                            netherWartNoise,
                                                                            WARPED_WART_BLOCK
                                                                    ),
                                                                    WARPED_NYLIUM
                                                            )
                                                    )
                                            )
                                    ),
                                    SurfaceRules.ifTrue(
                                            SurfaceRules.isBiome(Biomes.CRIMSON_FOREST),
                                            SurfaceRules.ifTrue(
                                                    SurfaceRules.not(netherrackNoise),
                                                    SurfaceRules.ifTrue(
                                                            y31Check,
                                                            SurfaceRules.sequence(
                                                                    SurfaceRules.ifTrue(
                                                                            netherWartNoise,
                                                                            NETHER_WART_BLOCK
                                                                    ),
                                                                    CRIMSON_NYLIUM
                                                            )
                                                    )
                                            )
                                    )
                            )
                    ),
                    SurfaceRules.ifTrue(
                            SurfaceRules.isBiome(Biomes.NETHER_WASTES),
                            SurfaceRules.sequence(
                                    SurfaceRules.ifTrue(
                                            SurfaceRules.UNDER_FLOOR,
                                            SurfaceRules.ifTrue(
                                                    soulSandNoise,
                                                    SurfaceRules.sequence(
                                                            SurfaceRules.ifTrue(
                                                                    SurfaceRules.not(holeNoise),
                                                                    SurfaceRules.ifTrue(
                                                                            y30StartCheck,
                                                                            SurfaceRules.ifTrue(
                                                                                    y35InvStartCheck,
                                                                                    SOUL_SAND
                                                                            )
                                                                    )
                                                            ),
                                                            NETHERRACK
                                                    )
                                            )
                                    ),
                                    SurfaceRules.ifTrue(
                                            SurfaceRules.ON_FLOOR,
                                            SurfaceRules.ifTrue(
                                                    y31Check,
                                                    SurfaceRules.ifTrue(
                                                            y35InvStartCheck,
                                                            SurfaceRules.ifTrue(
                                                                    gravelNoise,
                                                                    SurfaceRules.sequence(
                                                                            SurfaceRules.ifTrue(
                                                                                    y32Check,
                                                                                    GRAVEL
                                                                            ),
                                                                            SurfaceRules.ifTrue(
                                                                                    SurfaceRules.not(holeNoise),
                                                                                    GRAVEL
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    ),
                    SurfaceRules.ifTrue(
                            SurfaceRules.isBiome(
                                    Registry.BiomeRegistry.LAVA_GARDENS.getKey()
                            ),
                            SurfaceRules.sequence(
                                    SurfaceRules.ifTrue(
                                            SurfaceRules.ON_FLOOR,
                                            MAGMA_MYCELIUM
                                    )
                            )
                    ),
                    NETHERRACK
            );
            callback.setReturnValue(source);
            callback.cancel();
        }
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    private static SurfaceRules.RuleSource makeStateRule2(RegistryObject<Block> obj) {
        return new RuleSource2(obj);
    }
}
