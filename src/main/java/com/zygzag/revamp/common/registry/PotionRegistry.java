package com.zygzag.revamp.common.registry;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.zygzag.revamp.common.Revamp.MODID;

public class PotionRegistry extends Registry<Potion> {
    public static final PotionRegistry INSTANCE = new PotionRegistry(DeferredRegister.create(ForgeRegistries.POTIONS, MODID), MODID);
    public static final RegistryObject<Potion> RAGE_POTION = INSTANCE.register("rage", () -> new Potion("rage", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 800, 1), new MobEffectInstance(MobEffects.DAMAGE_BOOST, 800, 1)));
    public static final RegistryObject<Potion> LONG_RAGE_POTION = INSTANCE.register("long_rage", () -> new Potion("long_rage", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1600, 1), new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1600, 1)));
    public static final RegistryObject<Potion> STRONG_RAGE_POTION = INSTANCE.register("strong_rage", () -> new Potion("strong_rage", new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 750, 2), new MobEffectInstance(MobEffects.DAMAGE_BOOST, 750, 2)));

    public static final RegistryObject<Potion> SIGHT_POTION = INSTANCE.register("sight", () -> new Potion("sight", new MobEffectInstance(MobEffectRegistry.SIGHT_EFFECT.get(), 100, 0)));
    public static final RegistryObject<Potion> LONG_SIGHT_POTION = INSTANCE.register("long_sight", () -> new Potion("long_sight", new MobEffectInstance(MobEffectRegistry.SIGHT_EFFECT.get(), 200, 0)));
    public static final RegistryObject<Potion> STRONG_SIGHT_POTION = INSTANCE.register("strong_sight", () -> new Potion("strong_sight", new MobEffectInstance(MobEffectRegistry.SIGHT_EFFECT.get(), 100, 1)));

    public PotionRegistry(DeferredRegister<Potion> register, String modid) {
        super(register, modid);
    }

    public static void bootstrapPotionRecipes() {
        ItemStack strengthPot = Items.POTION.getDefaultInstance();
        ItemStack strengthLongPot = Items.POTION.getDefaultInstance();
        ItemStack strengthStrongPot = Items.POTION.getDefaultInstance();
        ItemStack speedPot = Items.POTION.getDefaultInstance();
        ItemStack speedLongPot = Items.POTION.getDefaultInstance();
        ItemStack speedStrongPot = Items.POTION.getDefaultInstance();
        ItemStack ragePot = Items.POTION.getDefaultInstance();
        ItemStack rageStrongPot = Items.POTION.getDefaultInstance();
        ItemStack rageLongPot = Items.POTION.getDefaultInstance();
        ItemStack splashRagePot = Items.SPLASH_POTION.getDefaultInstance();
        ItemStack splashRageStrongPot = Items.SPLASH_POTION.getDefaultInstance();
        ItemStack splashRageLongPot = Items.SPLASH_POTION.getDefaultInstance();
        ItemStack lingeringRagePot = Items.LINGERING_POTION.getDefaultInstance();
        ItemStack lingeringRageStrongPot = Items.LINGERING_POTION.getDefaultInstance();
        ItemStack lingeringRageLongPot = Items.LINGERING_POTION.getDefaultInstance();
        PotionUtils.setPotion(strengthPot, Potions.STRENGTH);
        PotionUtils.setPotion(strengthStrongPot, Potions.STRONG_STRENGTH);
        PotionUtils.setPotion(strengthLongPot, Potions.LONG_STRENGTH);
        PotionUtils.setPotion(speedPot, Potions.SWIFTNESS);
        PotionUtils.setPotion(speedStrongPot, Potions.STRONG_SWIFTNESS);
        PotionUtils.setPotion(speedLongPot, Potions.LONG_SWIFTNESS);
        PotionUtils.setPotion(ragePot, PotionRegistry.RAGE_POTION.get());
        PotionUtils.setPotion(rageStrongPot, PotionRegistry.STRONG_RAGE_POTION.get());
        PotionUtils.setPotion(rageLongPot, PotionRegistry.LONG_RAGE_POTION.get());
        PotionUtils.setPotion(splashRagePot, PotionRegistry.RAGE_POTION.get());
        PotionUtils.setPotion(splashRageStrongPot, PotionRegistry.STRONG_RAGE_POTION.get());
        PotionUtils.setPotion(splashRageLongPot, PotionRegistry.LONG_RAGE_POTION.get());
        PotionUtils.setPotion(lingeringRagePot, PotionRegistry.RAGE_POTION.get());
        PotionUtils.setPotion(lingeringRageStrongPot, PotionRegistry.STRONG_RAGE_POTION.get());
        PotionUtils.setPotion(lingeringRageLongPot, PotionRegistry.LONG_RAGE_POTION.get());

        ItemStack sightPot = Items.POTION.getDefaultInstance();
        ItemStack sightLongPot = Items.POTION.getDefaultInstance();
        ItemStack sightStrongPot = Items.POTION.getDefaultInstance();
        ItemStack splashSightPot = Items.SPLASH_POTION.getDefaultInstance();
        ItemStack splashSightLongPot = Items.SPLASH_POTION.getDefaultInstance();
        ItemStack splashSightStrongPot = Items.SPLASH_POTION.getDefaultInstance();
        ItemStack lingeringSightPot = Items.LINGERING_POTION.getDefaultInstance();
        ItemStack lingeringSightLongPot = Items.LINGERING_POTION.getDefaultInstance();
        ItemStack lingeringSightStrongPot = Items.LINGERING_POTION.getDefaultInstance();
        ItemStack awkwardPot = Items.POTION.getDefaultInstance();
        PotionUtils.setPotion(sightPot, PotionRegistry.SIGHT_POTION.get());
        PotionUtils.setPotion(splashSightPot, PotionRegistry.SIGHT_POTION.get());
        PotionUtils.setPotion(lingeringSightPot, PotionRegistry.SIGHT_POTION.get());
        PotionUtils.setPotion(sightLongPot, PotionRegistry.LONG_SIGHT_POTION.get());
        PotionUtils.setPotion(splashSightLongPot, PotionRegistry.LONG_SIGHT_POTION.get());
        PotionUtils.setPotion(lingeringSightLongPot, PotionRegistry.LONG_SIGHT_POTION.get());
        PotionUtils.setPotion(sightStrongPot, PotionRegistry.STRONG_SIGHT_POTION.get());
        PotionUtils.setPotion(splashSightStrongPot, PotionRegistry.STRONG_SIGHT_POTION.get());
        PotionUtils.setPotion(lingeringSightStrongPot, PotionRegistry.STRONG_SIGHT_POTION.get());
        PotionUtils.setPotion(awkwardPot, Potions.AWKWARD);

        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(strengthPot),
                Ingredient.of(Items.END_STONE.getDefaultInstance()),
                ragePot
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(strengthLongPot),
                Ingredient.of(Items.END_STONE.getDefaultInstance()),
                rageLongPot
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(strengthStrongPot),
                Ingredient.of(Items.END_STONE.getDefaultInstance()),
                rageStrongPot
        );

        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(speedPot),
                Ingredient.of(Items.END_STONE.getDefaultInstance()),
                ragePot
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(speedLongPot),
                Ingredient.of(Items.END_STONE.getDefaultInstance()),
                rageLongPot
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(speedStrongPot),
                Ingredient.of(Items.END_STONE.getDefaultInstance()),
                rageStrongPot
        );

        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(ragePot),
                Ingredient.of(Items.GUNPOWDER.getDefaultInstance()),
                splashRagePot
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(rageStrongPot),
                Ingredient.of(Items.GUNPOWDER.getDefaultInstance()),
                splashRageStrongPot
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(rageLongPot),
                Ingredient.of(Items.GUNPOWDER.getDefaultInstance()),
                splashRageLongPot
        );

        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(splashRagePot),
                Ingredient.of(Items.DRAGON_BREATH.getDefaultInstance()),
                lingeringRagePot
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(splashRageLongPot),
                Ingredient.of(Items.DRAGON_BREATH.getDefaultInstance()),
                lingeringRageLongPot
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(splashRageStrongPot),
                Ingredient.of(Items.DRAGON_BREATH.getDefaultInstance()),
                lingeringRageStrongPot
        );

        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(awkwardPot),
                Ingredient.of(Items.AMETHYST_CLUSTER),
                sightPot
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(sightPot),
                Ingredient.of(Items.REDSTONE),
                sightLongPot
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(sightPot),
                Ingredient.of(Items.GLOWSTONE),
                sightStrongPot
        );

        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(sightPot),
                Ingredient.of(Items.GUNPOWDER),
                splashSightPot
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(sightLongPot),
                Ingredient.of(Items.GUNPOWDER),
                splashSightLongPot
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(sightStrongPot),
                Ingredient.of(Items.GUNPOWDER),
                splashSightStrongPot
        );

        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(splashSightPot),
                Ingredient.of(Items.DRAGON_BREATH),
                lingeringSightPot
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(splashSightLongPot),
                Ingredient.of(Items.DRAGON_BREATH),
                lingeringSightLongPot
        );
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(splashSightStrongPot),
                Ingredient.of(Items.DRAGON_BREATH),
                lingeringSightStrongPot
        );
    }
}
