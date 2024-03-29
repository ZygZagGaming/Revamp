package com.zygzag.revamp.common.registry;

import com.zygzag.revamp.common.item.recipe.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.zygzag.revamp.common.Revamp.MODID;

public class RecipeSerializerRegistry extends Registry<RecipeSerializer<?>> {
    public static final RecipeSerializerRegistry INSTANCE = new RecipeSerializerRegistry(DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID), MODID);
    public static final RegistryObject<SimpleRecipeSerializer<ShulkerBowlRecipe>> SHULKER_BOWL_CRAFTING = INSTANCE.register("crafting_special_shulker_bowl", () -> new SimpleRecipeSerializer<>(ShulkerBowlRecipe::new));
    public static final RegistryObject<RecipeSerializer<TransmutationRecipe>> TRANSMUTATION_SERIALIZER = INSTANCE.register("transmutation", TransmutationRecipe.TransmutationSerializer::new);
    public static final RegistryObject<RecipeSerializer<EmpowermentRecipe>> EMPOWERMENT_SERIALIZER = INSTANCE.register("empowerment", EmpowermentRecipe.EmpowermentSerializer::new);
    public static final RegistryObject<SimpleRecipeSerializer<SocketRemoveRecipe>> SOCKET_REMOVE_CRAFTING = INSTANCE.register("crafting_special_socket_remove", () -> new SimpleRecipeSerializer<>(SocketRemoveRecipe::new));
    public static final RegistryObject<SimpleRecipeSerializer<EnderBookCopyRecipe>> ENDER_BOOK_COPY_CRAFTING = INSTANCE.register("crafting_special_ender_book_copy", () -> new SimpleRecipeSerializer<>(EnderBookCopyRecipe::new));

    private RecipeSerializerRegistry(DeferredRegister<RecipeSerializer<?>> register, String modid) {
        super(register, modid);
    }
}
