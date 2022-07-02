package com.zygzag.revamp.common.item.recipe;

import com.zygzag.revamp.common.item.iridium.ISocketable;
import com.zygzag.revamp.common.item.iridium.Socket;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SocketRemoveRecipe extends CustomRecipe {
    public SocketRemoveRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer container, Level world) {
        int size = container.getContainerSize();
        ItemStack item = null;
        for (int i = 0; i < size; i++) {
            ItemStack loop = container.getItem(i);
            if (loop != ItemStack.EMPTY) {
                if (item == null) item = loop;
                else return false;
            }
        }
        if (item == null) return false;
        return item.getItem() instanceof ISocketable s && s.getSocket() != Socket.NONE;
    }

    @Override
    public ItemStack assemble(CraftingContainer container) {
        int size = container.getContainerSize();
        ItemStack item = null;
        for (int i = 0; i < size; i++) {
            ItemStack loop = container.getItem(i);
            if (loop != ItemStack.EMPTY) {
                if (item == null) {
                    item = loop;
                }
                else return ItemStack.EMPTY;
            }
        }
        if (item == null) return ItemStack.EMPTY;
        if (!(item.getItem() instanceof ISocketable socketable)) return ItemStack.EMPTY;
        ItemStack stack = socketable.getSocketlessForm().getDefaultInstance();
        stack.setTag(item.getOrCreateTag());
        return stack;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x * y >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Registry.RecipeSerializerRegistry.SOCKET_REMOVE_CRAFTING.get();
    }
}
