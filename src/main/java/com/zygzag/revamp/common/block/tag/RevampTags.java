package com.zygzag.revamp.common.block.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.Lazy;

public class RevampTags {
    public static final Lazy<TagKey<Block>> FARMLAND = () -> BlockTags.create(new ResourceLocation("forge:farmland"));
    public static final Lazy<TagKey<EntityType<?>>> BOSSES = () -> EntityTypeTags.create("forge:bosses");
    public static final Lazy<TagKey<EntityType<?>>> ILLAGERS = () -> EntityTypeTags.create("forge:illagers");
    public static final Lazy<TagKey<Block>> COPPER_ORES = () -> BlockTags.create(new ResourceLocation("forge:ores/copper"));
}
