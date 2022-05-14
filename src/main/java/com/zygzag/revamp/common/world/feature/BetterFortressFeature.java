package com.zygzag.revamp.common.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.QuartPos;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.NetherBridgePieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BetterFortressFeature extends StructureFeature<NoneFeatureConfiguration> {
    public static final WeightedRandomList<MobSpawnSettings.SpawnerData> FORTRESS_ENEMIES = WeightedRandomList.create(
            new MobSpawnSettings.SpawnerData(EntityType.BLAZE, 10, 2, 3),
            new MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4),
            new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 8, 5, 5),
            new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 2, 5, 5),
            new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 3, 4, 4)
    );

    public BetterFortressFeature(Codec<NoneFeatureConfiguration> config) {
        super(config, PieceGeneratorSupplier.simple(BetterFortressFeature::checkLocation, BetterFortressFeature::generatePieces));
    }

    private static boolean checkLocation(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> ctx) {
        return ctx.validBiome().test(ctx.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(ctx.chunkPos().getMiddleBlockX()), QuartPos.fromBlock(64), QuartPos.fromBlock(ctx.chunkPos().getMiddleBlockZ())));
    }

    private static void generatePieces(StructurePiecesBuilder builder, PieceGenerator.Context<NoneFeatureConfiguration> ctx) {
        NetherBridgePieces.StartPiece start = new NetherBridgePieces.StartPiece(ctx.random(), ctx.chunkPos().getBlockX(2), ctx.chunkPos().getBlockZ(2));
        builder.addPiece(start);
        start.addChildren(start, builder, ctx.random());
        List<StructurePiece> list = start.pendingChildren;

        while(!list.isEmpty()) {
            int i = ctx.random().nextInt(list.size());
            StructurePiece structurepiece = list.remove(i);
            structurepiece.addChildren(start, builder, ctx.random());
        }

        builder.moveInsideHeights(ctx.random(), 48, 70);
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_DECORATION;
    }
}