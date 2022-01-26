package com.zygzag.revamp.client.render;

import com.zygzag.revamp.common.entity.ThrownAxe;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ThrownAxeRenderer extends ThrownItemRenderer<ThrownAxe> {
    public ThrownAxeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
