package com.zygzag.revamp.common.loot;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class ExecutionerModifier extends LootModifier {

    public static Codec<ExecutionerModifier> CODEC = RecordCodecBuilder.create(
            inst -> codecStart(inst)
                    .and(
                            inst.group(
                                    ForgeRegistries.ITEMS.getCodec()
                                            .fieldOf("item_to_drop")
                                            .forGetter(
                                                    (it) -> it.itemOut
                                            )
                            ).t1()
                    )
                    .apply(
                            inst,
                            ExecutionerModifier::new
                    )
    );

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
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
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

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
