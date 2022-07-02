package com.zygzag.revamp.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChargeParticle extends TextureSheetParticle {
    private static final int LIFETIME_MIN = 16;
    private static final int LIFETIME_MAX = 32;
    private static final double MOTION_STDDEV = 0.0125;
    private static final float SHAKE_AMT = (float) ((1/4f) * Math.PI);
    private final float r;
    private final float r2;
    private RotationState state;
    private SpriteSet sprites;
    protected ChargeParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z);
        RandomSource rng = world.random;
        this.lifetime = rng.nextInt(LIFETIME_MIN, LIFETIME_MAX);
        /*this.xd = rng.nextGaussian(0, MOTION_STDDEV);
        this.yd = rng.nextGaussian(0, MOTION_STDDEV);
        this.zd = rng.nextGaussian(0, MOTION_STDDEV);*/
        this.sprites = sprites;
        if (sprites instanceof ParticleEngine.MutableSpriteSet mss) setSprite(mss.sprites.get(0));
        else setSprite(sprites.get(rng));
        r = rng.nextFloat() * (float) (Math.PI * 2);
        r2 = r + (rng.nextBoolean() ? -1 : 1) * SHAKE_AMT;
        roll = r;
        state = RotationState.MIN;
    }

    public int getFrame() {
        if (removed) return 0;
        int frametime = 2;
        if (age < 4 * frametime) return age / frametime;
        else if (age >= lifetime - 4 * frametime) return (lifetime - age) / frametime;
        return 4;
    }

    public void render(VertexConsumer consumer, Camera cam, float partialTicks) { // yes, copied decompiled code (mostly)
        Vec3 vec3 = cam.getPosition();
        float f = (float)(Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        float f4 = this.getQuadSize(partialTicks);
        Quaternion quaternion;
        if (this.roll == 0.0F) {
            quaternion = cam.rotation();
        } else {
            quaternion = new Quaternion(cam.rotation());
            float f3 = Mth.lerp(partialTicks, this.oRoll, this.roll);

            quaternion.mul(Vector3f.ZP.rotation(f3));
        }

        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        Vector3f rotationPoint = new Vector3f(3f / 8, 3f / 8, 0);
        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.sub(rotationPoint);
            vector3f.transform(quaternion);
            vector3f.add(rotationPoint);
            vector3f.mul(f4);
            vector3f.add(f, f1, f2);
        }

        float f7 = this.getU0();
        float f8 = this.getU1();
        float f5 = this.getV0();
        float f6 = this.getV1();
        int j = this.getLightColor(partialTicks);
        consumer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }

    @Override
    public void tick() {
        super.tick();
        if (sprites instanceof ParticleEngine.MutableSpriteSet m) {
            int s = getFrame();
            setSprite(m.sprites.get(s));
        } else pickSprite(sprites);
        oRoll = roll;
        roll = r();
        state = state.next();
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    private float r() {
        if (state == RotationState.MAX) return r2;
        else if (state == RotationState.MIN) return r;
        else return (r + r2) / 2;
    }

    @Override
    protected int getLightColor(float idk) {
        return 0xffffff;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double dx, double dy, double deez) {
            ChargeParticle p = new ChargeParticle(world, x, y, z, sprites);
            return p;
        }
    }

    enum RotationState {
        MIN,
        MID_UP,
        MAX,
        MID_DOWN;
        public RotationState next() {
            return switch (this) {
                case MAX -> MID_DOWN;
                case MIN -> MID_UP;
                case MID_UP -> MAX;
                case MID_DOWN -> MIN;
            };
        }
    }
}
