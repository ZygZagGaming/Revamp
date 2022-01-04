package com.zygzag.revamp.common.item.iridium;

import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.util.Pair;
import com.zygzag.revamp.common.registry.Registry;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IridiumHoeItem extends HoeItem implements ISocketable {
    Socket socket;
    public IridiumHoeItem(Tier tier, int damage, float speed, Properties properties, Socket socket) {
        super(tier, damage, speed, properties);
        this.socket = socket;
    }

    @Override
    public boolean hasUseAbility() {
        return hasCooldown();
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
            m.append(new TranslatableComponent( str + "_ability.revamp.hoe." + socket.name().toLowerCase()).withStyle(ChatFormatting.GOLD));
            text.add(m);
            text.add(new TranslatableComponent("description." + str + "_ability.revamp.hoe." + socket.name().toLowerCase()));
            if (hasCooldown()) {
                MutableComponent comp = new TranslatableComponent("revamp.cooldown").withStyle(ChatFormatting.GRAY);
                comp.append(new TextComponent(": ").withStyle(ChatFormatting.GRAY));
                comp.append(new TextComponent(Float.toString(getCooldown() / 20f) + " ").withStyle(ChatFormatting.GOLD));
                comp.append(new TranslatableComponent("revamp.seconds").withStyle(ChatFormatting.GRAY));
                text.add(comp);
            }
        }
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public boolean hasCooldown() {
        return socket == Socket.AMETHYST;
    }

    @Override
    public int getCooldown() {
        return socket == Socket.AMETHYST ? Constants.AMETHYST_HOE_COOLDOWN : 0;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        BlockPos blockpos = context.getClickedPos();
        Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = TILLABLES.get(world.getBlockState(blockpos).getBlock());
        if (context.getClickedFace() != Direction.DOWN && world.isEmptyBlock(blockpos.above())) {
            if (pair != null) {
                Predicate<UseOnContext> predicate = pair.getFirst();
                if (predicate.test(context)) {
                    switch (socket) {
                        case DIAMOND -> {
                            int rand = (int) (Math.random() * 2000.0);
                            if (rand >= 69 && rand <= 71) {
                                ItemStack i;
                                if (rand == 69) {
                                    i = Items.DIAMOND.getDefaultInstance();
                                } else if (rand == 70) {
                                    i = Items.EMERALD.getDefaultInstance();
                                } else {
                                    i = Items.AMETHYST_SHARD.getDefaultInstance();
                                    i.setCount((int) (Math.random() * 8));
                                }
                                ItemEntity e;
                                if (player != null)
                                    e = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), i);
                                else e = new ItemEntity(world, blockpos.getX(), blockpos.getY(), blockpos.getZ(), i);
                                world.addFreshEntity(e);
                            }
                            return super.useOn(context);
                        }
                        case EMERALD -> {
                            InteractionResult result = super.useOn(context);
                            if (!world.isClientSide) {
                                world.setBlock(blockpos, Registry.BLESSED_SOIL.get().defaultBlockState().setValue(FarmBlock.MOISTURE, world.getBlockState(blockpos).getValue(FarmBlock.MOISTURE)), 0);
                            }
                            return result;
                        }
                    }
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        if (socket == Socket.WITHER_SKULL && toolAction == ToolActions.SWORD_SWEEP) return true;
        return super.canPerformAction(stack, toolAction);
    }

    @NotNull
    @Override
    public AABB getSweepHitBox(@NotNull ItemStack stack, @NotNull Player player, @NotNull Entity target) {
        if (socket == Socket.WITHER_SKULL) return target.getBoundingBox().inflate(5f);
        return super.getSweepHitBox(stack, player, target);
    }
}
