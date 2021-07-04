package com.zygzag.revamp.util;

import net.minecraft.entity.EntityType;

public class EmpowermentData {
    public final EntityType<?> entity;
    public final int multiplier;
    public final int threshold;

    public EmpowermentData(EntityType<?> entity, int multiplier, int threshold) {
        this.entity = entity;
        this.multiplier = multiplier;
        this.threshold = threshold;
    }

    @Override
    public String toString() {
        return "{threshold: " + this.threshold + ", multiplier: " + this.multiplier + ", entity: " + this.entity + "}";
    }

    public static class AbominationEmpowermentData extends EmpowermentData {

        public final EntityType<?> abomination;

        public AbominationEmpowermentData(EntityType<?> entity, int multiplier, int threshold, EntityType<?> abomination) {
            super(entity, multiplier, threshold);
            this.abomination = abomination;
        }

        public static AbominationEmpowermentData fromRegularData(EmpowermentData data, EntityType<?> abomination) {
            return new AbominationEmpowermentData(data.entity, data.multiplier, data.threshold, abomination);
        }

        @Override
        public String toString() {
            return "{threshold: " + this.threshold + ", multiplier: " + this.multiplier + ", entity: " + this.entity + ", abomination: " + this.abomination + "}";
        }
    }
}
