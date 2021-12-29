package com.zygzag.revamp.common.item.iridium;

import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class IridiumChestplateItem extends ArmorItem implements ISocketable {
    Socket socket;
    public IridiumChestplateItem(ArmorMaterial material, Properties properties, Socket socket) {
        super(material, EquipmentSlot.CHEST, properties);
        this.socket = socket;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        Socket s = getSocket();
        Item i = s.i;
        MutableComponent m;
        if (s != Socket.NONE) {
            MutableComponent t = new TranslatableComponent("socketed.revamp").withStyle(ChatFormatting.GRAY);
            t.append(new TextComponent(": ").withStyle(ChatFormatting.GRAY));
            t.append(((MutableComponent) i.getName(i.getDefaultInstance())).withStyle(ChatFormatting.GOLD));
            text.add(t);

            Socket socket = getSocket();
            text.add(new TextComponent(""));
            m = new TranslatableComponent("passive.revamp").withStyle(ChatFormatting.GRAY);
            m.append(new TextComponent( ": ").withStyle(ChatFormatting.GRAY));
            m.append(new TranslatableComponent("passive_ability.revamp.chestplate." + socket.name().toLowerCase()).withStyle(ChatFormatting.GOLD));
            text.add(m);
            text.add(new TranslatableComponent("description.passive_ability.revamp.chestplate." + socket.name().toLowerCase()));
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
        return false;
    }

    @Override
    public boolean hasUseAbility() {
        return false;
    }
}
