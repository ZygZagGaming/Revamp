package com.zygzag.revamp.common.block.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;

public class RevampTags {
    public static final Lazy<Tag<Block>> FARMLAND = () -> BlockTags.createOptional(new ResourceLocation("forge:farmland"));
    public static final Lazy<Tag<EntityType<?>>> BOSSES = () -> EntityTypeTags.createOptional(new ResourceLocation("forge:bosses"));
    public static final Lazy<Tag<EntityType<?>>> ILLAGERS = () -> EntityTypeTags.createOptional(new ResourceLocation("forge:illagers"));
    public static final Lazy<Tag<Block>> COPPER_ORES = () -> BlockTags.createOptional(new ResourceLocation("forge:ores/copper"));
}
