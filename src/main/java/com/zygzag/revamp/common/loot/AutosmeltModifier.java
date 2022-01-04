package com.zygzag.revamp.common.loot;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class AutosmeltModifier extends LootModifier {
    /**
     * Constructs a LootModifier.
     *
     * @param conditionsIn the ILootConditions that need to be matched before the loot is modified.
     */
    protected AutosmeltModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @NotNull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        Level world = context.getLevel();
        for (int i = 0; i < generatedLoot.size(); i++) {
            ItemStack stack = generatedLoot.get(i);
            SimpleContainer cont = new SimpleContainer(stack);
            Optional<SmeltingRecipe> opt = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, cont, world);
            if (opt.isPresent()) {
                ItemStack outStack = opt.get().assemble(cont);
                outStack.setCount(stack.getCount());
                generatedLoot.set(i, outStack);
            }
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<AutosmeltModifier> {

        @Override
        public AutosmeltModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] ailootcondition) {
            return new AutosmeltModifier(ailootcondition);
        }

        @Override
        public JsonObject write(AutosmeltModifier instance) {
            return makeConditions(instance.conditions);
        }
    }
}
