package com.zygzag.revamp.common.item.recipe;

import com.zygzag.revamp.common.registry.ItemRegistry;
import com.zygzag.revamp.common.registry.RecipeSerializerRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EnderBookCopyRecipe extends CustomRecipe {
    public EnderBookCopyRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer container, Level world) {
        int size = container.getContainerSize();
        ItemStack enderBook = null, book = null;
        for (int i = 0; i < size; i++) {
            ItemStack loop = container.getItem(i);
            if (loop != ItemStack.EMPTY) {
                if (loop.is(ItemRegistry.ENDER_BOOK_ITEM.get())) {
                    if (enderBook == null) enderBook = loop;
                    else return false;
                } else if (loop.is(Items.WRITABLE_BOOK)) {
                    if (book == null) book = loop;
                    else return false;
                }
            }
        }
        return enderBook != null && book != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        int size = container.getContainerSize();
        ItemStack enderBook = null, book = null;
        for (int i = 0; i < size; i++) {
            ItemStack loop = container.getItem(i);
            if (loop != ItemStack.EMPTY) {
                if (loop.is(ItemRegistry.ENDER_BOOK_ITEM.get())) {
                    if (enderBook == null) enderBook = loop;
                    else return ItemStack.EMPTY;
                } else if (loop.is(Items.WRITABLE_BOOK)) {
                    if (book == null) book = loop;
                    else return ItemStack.EMPTY;
                }
            }
        }
        if (enderBook == null || book == null) return ItemStack.EMPTY;
        CompoundTag tag = enderBook.getOrCreateTag();
        if (!tag.contains("id")) return ItemStack.EMPTY;
        else {
            int id = tag.getInt("id");
            int gen = tag.getInt("generation");
            ItemStack toReturn = ItemRegistry.ENDER_BOOK_ITEM.get().getDefaultInstance();
            toReturn.getOrCreateTag().putInt("id", id);
            toReturn.getOrCreateTag().putInt("generation", Math.min(gen + 1, 3));
            return toReturn;
        }
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializerRegistry.ENDER_BOOK_COPY_CRAFTING.get();
    }
}
