package com.zygzag.revamp.common.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zygzag.revamp.common.Revamp;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConductivenessReloadListener extends SimpleJsonResourceReloadListener {
    private Map<Block, Float> map = new HashMap<>();
    public static final Gson GSON = new Gson();

    public ConductivenessReloadListener() {
        super(GSON, "conductiveness");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> json, ResourceManager manager, ProfilerFiller filler) {
        ImmutableMap.Builder<Block, Float> builder = ImmutableMap.builder();

            json.forEach((loc, elem) -> {
                if (elem instanceof JsonObject obj) {
                    for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                        try {
                            ResourceLocation loc2 = new ResourceLocation(entry.getKey());
                            if (!ForgeRegistries.BLOCKS.containsKey(loc2)) Revamp.LOGGER.error("Missing block {}", entry.getKey());
                            else builder.put(ForgeRegistries.BLOCKS.getValue(loc2), entry.getValue().getAsFloat());
                        } catch (Exception e) {
                            Revamp.LOGGER.error(e);
                        }
                    }
                }
            });

        map = builder.build();
    }

    public float getValue(Block block) {
        return map.getOrDefault(block, 0f);
    }

    public boolean isInsulator(Block block) {
        return getValue(block) < 0;
    }

    public boolean isConductor(Block block) {
        return getValue(block) > 0;
    }

    public boolean isGround(Block block) {
        return getValue(block) == 0;
    }
}
