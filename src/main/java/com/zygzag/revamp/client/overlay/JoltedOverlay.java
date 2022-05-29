package com.zygzag.revamp.client.overlay;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.util.Constants;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

public class JoltedOverlay implements IIngameOverlay {
    public enum PosOrNeg {POSITIVE, NEGATIVE}
    public static ResourceLocation getLoc(int frame, PosOrNeg posOrNeg) {
        return Revamp.loc("textures/misc/jolted_" + posOrNeg.toString().toLowerCase() + "_" + frame + ".png");
    }
    private int counter = 0;

    @Override
    public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height) {
        Player player = Minecraft.getInstance().player;
        counter++;
        if (counter == Constants.JOLTED_NUM_FRAMES * Constants.JOLTED_FRAMETIME) counter = 0;
        if (player != null) {
            GeneralUtil.ifCapability(player, Revamp.ENTITY_CHARGE_CAPABILITY, (handler) -> {
                float charge = handler.getCharge();
                if (charge > 0) {
                    gui.renderTextureOverlay(getLoc(counter / Constants.JOLTED_FRAMETIME, PosOrNeg.POSITIVE), charge / handler.getMaxCharge());
                } else if (charge < 0) {
                    gui.renderTextureOverlay(getLoc(counter / Constants.JOLTED_FRAMETIME, PosOrNeg.NEGATIVE), -charge / handler.getMaxCharge());
                }
            });
        }
    }
}
