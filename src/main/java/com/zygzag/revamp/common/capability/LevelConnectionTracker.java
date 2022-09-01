package com.zygzag.revamp.common.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LevelConnectionTracker {
    public Map<TagKey<Block>, ConnectionTracker> connectionTrackers = new HashMap<>();
    public Level world;
    public LevelConnectionTracker(Level world){
        this.world = world;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        for (Map.Entry<TagKey<Block>, ConnectionTracker> entry : connectionTrackers.entrySet()) {
            nbt.put(entry.getKey().location().toString(), entry.getValue().serializeNBT());
        }
        return nbt;
    }

    public void deserializeNBT(CompoundTag tag) {
        for (String key : tag.getAllKeys()) {
            Optional<TagKey<Block>> tagKey = ForgeRegistries.BLOCKS.tags().getTagNames().filter((it) -> it.location().equals(new ResourceLocation(key))).findFirst();
            tagKey.ifPresent(blockTagKey -> {
                connectionTrackers.put(blockTagKey, ConnectionTracker.deserializeNBT(tag.getList(key, Tag.TAG_LIST), blockTagKey, world));
            });
        }
    }

    public ConnectionTracker createNewTracker(TagKey<Block> tag) {
        ConnectionTracker tracker = new ConnectionTracker(tag, world);
        connectionTrackers.put(tag, tracker);
        return tracker;
    }

    public ConnectionTracker getOrCreateTracker(TagKey<Block> tag) {
        ConnectionTracker tracker = connectionTrackers.get(tag);
        if (tracker != null) tracker = createNewTracker(tag);
        return tracker;
    }

    public void update(@Nullable BlockPos updatePosition) {
        for (ConnectionTracker tracker : connectionTrackers.values()) tracker.update(updatePosition, world);
    }
}
