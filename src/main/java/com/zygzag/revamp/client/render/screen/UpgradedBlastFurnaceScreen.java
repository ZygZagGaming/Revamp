package com.zygzag.revamp.client.render.screen;

import com.zygzag.revamp.common.block.menu.UpgradedBlastFurnaceMenu;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UpgradedBlastFurnaceScreen extends AbstractFurnaceScreen<UpgradedBlastFurnaceMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/blast_furnace.png");

    public UpgradedBlastFurnaceScreen(UpgradedBlastFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, new UpgradedBlastingRecipeBookComponent(), inventory, title, TEXTURE);
    }
}
