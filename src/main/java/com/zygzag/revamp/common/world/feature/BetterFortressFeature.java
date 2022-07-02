package com.zygzag.revamp.common.world.feature;

import com.mojang.serialization.Codec;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BetterFortressFeature extends Structure {
    public static final Codec<BetterFortressFeature> CODEC = simpleCodec(BetterFortressFeature::new);

    public BetterFortressFeature(StructureSettings settings) {
        super(settings);
    }

    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext ctx) {
        ChunkPos chunkpos = ctx.chunkPos();
        BlockPos blockpos = new BlockPos(chunkpos.getMinBlockX(), 64, chunkpos.getMinBlockZ());
        return Optional.of(new Structure.GenerationStub(blockpos, (p_228526_) -> {
            generatePieces(p_228526_, ctx);
        }));
    }

    private static void generatePieces(StructurePiecesBuilder builder, Structure.GenerationContext ctx) {
        BetterFortressPieces.StartPiece startPiece = new BetterFortressPieces.StartPiece(ctx.random(), ctx.chunkPos().getBlockX(2), ctx.chunkPos().getBlockZ(2));
        builder.addPiece(startPiece);
        startPiece.addChildren(startPiece, builder, ctx.random());
        List<StructurePiece> list = startPiece.pendingChildren;

        while (!list.isEmpty()) {
            int i = ctx.random().nextInt(list.size());
            StructurePiece structurepiece = list.remove(i);
            structurepiece.addChildren(startPiece, builder, ctx.random());
        }

        builder.moveInsideHeights(ctx.random(), 48, 70);
    }

    public StructureType<?> type() {
        return Registry.StructureTypeRegistry.BETTER_FORTRESS.get();
    }
}