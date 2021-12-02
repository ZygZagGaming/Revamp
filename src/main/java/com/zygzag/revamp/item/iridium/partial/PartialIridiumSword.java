package com.zygzag.revamp.item.iridium.partial;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class PartialIridiumSword extends SwordItem {
    int numberOfPlates;
    int maxNumOfPlates;
    public PartialIridiumSword(Tier tier, int damage, float speed, Properties properties, int maxNumOfPlates, int numberOfPlates) {
        super(tier, damage, speed, properties);
        this.numberOfPlates = numberOfPlates;
        this.maxNumOfPlates = maxNumOfPlates;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        super.appendHoverText(stack, world, text, flag);
        text.add(new TextComponent(""));
        MutableComponent plated = new TextComponent("Plated: ").withStyle(ChatFormatting.GRAY);
        plated.append(new TextComponent(numberOfPlates + " / " + maxNumOfPlates).withStyle(ChatFormatting.GOLD));
        text.add(plated);
    }
}
