package com.zygzag.revamp.common;

import com.zygzag.revamp.client.overlay.JoltedOverlay;
import com.zygzag.revamp.common.block.ArcCrystalBlock;
import com.zygzag.revamp.common.capability.*;
import com.zygzag.revamp.common.charge.EnergyCharge;
import com.zygzag.revamp.common.data.ConductivenessReloadListener;
import com.zygzag.revamp.common.data.RevampRecipeDataProvider;
import com.zygzag.revamp.common.entity.EmpoweredWither;
import com.zygzag.revamp.common.entity.RevampedBlaze;
import com.zygzag.revamp.common.item.iridium.ISocketable;
import com.zygzag.revamp.common.networking.RevampPacketHandler;
import com.zygzag.revamp.common.registry.*;
import com.zygzag.revamp.common.tag.RevampTags;
import com.zygzag.revamp.common.world.RevampRegion;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.data.event.GatherDataEvent;
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
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import top.theillusivec4.curios.api.SlotTypeMessage;

import java.util.*;
import java.util.function.Supplier;

@Mod("revamp")
@SuppressWarnings("unused")
public class Revamp {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "revamp";
    public static final TagKey<Block> NEEDS_IRIDIUM_TOOL_TAG = BlockTags.create(new ResourceLocation("revamp:needs_iridium_tool"));
    public static final TagKey<Item> SOCKETABLE_TAG = ItemTags.create(new ResourceLocation("revamp:can_be_socketed"));
    public static final HashMap<String, ResourceLocation> LOCATION_CACHE = new HashMap<>();
    public static final Capability<EntityChargeHandler> ENTITY_CHARGE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ChunkChargeHandler> CHUNK_CHARGE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ClientLevelChargeHandler> CLIENT_LEVEL_CHARGE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<LevelConnectionTracker> LEVEL_CONNECTION_TRACKER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ServerLevelEnderBookHandler> SERVER_LEVEL_ENDER_BOOK_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ArcHandler> ARC_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final WoodType MAGMA_WOOD_TYPE = WoodType.register(WoodType.create("revamp:magma"));

    public static final Supplier<List<TagKey<Block>>> trackedTags = Lazy.of(() -> List.of( //TODO: make this a reload listener
            RevampTags.MAGMA_NYLIUM_CONNECTABLE.get()
    ));

    public static final ConductivenessReloadListener CONDUCTIVENESS = new ConductivenessReloadListener();
    private boolean isClient = false;

    public Revamp() {
        WoodType t = MAGMA_WOOD_TYPE; // load it early
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(this::processIMC);
        modEventBus.addListener(this::doClientStuff);
        modEventBus.addListener(this::registerAttributes);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerRenderers);
        if (isClient) modEventBus.addListener(this::registerBlockColorHandlers);
        Registry.register(modEventBus);

        // forgeEventBus.addListener(this::doServerStuff);

        forgeEventBus.register(this);
        forgeEventBus.addListener(this::addReloadListeners);
        modEventBus.addListener(this::gatherData);
        forgeEventBus.addGenericListener(Entity.class, this::attachCapabilitiesToEntities);
        forgeEventBus.addGenericListener(LevelChunk.class, this::attachCapabilitiesToChunks);
        forgeEventBus.addGenericListener(Level.class, this::attachCapabilitiesToLevels);
    }

    private static SurfaceRules.RuleSource makeSurfaceRules() {
        SurfaceRules.RuleSource magmaNylium = makeStateRule(BlockRegistry.MAGMA_NYLIUM_BLOCK.get());
        return SurfaceRules.ifTrue(
                SurfaceRules.isBiome(
                        BiomeRegistry.LAVA_GARDENS.getKey()
                ),
                SurfaceRules.sequence(
                        SurfaceRules.ifTrue(
                                SurfaceRules.ON_FLOOR,
                                SurfaceRules.ifTrue(
                                        SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.belowTop(5), 0)),
                                        magmaNylium
                                )
                        )
                )
        );
    }

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Regions.register(new RevampRegion(loc("nether"), 2));

            SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.NETHER, MODID, makeSurfaceRules());

            PotionRegistry.bootstrapPotionRecipes();
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
        isClient = true;

        event.enqueueWork(() -> {
            Sheets.addWoodType(MAGMA_WOOD_TYPE);
        });
    }

    private void registerOverlays(final RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("jolted", new JoltedOverlay());
    }

    private void registerAttributes(final EntityAttributeCreationEvent evt) {
        evt.put(EntityRegistry.EMPOWERED_WITHER.get(), EmpoweredWither.createAttributes().build());
        evt.put(EntityRegistry.REVAMPED_BLAZE.get(), RevampedBlaze.createAttributes().build());
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(EntityChargeHandler.class);
        event.register(ChunkChargeHandler.class);
        event.register(ClientLevelChargeHandler.class);
        event.register(ArcHandler.class);
    }

    private void attachCapabilitiesToEntities(final AttachCapabilitiesEvent<Entity> event) {
        LazyOptional<EntityChargeHandler> lazy = LazyOptional.of(() -> new EntityChargeHandler(event.getObject(), 0f, 20f));
        event.addCapability(loc("entity_charge"), new ICapabilitySerializable<CompoundTag>() {
            @Override
            public CompoundTag serializeNBT() {
                CompoundTag ct = new CompoundTag();
                lazy.resolve().ifPresent((handler) -> {
                    ct.putFloat("charge", handler.getCharge());
                    ct.putFloat("maxCharge", handler.getMaxCharge());
                });
                return ct;
            }

            @Override
            public void deserializeNBT(CompoundTag nbt) {
                lazy.resolve().ifPresent((handler) -> {
                    handler.setCharge(nbt.getFloat("charge"));
                    handler.setMaxCharge(nbt.getFloat("maxCharge"));
                });
            }

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
        event.addCapability(loc("chunk_charge"), new ICapabilitySerializable<CompoundTag>() {
            @Override
            public CompoundTag serializeNBT() {
                CompoundTag ct = new CompoundTag();
                ListTag positions = new ListTag();
                ListTag charges = new ListTag();
                lazy.resolve().ifPresent((handler) -> {
                    for (Map.Entry<BlockPos, EnergyCharge> entry : handler.charges.entrySet()) {
                        BlockPos bp = entry.getKey();
                        ListTag pos = new ListTag();
                        pos.add(DoubleTag.valueOf(bp.getX()));
                        pos.add(DoubleTag.valueOf(bp.getY()));
                        pos.add(DoubleTag.valueOf(bp.getZ()));
                        positions.add(pos);
                        charges.add(FloatTag.valueOf(entry.getValue().getCharge()));
                    }
                });
                ct.put("positions", positions);
                ct.put("charges", charges);
                return ct;
            }

            @Override
            public void deserializeNBT(CompoundTag nbt) {
                ListTag positions = nbt.getList("positions", Tag.TAG_LIST);
                ListTag charges = nbt.getList("charges", Tag.TAG_FLOAT);
                lazy.resolve().ifPresent((handler) -> {
                    for (int i = 0; i < positions.size() && i < charges.size(); i++) {
                        ListTag position = positions.getList(i);
                        float charge = charges.getFloat(i);
                        BlockPos pos = new BlockPos(position.getDouble(0), position.getDouble(1), position.getDouble(2));
                        handler.charges.put(pos, new EnergyCharge(charge, pos, handler));
                    }
                });
            }
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
        } else {
            LazyOptional<LevelConnectionTracker> contiguousLazy = LazyOptional.of(() -> new LevelConnectionTracker(event.getObject()));
            event.addCapability(loc("contiguous_section_tracker"), new ICapabilitySerializable<CompoundTag>() {
                @Override
                public CompoundTag serializeNBT() {
                    CompoundTag tag = new CompoundTag();
                    Optional<LevelConnectionTracker> op = contiguousLazy.resolve();
                    if (op.isPresent()) {
                        tag = op.get().serializeNBT();
                    }
                    return tag;
                }

                @Override
                public void deserializeNBT(CompoundTag nbt) {
                    Optional<LevelConnectionTracker> op = contiguousLazy.resolve();
                    op.ifPresent(handler -> handler.deserializeNBT(nbt));
                }

                @NotNull
                @Override
                public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    if (cap == LEVEL_CONNECTION_TRACKER_CAPABILITY) {
                        return contiguousLazy.cast();
                    }
                    return LazyOptional.empty();
                }
            });

            if (event.getObject().dimension() == Level.OVERWORLD) {
                LazyOptional<ServerLevelEnderBookHandler> lazy = LazyOptional.of(() -> new ServerLevelEnderBookHandler(event.getObject(), new ArrayList<>()));
                event.addCapability(loc("server_level_ender_book"), new ICapabilitySerializable<ListTag>() {
                    @Override
                    public ListTag serializeNBT() {
                        ListTag tag = new ListTag();
                        lazy.resolve().ifPresent((handler) -> {
                            for (Document doc : handler.documents) {
                                tag.add(doc.serializeNBT());
                            }
                        });
                        return tag;
                    }

                    @Override
                    public void deserializeNBT(ListTag nbt) {
                        List<Document> docs = new ArrayList<>();
                        lazy.resolve().ifPresent((handler) -> {
                            for (Tag elem : nbt) {
                                if (elem instanceof ListTag list) docs.add(Document.valueOf(list));
                            }
                            handler.documents = docs;
                        });
                    }

                    @NotNull
                    @Override
                    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                        if (cap == SERVER_LEVEL_ENDER_BOOK_CAPABILITY) {
                            return lazy.cast();
                        }
                        return LazyOptional.empty();
                    }
                });
            }
        }
    }

    public void addReloadListeners(final AddReloadListenerEvent event) {
        event.addListener(CONDUCTIVENESS);
    }

    private void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityTypeRegistry.CUSTOM_SIGN.get(), SignRenderer::new);
    }

    private void registerBlockColorHandlers(final RegisterColorHandlersEvent.Block event) {
        BlockColors colors = event.getBlockColors();
        colors.register(ArcCrystalBlock::getColor, BlockRegistry.ARC_CRYSTAL.get());
    }

    // private void doServerStuff(final FMLServerStartingEvent event) { }

    @MethodsReturnNonnullByDefault
    public static CreativeModeTab MAIN_TAB = new CreativeModeTab("revamp_main_tab") {
        @Override
        public ItemStack makeIcon() {
            return ItemRegistry.IRIDIUM_PLATING.get().getDefaultInstance();
        }
    };

    public static ResourceLocation loc(String s) {
        if (LOCATION_CACHE.containsKey(s)) return LOCATION_CACHE.get(s);
        ResourceLocation l = new ResourceLocation(MODID, s);
        LOCATION_CACHE.put(s, l);
        return l;
    }

    private void gatherData(final GatherDataEvent event) {
        DataGenerator datagen = event.getGenerator();
        datagen.addProvider(event.includeServer(), new RevampRecipeDataProvider(datagen));
    }

    public static final EnchantmentCategory COOLDOWN_CATEGORY = EnchantmentCategory.create("cooldown", (item) -> item instanceof ISocketable socketable && socketable.hasCooldown());
}
