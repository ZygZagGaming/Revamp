package com.zygzag.revamp.common.capability;

import com.zygzag.revamp.common.charge.Arc;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ArcHandler {
    public List<Arc> arcs = new ArrayList<>();
    public Level world;
    public ArcHandler(Level world) {
        this.world = world;
    }

    public void add(Arc arc) {
        arcs.add(arc);
    }
    public void tick() {
        List<Arc> toTick = new ArrayList<>(arcs);
        for (Arc arc : toTick) {
            arc.tick();
        }
    }
}
