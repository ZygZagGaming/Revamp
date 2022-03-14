package com.zygzag.revamp.util;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public class Constants {

    public static int AMETHYST_HOE_COOLDOWN = 400;

    public static int AMETHYST_PICKAXE_COOLDOWN = 1800;

    public static int AMETHYST_SCEPTER_COOLDOWN = 400;
    public static int EMERALD_SCEPTER_COOLDOWN = 1000;
    public static int SKULL_SCEPTER_COOLDOWN = 400;
    public static int WITHER_SKULL_SCEPTER_COOLDOWN = 1000;

    public static int WITHER_SKULL_SWORD_COOLDOWN = 1000;

    public static int EMERALD_SHOVEL_COOLDOWN = 20;
    public static int SKULL_SHOVEL_COOLDOWN = 100;
    public static int WITHER_SKULL_SHOVEL_COOLDOWN = 400;

    public static int SIGHT_EFFECT_COLOR = 0x662382;
    public static int REACH_EFFECT_COLOR = 0xe34b34;

    public static int UNWATERED_SOIL_HIGHLIGHT_COLOR = 0xffffff;
    public static int CROP_COLOR = 0xf5a716;

    public static int COPPER_ORE_COLOR = 0xe77c56;
    public static int COAL_ORE_COLOR = 0x323232;
    public static int IRON_ORE_COLOR = 0xd8d8d8;
    public static int GOLD_ORE_COLOR = 0xfdf55f;
    public static int NETHERITE_ORE_COLOR = 0x654740;
    public static int DIAMOND_ORE_COLOR = 0x2be0b9;
    public static int QUARTZ_ORE_COLOR = 0xddd4c6;
    public static int LAPIS_ORE_COLOR = 0x345ec3;
    public static int REDSTONE_ORE_COLOR = 0xaa0f01;

    public static RenderType.CompositeRenderType TEST = RenderType.create("test", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 256, RenderType.CompositeState.builder().setShaderState(RenderType.RENDERTYPE_LINES_SHADER).setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty())).setLayeringState(RenderType.VIEW_OFFSET_Z_LAYERING).setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY).setOutputState(RenderType.OUTLINE_TARGET).setWriteMaskState(RenderType.COLOR_DEPTH_WRITE).setDepthTestState(RenderType.NO_DEPTH_TEST).setCullState(RenderType.NO_CULL).createCompositeState(true));
}
