package com.zygzag.revamp.common.recipe;

import com.google.gson.JsonObject;
import com.zygzag.revamp.common.item.ISocketable;
import com.zygzag.revamp.common.item.SocketItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SocketRecipe extends SmithingRecipe implements IRecipe<IInventory> {

    public ResourceLocation id;
    public Ingredient item;

    public SocketRecipe(ResourceLocation id, Ingredient item) {
        super(id, item, Ingredient.EMPTY, ItemStack.EMPTY);
        this.item = item;
    }

    @Override
    public boolean matches(IInventory inv, World world) {
        return inv.getItem(0)
                .getItem() instanceof ISocketable &&
                item.test(
                        inv.getItem(0)
                ) && ((ISocketable) inv.getItem(0).getItem())
                .canApplySocket(inv.getItem(0), inv.getItem(1));
    }

    @Override
    public ItemStack assemble(IInventory inv) {
        return inv.getItem(0);
        /*ItemStack item = inv.getItem(0);
        if (item.getItem() instanceof ISocketable && this.item.test(item) && ((ISocketable) item.getItem()).canApplySocket(item, inv.getItem(1))) {
            ((ISocketable) item.getItem()).applySocket(item, inv.getItem(1));
            return item;
        }
        return inv.getItem(0); // Should never happen*/
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x + y >= 2;
    }

    @Override
    public ItemStack getResultItem() {
        return item.getItems()[0];
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return new SocketRecipe.Serializer();
    }

    @Override
    public IRecipeType<?> getType() {
        return IRecipeType.SMITHING;
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return stack.getItem() instanceof SocketItem;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SocketRecipe> {
        public SocketRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new SocketRecipe(id, Ingredient.fromJson(json.get("item")));
        }

        public SocketRecipe fromNetwork(ResourceLocation id, PacketBuffer buf) {
            return new SocketRecipe(id, Ingredient.fromNetwork(buf));
        }

        public void toNetwork(PacketBuffer buf, SocketRecipe recipe) {
            recipe.item.toNetwork(buf);
        }
    }
}
