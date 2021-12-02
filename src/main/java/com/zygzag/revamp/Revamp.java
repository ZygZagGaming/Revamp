package com.zygzag.revamp;

import com.zygzag.revamp.entity.EmpoweredWither;
import com.zygzag.revamp.registry.Registry;
import com.zygzag.revamp.render.EmpoweredWitherRenderer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.targets.FMLServerLaunchHandler;
// import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

@Mod("revamp")
@SuppressWarnings("unused")
public class Revamp {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "revamp";
    public static final Tag<Block> NEEDS_IRIDIUM_TOOL_TAG = BlockTags.getAllTags().getTag(new ResourceLocation("revamp:needs_iridium_tool"));
    public static final Tag<Item> SOCKETABLE_TAG = ItemTags.getAllTags().getTag(new ResourceLocation("revamp:can_be_socketed"));
    public static final HashMap<String, ResourceLocation> LOCATION_CACHE = new HashMap<>();

    public Revamp() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(this::processIMC);
        modEventBus.addListener(this::doClientStuff);
        modEventBus.addListener(this::registerAttributes);
        Registry.register(modEventBus);

        // forgeEventBus.addListener(this::doServerStuff);

        forgeEventBus.register(this);
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
        });
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {

    }

    private void processIMC(final InterModProcessEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        EntityRenderers.register(Registry.EMPOWERED_WITHER.get(), EmpoweredWitherRenderer::new);
        EntityRenderers.register(Registry.TRANSMUTATION_BOTTLE_ENTITY.get(), ThrownItemRenderer::new);
    }

    private void registerAttributes(final EntityAttributeCreationEvent evt) {
        evt.put(Registry.EMPOWERED_WITHER.get(), EmpoweredWither.createAttributes().build());
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
