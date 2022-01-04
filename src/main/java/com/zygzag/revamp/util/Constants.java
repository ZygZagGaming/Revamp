package com.zygzag.revamp.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.zygzag.revamp.common.block.tag.RevampTags;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import java.util.OptionalDouble;
import java.util.function.Function;

public class Constants {

    public static int AMETHYST_HOE_COOLDOWN = 400;

    public static int AMETHYST_PICKAXE_COOLDOWN = 400;

    public static int AMETHYST_SCEPTER_COOLDOWN = 400;
    public static int EMERALD_SCEPTER_COOLDOWN = 1000;
    public static int SKULL_SCEPTER_COOLDOWN = 1000;
    public static int WITHER_SKULL_SCEPTER_COOLDOWN = 1000;

    public static int EMERALD_SHOVEL_COOLDOWN = 20;
    public static int SKULL_SHOVEL_COOLDOWN = 100;
    public static int WITHER_SKULL_SHOVEL_COOLDOWN = 400;

    public static int SIGHT_EFFECT_COLOR = 0x662382;

    public static int COPPER_ORE_COLOR = 0x0;
    public static int COAL_ORE_COLOR = 0x0;
    public static int IRON_ORE_COLOR = 0x0;
    public static int GOLD_ORE_COLOR = 0x0;
    public static int NETHERITE_ORE_COLOR = 0x0;
    public static int DIAMOND_ORE_COLOR = 0x0;
    public static int QUARTZ_ORE_COLOR = 0x0;
    public static int LAPIS_ORE_COLOR = 0x0;
    public static int REDSTONE_ORE_COLOR = 0x0;

    public static int getColor(BlockState state) {
        if (state.is(RevampTags.COPPER_ORES.get())) return COPPER_ORE_COLOR;
        else if (state.is(Tags.Blocks.ORES_COAL)) return COAL_ORE_COLOR;
        else if (state.is(Tags.Blocks.ORES_IRON)) return IRON_ORE_COLOR;
        else if (state.is(Tags.Blocks.ORES_GOLD)) return GOLD_ORE_COLOR;
        else if (state.is(Tags.Blocks.ORES_NETHERITE_SCRAP)) return NETHERITE_ORE_COLOR;
        else if (state.is(Tags.Blocks.ORES_DIAMOND)) return DIAMOND_ORE_COLOR;

        return 0;
    }

    public static RenderType.CompositeRenderType TEST = RenderType.create("test", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 256, RenderType.CompositeState.builder().setShaderState(RenderType.RENDERTYPE_LINES_SHADER).setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING).setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY).setOutputState(RenderType.OUTLINE_TARGET).setWriteMaskState(RenderType.COLOR_DEPTH_WRITE).setDepthTestState(RenderType.NO_DEPTH_TEST).setCullState(RenderType.NO_CULL).createCompositeState(true));
}
