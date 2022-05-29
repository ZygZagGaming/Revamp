package com.zygzag.revamp.common.charge;

import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.SliceShape;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public record Quad3D(Vec3 a, Vec3 b, Vec3 c, Vec3 d) {
    public Vec3 normal() {
        Vec3 u = a.subtract(b);
        Vec3 v = a.subtract(c);
        return u.cross(v);
    }

    public double distanceFrom(Quad3D other) {
        return a.distanceTo(other.a) + b.distanceTo(other.b) + c.distanceTo(other.c) + d.distanceTo(other.d);
    }

    public Quad3D rotatePoints(int k) {
        List<Vec3> pts = toList();
        return fromList(GeneralUtil.rotate(pts, k));
    }

    public Quad3D alignTo(Quad3D other) {
        double min = distanceFrom(other);
        Quad3D minQuad = this;
        for (int index = 1; index < 4; index++) {
            Quad3D quad = rotatePoints(index);
            double d = quad.distanceFrom(other);
            if (min > d) {
                min = d;
                minQuad = quad;
            }
        }
        return minQuad;
    }

    public Quad3D averagedWith(Quad3D other) {
        Quad3D aligned = other.alignTo(this);
        return new Quad3D(a.add(aligned.a).scale(0.5), b.add(aligned.b).scale(0.5), c.add(aligned.c).scale(0.5), d.add(aligned.d).scale(0.5));
    }

    public Vec3 center() {
        return a.add(b).add(c).add(d).scale(0.25);
    }

    public List<Vec3> toList() {
        return List.of(a, b, c, d);
    }

    public static Quad3D fromList(List<Vec3> list) {
        return new Quad3D(list.get(0), list.get(1), list.get(2), list.get(3));
    }

    public Quad3D scale(double f) {
        Vec3 center = center();
        return fromList(toList().stream().map((it) -> it.subtract(center).scale(f).add(center)).toList());
    }

    @Override
    public String toString() {
        return a + "\n" + b + "\n" + c + "\n" + d;
    }

    public Quad3D reverse() {
        return new Quad3D(d, c, b, a);
    }
}
