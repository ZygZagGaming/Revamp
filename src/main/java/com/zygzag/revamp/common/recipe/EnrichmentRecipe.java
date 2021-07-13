package com.zygzag.revamp.common.recipe;

import com.google.gson.JsonObject;
import com.zygzag.revamp.common.item.IEnrichment;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class EnrichmentRecipe extends SmithingRecipe implements IRecipe<IInventory> {

    public Attribute attribute;
    public AttributeModifier modifier;
    public ResourceLocation id;
    public EquipmentSlotType slot;
    public Ingredient base;
    public Ingredient addition;

    public EnrichmentRecipe(ResourceLocation id, Ingredient base, Ingredient addition, Attribute attribute, AttributeModifier modifier, EquipmentSlotType slot) {
        super(id, base, addition, ItemStack.EMPTY);
        this.id = id;
        this.base = base;
        this.addition = addition;
        this.attribute = attribute;
        this.modifier = modifier;
        this.slot = slot;
    }

    @Override
    public boolean matches(IInventory inv, World world) {
        return inv.getItem(1).getItem() instanceof IEnrichment && ((IEnrichment) inv.getItem(1).getItem()).canBeAppliedTo(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(IInventory inv) {
        if (inv.getItem(1).getItem() instanceof IEnrichment) {
            return ((IEnrichment) inv.getItem(1).getItem()).apply(inv.getItem(0), (float) modifier.getAmount(), modifier.getOperation());
        }
        return inv.getItem(0); // Should never happen
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x + y >= 2;
    }

    @Override
    public ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return new Serializer();
    }

    @Override
    public IRecipeType<?> getType() {
        return IRecipeType.SMITHING;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<EnrichmentRecipe> {
        public EnrichmentRecipe fromJson(ResourceLocation id, JsonObject json) {
            EnrichmentRecipe recipe = new EnrichmentRecipe(
                    id,
                    Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "base")),
                    Ingredient.fromJson(JSONUtils.getAsJsonObject(json, "addition")),
                    ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(JSONUtils.getAsString(json, "attribute"))),
                    new AttributeModifier(
                            JSONUtils.getAsString(json, "name"),
                            JSONUtils.getAsFloat(json, "amount"),
                            AttributeModifier.Operation.valueOf(JSONUtils.getAsString(json, "operation").toUpperCase())
                    ),
                    EquipmentSlotType.byName(JSONUtils.getAsString(json, "slot"))
            );
            return recipe;
        }

        public EnrichmentRecipe fromNetwork(ResourceLocation id, PacketBuffer buf) {
            return new EnrichmentRecipe(
                    id,
                    Ingredient.fromNetwork(buf),
                    Ingredient.fromNetwork(buf),
                    ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(buf.readUtf())),
                    new AttributeModifier(
                            buf.readUtf(),
                            buf.readFloat(),
                            AttributeModifier.Operation.valueOf(buf.readUtf())
                    ),
                    EquipmentSlotType.byName(buf.readUtf())
            );
        }

        public void toNetwork(PacketBuffer buf, EnrichmentRecipe recipe) {
            recipe.base.toNetwork(buf);
            recipe.addition.toNetwork(buf);
            buf.writeUtf(recipe.attribute.getDescriptionId());
            buf.writeUtf(recipe.modifier.getName());
            buf.writeFloat(((float) recipe.modifier.getAmount()));
            buf.writeUtf(recipe.modifier.getOperation().name());
            buf.writeUtf(recipe.slot.getName());
        }
    }
}
