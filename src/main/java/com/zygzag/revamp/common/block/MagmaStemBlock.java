package com.zygzag.revamp.common.block;

import com.zygzag.revamp.common.registry.BlockRegistry;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

public class MagmaStemBlock extends RotatedPillarBlock {
    private final boolean isStripped;
    public MagmaStemBlock(Properties properties, boolean isStripped) {
        super(properties);
        this.isStripped = isStripped;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction action, boolean simulate) {
        if (action == ToolActions.AXE_STRIP) {
            if (!isStripped) return BlockRegistry.STRIPPED_MAGMA_STEM_BLOCK.get().defaultBlockState().setValue(AXIS, state.getValue(AXIS));
            else return state;
        } else return state;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(AXIS, ctx.getClickedFace().getAxis());
    }
}
