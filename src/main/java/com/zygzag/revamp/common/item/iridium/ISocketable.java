package com.zygzag.revamp.common.item.iridium;

import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public interface ISocketable {
    Socket getSocket();

    boolean hasCooldown();

    boolean hasUseAbility();

    default int getCooldown() {
        return 0;
    }

    static void addCooldown(Player player, ItemStack stack, int amount) {
        if (!player.getAbilities().instabuild) {
            int level = EnchantmentHelper.getItemEnchantmentLevel(Registry.COOLDOWN_ENCHANTMENT.get(), stack);
            player.getCooldowns().addCooldown(stack.getItem(), amount / (level + 1));
        }
    }

    default Item getSocketlessForm() {
        if (this instanceof IridiumAxeItem) return Registry.IRIDIUM_AXE.get();
        else if (this instanceof IridiumChestplateItem) return Registry.IRIDIUM_CHESTPLATE.get();
        else if (this instanceof IridiumHoeItem) return Registry.IRIDIUM_HOE.get();
        else if (this instanceof IridiumPickaxeItem) return Registry.IRIDIUM_PICKAXE.get();
        else if (this instanceof IridiumScepterItem) return Registry.IRIDIUM_SCEPTER.get();
        else if (this instanceof IridiumShovelItem) return Registry.IRIDIUM_SHOVEL.get();
        else if (this instanceof IridiumSwordItem) return Registry.IRIDIUM_SWORD.get();
        // else if (this instanceof IridiumRingItem) return Registry.IRIDIUM_RING.get(); // foreshadowing
        return Registry.IRIDIUM_PLATING.get(); // should never happen
    }
}
