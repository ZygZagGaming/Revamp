package com.zygzag.revamp.item.iridium;

import com.mojang.datafixers.util.Pair;
import com.zygzag.revamp.registry.Registry;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

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
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        Socket s = getSocket();
        Item i = s.i;
        MutableComponent m;
        if (s != Socket.NONE) {
            String str = s == Socket.AMETHYST ? "use" : "passive";
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
        }
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos blockpos = context.getClickedPos();
        Pair<Predicate<UseOnContext>, Consumer<UseOnContext>> pair = TILLABLES.get(level.getBlockState(blockpos).getBlock());
        if (context.getClickedFace() != Direction.DOWN && level.isEmptyBlock(blockpos.above())) {
            if (pair != null) {
                Predicate<UseOnContext> predicate = pair.getFirst();
                if (predicate.test(context)) {
                    int rand = (int) (Math.random() * 200.0);
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
                        if (player != null) e = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), i);
                        else e = new ItemEntity(level, blockpos.getX(), blockpos.getY(), blockpos.getZ(), i);
                        level.addFreshEntity(e);
                    }
                    return super.useOn(context);
                }
            }
        }

        return InteractionResult.PASS;
    }

    public void addCooldown(Player player, Item item, int amount, ItemStack stack) {
        int level = EnchantmentHelper.getItemEnchantmentLevel(Registry.COOLDOWN_ENCHANTMENT.get(), stack);
        player.getCooldowns().addCooldown(item, amount * (5 - level));
    }

}
