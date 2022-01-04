package com.zygzag.revamp.common.loot;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExecutionerModifier extends LootModifier {
    Item itemOut;

    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    protected ExecutionerModifier(LootItemCondition[] conditionsIn, Item itemOut) {
        super(conditionsIn);
        this.itemOut = itemOut;
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if (!generatedLoot.contains(Items.WITHER_SKELETON_SKULL.getDefaultInstance())) { // if it ain't already have skull
            ItemStack item = itemOut.getDefaultInstance();
            Entity entity = context.getParam(LootContextParams.THIS_ENTITY);
            if (entity instanceof Player player) {
                CompoundTag tag = item.getOrCreateTag();
                tag.putString("SkullOwner", player.getScoreboardName());
            }
            generatedLoot.add(item);
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<ExecutionerModifier> {

        @Override
        public ExecutionerModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
            String id = object.get("item_to_drop").getAsString();
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
            return new ExecutionerModifier(ailootcondition, item);
        }

        @Override
        public JsonObject write(ExecutionerModifier instance) {
            JsonObject json = makeConditions(instance.conditions);
            ResourceLocation name = instance.itemOut.getRegistryName();
            if (name != null) json.addProperty("item_to_drop", name.toString());
            return json;
        }
    }
}
