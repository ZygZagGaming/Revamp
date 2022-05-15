package com.zygzag.revamp.common;

import com.zygzag.revamp.client.render.screen.UpgradedBlastFurnaceScreen;
import com.zygzag.revamp.common.entity.EmpoweredWither;
import com.zygzag.revamp.common.entity.RevampedBlaze;
import com.zygzag.revamp.common.item.recipe.ModRecipeType;
import com.zygzag.revamp.common.item.recipe.TransmutationRecipe;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotTypeMessage;

import java.util.ArrayList;
import java.util.HashMap;

@Mod("revamp")
@SuppressWarnings("unused")
public class Revamp {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "revamp";
    public static final TagKey<Block> NEEDS_IRIDIUM_TOOL_TAG = BlockTags.create(new ResourceLocation("revamp:needs_iridium_tool"));
    public static final TagKey<Item> SOCKETABLE_TAG = ItemTags.create(new ResourceLocation("revamp:can_be_socketed"));
    public static final HashMap<String, ResourceLocation> LOCATION_CACHE = new HashMap<>();
    public static final Capability<RevampedBlaze.Rods> RODS_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public Revamp() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(this::processIMC);
        modEventBus.addListener(this::doClientStuff);
        modEventBus.addListener(this::registerAttributes);
        modEventBus.addListener(this::registerCapabilities);
        Registry.register(modEventBus);

        // forgeEventBus.addListener(this::doServerStuff);

        forgeEventBus.register(this);
        forgeEventBus.addGenericListener(Entity.class, this::attachCapabilities);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
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
            PotionUtils.setPotion(ragePot, Registry.RAGE_POTION.get());
            PotionUtils.setPotion(rageStrongPot, Registry.STRONG_RAGE_POTION.get());
            PotionUtils.setPotion(rageLongPot, Registry.LONG_RAGE_POTION.get());
            PotionUtils.setPotion(splashRagePot, Registry.RAGE_POTION.get());
            PotionUtils.setPotion(splashRageStrongPot, Registry.STRONG_RAGE_POTION.get());
            PotionUtils.setPotion(splashRageLongPot, Registry.LONG_RAGE_POTION.get());
            PotionUtils.setPotion(lingeringRagePot, Registry.RAGE_POTION.get());
            PotionUtils.setPotion(lingeringRageStrongPot, Registry.STRONG_RAGE_POTION.get());
            PotionUtils.setPotion(lingeringRageLongPot, Registry.LONG_RAGE_POTION.get());

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
            PotionUtils.setPotion(sightPot, Registry.SIGHT_POTION.get());
            PotionUtils.setPotion(splashSightPot, Registry.SIGHT_POTION.get());
            PotionUtils.setPotion(lingeringSightPot, Registry.SIGHT_POTION.get());
            PotionUtils.setPotion(sightLongPot, Registry.LONG_SIGHT_POTION.get());
            PotionUtils.setPotion(splashSightLongPot, Registry.LONG_SIGHT_POTION.get());
            PotionUtils.setPotion(lingeringSightLongPot, Registry.LONG_SIGHT_POTION.get());
            PotionUtils.setPotion(sightStrongPot, Registry.STRONG_SIGHT_POTION.get());
            PotionUtils.setPotion(splashSightStrongPot, Registry.STRONG_SIGHT_POTION.get());
            PotionUtils.setPotion(lingeringSightStrongPot, Registry.STRONG_SIGHT_POTION.get());
            PotionUtils.setPotion(awkwardPot, Potions.AWKWARD);

            // region strength to rage
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
            // endregion

            // region speed to rage
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
            // endregion

            // region other recipes
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

            // endregion
        });
        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(ResourceKey.create(ForgeRegistries.BIOMES.getRegistryKey(), loc("lava_gardens")), 1));
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("ring").build());
    }

    private void processIMC(final InterModProcessEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        RecipeType<TransmutationRecipe> r = ModRecipeType.TRANSMUTATION; // force initialization of interface
        MenuScreens.register(Registry.UPGRADED_BLAST_FURNACE_MENU.get(), UpgradedBlastFurnaceScreen::new);
    }

    private void registerAttributes(final EntityAttributeCreationEvent evt) {
        evt.put(Registry.EMPOWERED_WITHER.get(), EmpoweredWither.createAttributes().build());
        evt.put(Registry.REVAMPED_BLAZE.get(), RevampedBlaze.createAttributes().build());
    }

    private void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof RevampedBlaze p) {
            LazyOptional<RevampedBlaze.Rods> lazy = LazyOptional.of(() -> new RevampedBlaze.Rods(p));
            event.addCapability(loc("rods"), new ICapabilityProvider() {
                @NotNull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    if (cap == RODS_CAPABILITY) {
                        return lazy.cast();
                    }
                    return LazyOptional.empty();
                }
            });
        }
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(RevampedBlaze.Rods.class);
    }

    // private void doServerStuff(final FMLServerStartingEvent event) { }

    @MethodsReturnNonnullByDefault
    public static CreativeModeTab MAIN_TAB = new CreativeModeTab("revamp_main_tab") {
        @Override
        public ItemStack makeIcon() {
            return Registry.IRIDIUM_PLATING.get().getDefaultInstance();
        }
    };

    public static ResourceLocation loc(String s) {
        if (LOCATION_CACHE.containsKey(s)) return LOCATION_CACHE.get(s);
        ResourceLocation l = new ResourceLocation(MODID, s);
        LOCATION_CACHE.put(s, l);
        return l;
    }
}
