package com.zygzag.revamp.common.item.iridium;

import com.zygzag.revamp.common.registry.EnchantmentRegistry;
import com.zygzag.revamp.util.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class IridiumSwordItem extends SwordItem implements ISocketable {
    Socket socket;
    public IridiumSwordItem(Tier tier, int damage, float speed, Properties properties, Socket socket) {
        super(tier, damage, speed, properties);
        this.socket = socket;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        Socket s = getSocket();
        Item i = s.i;
        MutableComponent m;
        if (s != Socket.NONE && world != null) {
            String str = hasCooldown() ? "use" : "passive";
            MutableComponent t = Component.translatable("socketed.revamp").withStyle(ChatFormatting.GRAY);
            t.append(Component.literal(": ").withStyle(ChatFormatting.GRAY));
            t.append(((MutableComponent) i.getName(i.getDefaultInstance())).withStyle(ChatFormatting.GOLD));
            text.add(t);

            Socket socket = getSocket();
            text.add(Component.literal(""));
            if (str.equals("passive")) m = Component.translatable(str + ".revamp").withStyle(ChatFormatting.GRAY);
            else m = Minecraft.getInstance().options.keyUse.getKey().getDisplayName().copy().withStyle(ChatFormatting.GRAY);
            m.append(Component.literal( ": ").withStyle(ChatFormatting.GRAY));
            m.append(Component.translatable( str + "_ability.revamp.sword." + socket.name().toLowerCase()).withStyle(ChatFormatting.GOLD));
            text.add(m);
            text.add(Component.translatable("description." + str + "_ability.revamp.sword." + socket.name().toLowerCase()));
            if (hasCooldown()) {
                MutableComponent comp = Component.translatable("revamp.cooldown").withStyle(ChatFormatting.GRAY);
                comp.append(Component.literal(": ").withStyle(ChatFormatting.GRAY));
                comp.append(Component.literal(getCooldown(stack, world) / 20f + " ").withStyle(ChatFormatting.GOLD));
                comp.append(Component.translatable("revamp.seconds").withStyle(ChatFormatting.GRAY));
                text.add(Component.literal("\n"));
                text.add(comp);
            }
        }
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (socket == Socket.WITHER_SKULL) {
            AABB aabb = new AABB(player.blockPosition().subtract(new Vec3i(6, 3, 6)), player.blockPosition().subtract(new Vec3i(-6, -3, -6)));
            List<Monster> monsters = world.getEntitiesOfClass(Monster.class, aabb, (m) -> m.getHealth() == 0.0);
            if (monsters.size() > 0) {
                player.heal(100f);
                ISocketable.addCooldown(player, stack, Constants.WITHER_SKULL_SWORD_COOLDOWN);
            }
        }
        return super.use(world, player, hand);
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public boolean hasUseAbility() {
        return hasCooldown();
    }

    @Override
    public boolean hasCooldown() {
        return socket == Socket.WITHER_SKULL;
    }

    @Override
    public int getCooldown(ItemStack stack, Level world) {
        int cooldownLevel = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.COOLDOWN_ENCHANTMENT.get(), stack);
        if (socket == Socket.WITHER_SKULL) return Constants.WITHER_SKULL_SWORD_COOLDOWN / (cooldownLevel + 1);
        else return 0;
    }
}
