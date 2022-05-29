package com.zygzag.revamp.common.charge;

import com.mojang.datafixers.util.Pair;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.networking.RevampPacketHandler;
import com.zygzag.revamp.common.networking.packet.ClientboundArcCreationPacket;
import com.zygzag.revamp.util.Constants;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Arc {
    public Vec3 start;
    public Vec3 end;
    public int between;
    public int lifetime;

    public Arc(Vec3 start, Vec3 end, int lifetime) {
        this(start, end, 4, lifetime);
    }

    public Arc(Vec3 start, Vec3 end, int between, int lifetime) {
        this.start = start;
        this.end = end;
        this.between = between;
        this.lifetime = lifetime;
    }

    public void sendToClients() {
        RevampPacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new ClientboundArcCreationPacket(this));
    }

    public double naiveLength() {
        return start.distanceTo(end);
    }

    public List<Vec3> makePoints(boolean bezier, long seed) {
        return bezier ? makeBezierPoints() : makePoints();
    }

    public List<Vec3> makePoints() {
        List<Vec3> list = new ArrayList<>();
        Random rng = new Random(currentSeed);
        for (double i = 0; i <= 1; i += 1.0 / between) {
            list.add(GeneralUtil.lerp(start, end, i).add(GeneralUtil.randVectorNormalized(rng).scale(Constants.ARC_RANDOMNESS * (-2 * (i * i - i)))));
        }
        //System.out.println(list);
        return list;
    }

    public List<Vec3> makeBezierPoints(int n) {
        List<Vec3> pts = makePoints();
        List<Vec3> pts2 = new ArrayList<>();
        for (float i = 0; i <= 1; i += (1.0 / n)) {
            pts2.add(GeneralUtil.pointAlongBezier(pts, i));
        }
        //System.out.println(pts);
        return pts2;
    }

    public List<Vec3> makeBezierPoints() {
        return makeBezierPoints((int) (naiveLength() * 4) + 1);
    }

    public List<CircleGeneratorFunction> joints(boolean bezier) {
        List<CircleGeneratorFunction> list = new ArrayList<>();
        List<Vec3> pts = bezier ? makeBezierPoints() : makePoints();
        for (int i = 1; i < pts.size(); i++) {
            Vec3 a = pts.get(i - 1);
            Vec3 b = pts.get(i);
            Pair<CircleGeneratorFunction, CircleGeneratorFunction> pair = boundFunctions(a, b, 0.0625);
            list.add(pair.getFirst());
            list.add(pair.getSecond());
        }
        return list;
    }

    public List<Quad3D> joints(boolean bezier, double theta) {
        List<CircleGeneratorFunction> joints = joints(bezier);
        List<Quad3D> jointQuads = new ArrayList<>();
        for (CircleGeneratorFunction f : joints) {
            jointQuads.add(f.generateSqaure(theta));
        }
        List<Quad3D> other = new ArrayList<>();
        other.add(jointQuads.get(0));
        for (int i = 1; i < jointQuads.size() - 1; i += 2) {
            Quad3D a = jointQuads.get(i);
            Quad3D b = jointQuads.get(i + 1);
            other.add(a.averagedWith(b));
        }
        other.add(jointQuads.get(jointQuads.size() - 1));
        return other;
    }

    public static Pair<CircleGeneratorFunction, CircleGeneratorFunction> boundFunctions(Vec3 a, Vec3 b, double sideLength) {
        CircleGeneratorFunction f1 = circleAtPointPerpendicularToVector(a, b.subtract(a), sideLength * Math.sqrt(2));
        CircleGeneratorFunction f2 = circleAtPointPerpendicularToVector(b, a.subtract(b), sideLength * Math.sqrt(2));
        return new Pair<>(f1, f2);
    }

    public static CircleGeneratorFunction circleAtPointPerpendicularToVector(Vec3 origin, Vec3 normal, double radius) {
        Pair<Vec3, Vec3> uv = getPerpendiculars(normal);
        //System.out.println("normal: " + normal);
        Vec3 u = uv.getFirst();
        Vec3 v = uv.getSecond();
        return (theta) -> {
            Vec3 k = origin.add(u.scale(Math.cos(theta) * radius).add(v.scale(Math.sin(theta) * radius)));
            return k;
        };
    }

    public static Pair<Vec3, Vec3> getPerpendiculars(Vec3 vec) {
        double length = vec.length(); // length of Q
        double sigma = Math.signum(vec.x) * length;    // copysign( l, Q[0]) if you have it
        double h = vec.x + sigma;   // first component of householder vector
        double beta = -1 / (sigma * h);  // householder scale
        // apply to (0,1,0)'
        double f = beta * vec.y;
        Vec3 u = new Vec3(f * h, 1 + f * vec.y, f * vec.z);
        double g = beta * vec.z;
        Vec3 v = new Vec3(g * h, g * vec.y, 1 + g * vec.z);
        return new Pair<>(u.normalize(), v.normalize());
    }

    public long currentSeed;

    public List<Quad3D> getQuadsForRendering(double scale, int tick) {
        return getBorderQuads(scale);
    }
    public List<Quad3D> getBorderQuads(double scale) {
        List<Quad3D> border = new ArrayList<>();
        List<Quad3D> list = joints(Constants.RENDER_BEZIER_ARCS, new Random(currentSeed).nextDouble());
        //System.out.println(list.get(0).scale(scale) + ", scale: " + scale);
        for (int i = 1; i < list.size(); i++) {
            Quad3D a = list.get(i - 1).scale(scale);
            Quad3D b = list.get(i).alignTo(a).scale(scale);

            border.add(new Quad3D(b.a(), b.b(), a.b(), a.a()));
            border.add(new Quad3D(b.b(), b.c(), a.c(), a.b()));
            border.add(new Quad3D(b.c(), b.d(), a.d(), a.c()));
            border.add(new Quad3D(b.d(), b.a(), a.a(), a.d()));
        }
        return border;
    }

    public void tick() {
        lifetime--;
        if (lifetime <= 0) remove();
        if (lifetime % 4 == 0) currentSeed = GeneralUtil.RANDOM.nextLong();
    }

    public void remove() {
        Level world = Minecraft.getInstance().level;
        if (world != null) {
            GeneralUtil.ifCapability(world, Revamp.ARC_CAPABILITY, (handler) -> handler.arcs.remove(this));
        }
    }
}
