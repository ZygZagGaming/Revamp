package com.zygzag.revamp.common.charge;

import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface CircleGeneratorFunction {
    Vec3 generate(double theta);

    default Quad3D generateSqaure(double theta) {
        return new Quad3D(generate(theta), generate(theta + Math.PI / 2), generate(theta + Math.PI), generate(theta + (3 * Math.PI / 2)));
    }
}