package com.zygzag.revamp.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChargeParticle extends TextureSheetParticle {
    private static final int LIFETIME_MIN = 9;
    private static final int LIFETIME_MAX = 16;
    private static final double MOTION_STDDEV = 0.0125;
    protected ChargeParticle(ClientLevel world, double x, double y, double z) {
        super(world, x, y, z);
        Random rng = world.random;
        this.lifetime = rng.nextInt(LIFETIME_MIN, LIFETIME_MAX);
        this.xd = rng.nextGaussian(0, MOTION_STDDEV);
        this.yd = rng.nextGaussian(0, MOTION_STDDEV);
        this.zd = rng.nextGaussian(0, MOTION_STDDEV);
    }

    @Override
    public void render(VertexConsumer consumer, Camera cam, float d) {
        super.render(consumer, cam, d);
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double dx, double dy, double deez) {
            ChargeParticle p = new ChargeParticle(world, x, y, z);
            p.pickSprite(sprites);
            return p;
        }
    }
}
