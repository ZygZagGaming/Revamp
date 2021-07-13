package com.zygzag.revamp.common.effect;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class EmpowermentEffect extends Effect {

    public EmpowermentEffect() {
        super(EffectType.BENEFICIAL, 7204316);
    }

    @Override
    public Effect addAttributeModifier(Attribute p_220304_1_, String p_220304_2_, double p_220304_3_, AttributeModifier.Operation p_220304_5_) {
        return super.addAttributeModifier(p_220304_1_, p_220304_2_, p_220304_3_, p_220304_5_);
    }
}
