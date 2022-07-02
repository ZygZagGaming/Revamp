package com.zygzag.revamp.common.capability;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.block.MagmaStemBlock;
import com.zygzag.revamp.common.registry.Registry;
import com.zygzag.revamp.common.tag.RevampTags;
import com.zygzag.revamp.util.Constants;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.units.qual.C;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LevelContiguousSectionTracker {
    public Map<TagKey<Block>, ContiguousSectionTracker> sectionTrackers = new HashMap<>();
    public Level world;
    public LevelContiguousSectionTracker(Level world){
        this.world = world;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        for (Map.Entry<TagKey<Block>, ContiguousSectionTracker> entry : sectionTrackers.entrySet()) {
            nbt.put(entry.getKey().location().toString(), entry.getValue().serializeNBT());
        }
        return nbt;
    }

    public void deserializeNBT(CompoundTag tag) {
        for (String key : tag.getAllKeys()) {
            Optional<TagKey<Block>> tagKey = ForgeRegistries.BLOCKS.tags().getTagNames().filter((it) -> it.location().equals(new ResourceLocation(key))).findFirst();
            tagKey.ifPresent(blockTagKey -> {
                sectionTrackers.put(blockTagKey, ContiguousSectionTracker.deserializeNBT(tag.getList(key, Tag.TAG_LIST), blockTagKey, world));
            });
        }
    }

    public ContiguousSectionTracker createNewTracker(TagKey<Block> tag) {
        ContiguousSectionTracker tracker = new ContiguousSectionTracker(tag, world);
        sectionTrackers.put(tag, tracker);
        return tracker;
    }

    public void update(@Nullable BlockPos updatePosition) {
        if (updatePosition != null) {
            BlockState state = world.getBlockState(updatePosition);
            List<TagKey<Block>> tags = Revamp.trackedTags.get();
            for (TagKey<Block> tag : tags) {
                if (state.is(tag)) {
                    Optional.ofNullable(sectionTrackers.getOrDefault(tag, null))
                            .ifPresentOrElse((tracker) -> {
                                tracker.addNewSection(updatePosition);
                            }, () -> {
                                createNewTracker(tag).addNewSection(updatePosition);
                            });
                    break;
                }
            }
        }
        for (ContiguousSectionTracker tracker : sectionTrackers.values()) tracker.update(updatePosition, world);
    }
}
