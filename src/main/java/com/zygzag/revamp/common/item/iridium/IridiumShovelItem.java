package com.zygzag.revamp.common.item.iridium;

import com.zygzag.revamp.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class IridiumShovelItem extends ShovelItem implements ISocketable {
    Socket socket;
    public IridiumShovelItem(Tier tier, float damage, float speed, Properties properties, Socket socket) {
        super(tier, damage, speed, properties);
        this.socket = socket;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        Socket s = getSocket();
        Item i = s.i;
        MutableComponent m;
        if (s != Socket.NONE) {
            String str = hasCooldown() ? "use" : "passive";
            MutableComponent t = new TranslatableComponent("socketed.revamp").withStyle(ChatFormatting.GRAY);
            t.append(new TextComponent(": ").withStyle(ChatFormatting.GRAY));
            t.append(((MutableComponent) i.getName(i.getDefaultInstance())).withStyle(ChatFormatting.GOLD));
            text.add(t);

            Socket socket = getSocket();
            text.add(new TextComponent(""));
            if (str.equals("passive")) m = new TranslatableComponent(str + ".revamp").withStyle(ChatFormatting.GRAY);
            else m = Minecraft.getInstance().options.keyUse.getKey().getDisplayName().copy().withStyle(ChatFormatting.GRAY);
            m.append(new TextComponent( ": ").withStyle(ChatFormatting.GRAY));
            m.append(new TranslatableComponent( str + "_ability.revamp.shovel." + socket.name().toLowerCase()).withStyle(ChatFormatting.GOLD));
            text.add(m);
            text.add(new TranslatableComponent("description." + str + "_ability.revamp.shovel." + socket.name().toLowerCase()));
            if (hasCooldown()) {
                MutableComponent comp = new TranslatableComponent("revamp.cooldown").withStyle(ChatFormatting.GRAY);
                comp.append(new TextComponent(": ").withStyle(ChatFormatting.GRAY));
                comp.append(new TextComponent(getCooldown() / 20f + " ").withStyle(ChatFormatting.GOLD));
                comp.append(new TranslatableComponent("revamp.seconds").withStyle(ChatFormatting.GRAY));
                text.add(comp);
            }
        }
    }

    @Override
    public boolean hasCooldown() {
        return socket == Socket.EMERALD || socket == Socket.SKULL || socket == Socket.WITHER_SKULL;
    }

    @Override
    public boolean hasUseAbility() {
        return hasCooldown();
    }

    @Override
    public int getCooldown() {
        switch (socket) {
            case EMERALD -> {
                return Constants.EMERALD_SHOVEL_COOLDOWN;
            }
            case SKULL -> {
                return Constants.SKULL_SHOVEL_COOLDOWN;
            }
            case WITHER_SKULL -> {
                return Constants.WITHER_SKULL_SHOVEL_COOLDOWN;
            }
        }
        return 0;
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    private static TagKey<Block> veinmineTag;

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity user) {
        if (veinmineTag == null) veinmineTag = BlockTags.create(new ResourceLocation("revamp:veinmine"));
        if (state.is(veinmineTag) && (!(user instanceof Player) || user.isShiftKeyDown() && !((Player) user).getCooldowns().isOnCooldown(this)) && stack.getItem() instanceof IridiumShovelItem shovel && shovel.getSocket() == Socket.DIAMOND) {
            int numDestroyed = 1;
            List<BlockPos> arr = Arrays.stream(getNeighboringBlocks(pos)).collect(Collectors.toList());
            if (level instanceof ServerLevel sLevel) {
                while (numDestroyed <= 64) {
                    ArrayList<BlockPos> tempList = new ArrayList<>();
                    for (BlockPos pos1 : arr) {
                        BlockState state1 = level.getBlockState(pos1);
                        if (state1.is(state.getBlock())) {
                            sLevel.destroyBlock(pos1, true, user);
                            BlockPos[] arr1 = getNeighboringBlocks(pos1);
                            tempList.addAll(Arrays.asList(arr1));
                            numDestroyed++;
                            if (numDestroyed == 64) break;
                        }
                    }
                    if (tempList.size() == 0) break;
                    arr = tempList;
                }
            }
            stack.hurtAndBreak(numDestroyed * 2, user, (e) -> { });
        }
        return super.mineBlock(stack, level, state, pos, user);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof IridiumShovelItem shovel) {
            switch (shovel.getSocket()) {
                case SKULL -> {
                    if (!player.getCooldowns().isOnCooldown(this)) {
                        double multiplier = 2;
                        if (!player.isOnGround()) multiplier = 0.5;
                        player.setDeltaMovement(player.getDeltaMovement().add(player.getLookAngle().multiply(multiplier, multiplier, multiplier)));
                        ISocketable.addCooldown(player, stack, Constants.SKULL_SHOVEL_COOLDOWN);
                        stack.hurtAndBreak(1, player, (e) -> {});
                        return InteractionResultHolder.consume(stack);
                    }
                }
                case EMERALD -> {
                    if (!world.isClientSide) {
                        BlockPos blockPos = player.blockPosition();
                        world.playSound(null, blockPos, SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 2.0F, 1.0F);
                        world.gameEvent(player, GameEvent.RING_BELL, blockPos);
                        BellBlockEntity.makeRaidersGlow(world, blockPos, world.getEntities(player, player.getBoundingBox().inflate(40.0), (e) -> e.getType().is(EntityTypeTags.RAIDERS)).stream().filter((e) -> e instanceof LivingEntity).map((e) -> (LivingEntity) e).collect(Collectors.toList()));
                        List<Villager> villagers = world.getEntitiesOfClass(Villager.class, player.getBoundingBox().inflate(40.0));
                        for (Villager villager : villagers) {
                            villager.getBrain().setActiveActivityIfPossible(Activity.HIDE);
                        }
                        ISocketable.addCooldown(player, stack, Constants.EMERALD_SHOVEL_COOLDOWN);
                        return InteractionResultHolder.consume(stack);
                    }
                }
                case WITHER_SKULL -> {
                    boolean isCrimson = Math.random() <= .75;
                    BlockState nylium = isCrimson ? Blocks.CRIMSON_NYLIUM.defaultBlockState() : Blocks.WARPED_NYLIUM.defaultBlockState();
                    for (int j = 0; j < 4; j++) {
                        BlockPos pos = player.blockPosition().below();
                        for (int i = 0; i < 64; i++) {
                            Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(player.getRandom());
                            if (world.getBlockState(pos).is(Blocks.GRASS_BLOCK)) {
                                world.setBlock(pos, nylium, 0);
                                if (Math.random() <= .45 && isAirLike(world.getBlockState(pos.above()))) {
                                    BlockState weeds = isCrimson ^ Math.random() <= .05 ? (Math.random() <= .1 ? Blocks.CRIMSON_FUNGUS.defaultBlockState() : Blocks.CRIMSON_ROOTS.defaultBlockState()) : (Math.random() <= .1 ? Blocks.WARPED_FUNGUS.defaultBlockState() : (Math.random() <= .33 ? Blocks.NETHER_SPROUTS.defaultBlockState() : Blocks.WARPED_ROOTS.defaultBlockState()));
                                    world.setBlock(pos.above(), weeds, 0);
                                }
                            } else if (world.getBlockState(pos).is(Blocks.SAND)) world.setBlock(pos, Blocks.SOUL_SAND.defaultBlockState(), 0);
                            else if (world.getBlockState(pos).is(Blocks.GRAVEL)) world.setBlock(pos, Blocks.SOUL_SOIL.defaultBlockState(), 0);
                            pos = pos.relative(d);
                            while (!isAirLike(world.getBlockState(pos))) pos = pos.above();
                            while (isAirLike(world.getBlockState(pos))) {
                                pos = pos.below();
                            }
                        }
                    }
                    ISocketable.addCooldown(player, stack, Constants.WITHER_SKULL_SHOVEL_COOLDOWN);
                    stack.hurtAndBreak(4, player, (e) -> { });

                    return InteractionResultHolder.consume(stack);
                }
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    private static boolean isAirLike(BlockState state) {
        return state.isAir() || state.is(Blocks.GRASS) || state.is(Blocks.TALL_GRASS);
    }

    private BlockPos[] getNeighboringBlocks(BlockPos pos) {
        BlockPos[] arr = new BlockPos[6];
        for (int i = 0; i < 6; i++) {
            arr[i] = pos.relative(Direction.values()[i]);
        }
        return arr;
    }
}
