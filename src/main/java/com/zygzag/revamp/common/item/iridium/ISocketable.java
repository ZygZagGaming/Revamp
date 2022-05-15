package com.zygzag.revamp.common.item.iridium;

import com.zygzag.revamp.common.registry.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public interface ISocketable {
    Socket getSocket();

    boolean hasCooldown();

    boolean hasUseAbility();

    default int getCooldown(ItemStack stack, Level world) {
        return 0;
    }

    static void addCooldown(Player player, ItemStack stack, int amount) {
        if (!player.getAbilities().instabuild) {
            int level = EnchantmentHelper.getItemEnchantmentLevel(Registry.EnchantmentRegistry.COOLDOWN_ENCHANTMENT.get(), stack);
            player.getCooldowns().addCooldown(stack.getItem(), amount / (level + 1));
        }
    }

    default Item getSocketlessForm() {
        if (this instanceof IridiumAxeItem) return Registry.IridiumGearRegistry.IRIDIUM_AXE.get();
        else if (this instanceof IridiumChestplateItem) return Registry.IridiumGearRegistry.IRIDIUM_CHESTPLATE.get();
        else if (this instanceof IridiumHoeItem) return Registry.IridiumGearRegistry.IRIDIUM_HOE.get();
        else if (this instanceof IridiumPickaxeItem) return Registry.IridiumGearRegistry.IRIDIUM_PICKAXE.get();
        else if (this instanceof IridiumScepterItem) return Registry.IridiumGearRegistry.IRIDIUM_SCEPTER.get();
        else if (this instanceof IridiumShovelItem) return Registry.IridiumGearRegistry.IRIDIUM_SHOVEL.get();
        else if (this instanceof IridiumSwordItem) return Registry.IridiumGearRegistry.IRIDIUM_SWORD.get();
        // else if (this instanceof IridiumRingItem) return Registry.IRIDIUM_RING.get(); // foreshadowing
        return Registry.ItemRegistry.IRIDIUM_PLATING.get(); // should never happen
    }
}
