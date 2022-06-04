package com.zygzag.revamp.common;

import com.zygzag.revamp.client.overlay.JoltedOverlay;
import com.zygzag.revamp.client.render.screen.UpgradedBlastFurnaceScreen;
import com.zygzag.revamp.common.block.ArcCrystalBlock;
import com.zygzag.revamp.common.charge.ArcHandler;
import com.zygzag.revamp.common.charge.ChunkChargeHandler;
import com.zygzag.revamp.common.charge.ClientLevelChargeHandler;
import com.zygzag.revamp.common.charge.EntityChargeHandler;
import com.zygzag.revamp.common.data.ConductivenessReloadListener;
import com.zygzag.revamp.common.entity.EmpoweredWither;
import com.zygzag.revamp.common.entity.RevampedBlaze;
import com.zygzag.revamp.common.item.iridium.ISocketable;
import com.zygzag.revamp.common.item.recipe.ModRecipeType;
import com.zygzag.revamp.common.item.recipe.TransmutationRecipe;
import com.zygzag.revamp.common.networking.RevampPacketHandler;
import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
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
    public static final Capability<EntityChargeHandler> ENTITY_CHARGE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ChunkChargeHandler> CHUNK_CHARGE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ClientLevelChargeHandler> CLIENT_LEVEL_CHARGE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ArcHandler> ARC_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    public static final ConductivenessReloadListener CONDUCTIVENESS = new ConductivenessReloadListener();

    public Revamp() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(this::processIMC);
        modEventBus.addListener(this::doClientStuff);
        modEventBus.addListener(this::registerAttributes);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerRenderers);
        modEventBus.addListener(this::registerBlockColorHandlers);
        Registry.register(modEventBus);

        // forgeEventBus.addListener(this::doServerStuff);

        forgeEventBus.register(this);
        forgeEventBus.addListener(this::addReloadListeners);
        forgeEventBus.addGenericListener(Entity.class, this::attachCapabilitiesToEntities);
        forgeEventBus.addGenericListener(LevelChunk.class, this::attachCapabilitiesToChunks);
        forgeEventBus.addGenericListener(Level.class, this::attachCapabilitiesToLevels);
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
            PotionUtils.setPotion(ragePot, Registry.PotionRegistry.RAGE_POTION.get());
            PotionUtils.setPotion(rageStrongPot, Registry.PotionRegistry.STRONG_RAGE_POTION.get());
            PotionUtils.setPotion(rageLongPot, Registry.PotionRegistry.LONG_RAGE_POTION.get());
            PotionUtils.setPotion(splashRagePot, Registry.PotionRegistry.RAGE_POTION.get());
            PotionUtils.setPotion(splashRageStrongPot, Registry.PotionRegistry.STRONG_RAGE_POTION.get());
            PotionUtils.setPotion(splashRageLongPot, Registry.PotionRegistry.LONG_RAGE_POTION.get());
            PotionUtils.setPotion(lingeringRagePot, Registry.PotionRegistry.RAGE_POTION.get());
            PotionUtils.setPotion(lingeringRageStrongPot, Registry.PotionRegistry.STRONG_RAGE_POTION.get());
            PotionUtils.setPotion(lingeringRageLongPot, Registry.PotionRegistry.LONG_RAGE_POTION.get());

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
            PotionUtils.setPotion(sightPot, Registry.PotionRegistry.SIGHT_POTION.get());
            PotionUtils.setPotion(splashSightPot, Registry.PotionRegistry.SIGHT_POTION.get());
            PotionUtils.setPotion(lingeringSightPot, Registry.PotionRegistry.SIGHT_POTION.get());
            PotionUtils.setPotion(sightLongPot, Registry.PotionRegistry.LONG_SIGHT_POTION.get());
            PotionUtils.setPotion(splashSightLongPot, Registry.PotionRegistry.LONG_SIGHT_POTION.get());
            PotionUtils.setPotion(lingeringSightLongPot, Registry.PotionRegistry.LONG_SIGHT_POTION.get());
            PotionUtils.setPotion(sightStrongPot, Registry.PotionRegistry.STRONG_SIGHT_POTION.get());
            PotionUtils.setPotion(splashSightStrongPot, Registry.PotionRegistry.STRONG_SIGHT_POTION.get());
            PotionUtils.setPotion(lingeringSightStrongPot, Registry.PotionRegistry.STRONG_SIGHT_POTION.get());
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
        RevampPacketHandler.registerMessages();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("ring").build());
    }

    private void processIMC(final InterModProcessEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        RecipeType<TransmutationRecipe> r = ModRecipeType.TRANSMUTATION; // force initialization of interface
        MenuScreens.register(Registry.MenuTypeRegistry.UPGRADED_BLAST_FURNACE_MENU.get(), UpgradedBlastFurnaceScreen::new);

        ItemBlockRenderTypes.setRenderLayer(Registry.BlockRegistry.LAVA_VINES_BLOCK.get(), RenderType.cutoutMipped());

        OverlayRegistry.registerOverlayTop("jolted", new JoltedOverlay());
    }

    private void registerAttributes(final EntityAttributeCreationEvent evt) {
        evt.put(Registry.EntityRegistry.EMPOWERED_WITHER.get(), EmpoweredWither.createAttributes().build());
        evt.put(Registry.EntityRegistry.REVAMPED_BLAZE.get(), RevampedBlaze.createAttributes().build());
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(RevampedBlaze.Rods.class);
        event.register(EntityChargeHandler.class);
        event.register(ChunkChargeHandler.class);
        event.register(ClientLevelChargeHandler.class);
        event.register(ArcHandler.class);
    }

    private void attachCapabilitiesToEntities(final AttachCapabilitiesEvent<Entity> event) {
        LazyOptional<EntityChargeHandler> lazy = LazyOptional.of(() -> new EntityChargeHandler(event.getObject(), 0f, 20f));
        event.addCapability(loc("entity_charge"), new ICapabilityProvider() {
            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == ENTITY_CHARGE_CAPABILITY) {
                    return lazy.cast();
                }
                return LazyOptional.empty();
            }
        });
    }

    private void attachCapabilitiesToChunks(final AttachCapabilitiesEvent<LevelChunk> event) {
        LazyOptional<ChunkChargeHandler> lazy = LazyOptional.of(() -> new ChunkChargeHandler(event.getObject()));
        event.addCapability(loc("chunk_charge"), new ICapabilityProvider() {
            @NotNull
            @Override
            public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == CHUNK_CHARGE_CAPABILITY) {
                    return lazy.cast();
                }
                return LazyOptional.empty();
            }
        });
    }

    private void attachCapabilitiesToLevels(final AttachCapabilitiesEvent<Level> event) {
        if (event.getObject().isClientSide) {
            LazyOptional<ClientLevelChargeHandler> lazy = LazyOptional.of(() -> new ClientLevelChargeHandler(event.getObject()));
            event.addCapability(loc("client_level_charge"), new ICapabilityProvider() {
                @NotNull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    if (cap == CLIENT_LEVEL_CHARGE_CAPABILITY) {
                        return lazy.cast();
                    }
                    return LazyOptional.empty();
                }
            });

            LazyOptional<ArcHandler> lazy2 = LazyOptional.of(() -> new ArcHandler(event.getObject()));
            event.addCapability(loc("arc"), new ICapabilityProvider() {
                @NotNull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    if (cap == ARC_CAPABILITY) {
                        return lazy2.cast();
                    }
                    return LazyOptional.empty();
                }
            });
        }
    }

    public void addReloadListeners(final AddReloadListenerEvent event) {
        event.addListener(CONDUCTIVENESS);
    }

    private void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {

    }

    private void registerBlockColorHandlers(final ColorHandlerEvent.Block event) {
        BlockColors colors = event.getBlockColors();
        colors.register(ArcCrystalBlock::getColor, Registry.BlockRegistry.ARC_CRYSTAL.get());
    }

    // private void doServerStuff(final FMLServerStartingEvent event) { }

    @MethodsReturnNonnullByDefault
    public static CreativeModeTab MAIN_TAB = new CreativeModeTab("revamp_main_tab") {
        @Override
        public ItemStack makeIcon() {
            return Registry.ItemRegistry.IRIDIUM_PLATING.get().getDefaultInstance();
        }
    };

    public static ResourceLocation loc(String s) {
        if (LOCATION_CACHE.containsKey(s)) return LOCATION_CACHE.get(s);
        ResourceLocation l = new ResourceLocation(MODID, s);
        LOCATION_CACHE.put(s, l);
        return l;
    }

    public static final EnchantmentCategory COOLDOWN_CATEGORY = EnchantmentCategory.create("cooldown", (item) -> item instanceof ISocketable socketable && socketable.hasCooldown());
}
