package com.zygzag.revamp.common.capability;

import com.zygzag.revamp.common.block.MagmaStemBlock;
import com.zygzag.revamp.common.registry.Registry;
import com.zygzag.revamp.common.tag.RevampTags;
import com.zygzag.revamp.util.Constants;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ContiguousSectionTracker {
    public List<ContiguousSection> sections;
    public TagKey<Block> tag;
    public Level world;

    public ContiguousSectionTracker(TagKey<Block> tag, Level world) {
        this.tag = tag;
        this.sections = new ArrayList<>();
        this.world = world;
    }

    public void replace(ContiguousSection section, List<ContiguousSection> list) {
        section.remove();
        sections.addAll(list);
    }

    public void addNewSection(BlockPos pos) {
        sections.add(new ContiguousSection(pos));
    }

    public void update(@Nullable BlockPos updatePosition, Level world) {
        List<ContiguousSection> sectionsToUpdate = new ArrayList<>(sections);
        for (ContiguousSection section : sectionsToUpdate) {
            if (updatePosition == null || section.touches(updatePosition)) {
                if (!section.removed) for (ContiguousSection other : sectionsToUpdate) {
                    if (!other.removed && other != section && other.touches(section)) {
                        section.absorb(other);
                    }
                }
                section.update(updatePosition);
            }
        }
        if (updatePosition != null) for (Direction dir : Direction.values()) {
            BlockPos relativePos = updatePosition.relative(dir);
            if (world.getBlockState(relativePos).is(tag)) {
                if (!GeneralUtil.any(sections, (it) -> it.contains(relativePos))) { // if it should be added but hasn't yet for some reason
                    ContiguousSection section = new ContiguousSection(relativePos);
                    boolean found = true;
                    while (found) {
                        found = false;
                        int s = section.blocks.size();
                        for (int i = 0; i < s; i++) for (int x = -1; x < 2; x++) for (int y = -1; y < 2; y++) for (int z = -1; z < 2; z++) {
                            BlockPos relativePos2 = section.blocks.get(i).offset(x, y, z);
                            if (!section.contains(relativePos2) && section.isValid(relativePos2)) {
                                section.add(relativePos2);
                                found = true;
                            }
                        }
                    }
                    sections.add(section);
                }
            }
        }
    }

    public ListTag serializeNBT() {
        ListTag nbt = new ListTag();
        for (ContiguousSection section : sections) {
            nbt.add(section.serializeNBT());
        }
        return nbt;
    }

    public static ContiguousSectionTracker deserializeNBT(ListTag nbt, TagKey<Block> tag, Level world) {
        ContiguousSectionTracker tracker = new ContiguousSectionTracker(tag, world);
        for (Tag t : nbt) {
            if (t instanceof ListTag l) {
                tracker.sections.add(ContiguousSection.deserializeNBT(l, tracker));
            }
        }
        return tracker;
    }

    String lastTick = "";
    public void printSections() {
        String s = sections.toString();
        if (!s.equals(lastTick)) {
            lastTick = s;
            System.out.println(s);
        }
    }

    public class ContiguousSection {
        public double distanceTo(BlockPos pos) {
            Double d = GeneralUtil.minOfOrNull(blocks, pos::distSqr);
            return d == null ? Double.NaN : Math.sqrt(d);
        }

        public double distanceToSqr(BlockPos pos) {
            Double d = GeneralUtil.minOfOrNull(blocks, pos::distSqr);
            return d == null ? Double.NaN : d;
        }

        public double distanceTo(ContiguousSection section) {
            Double d = GeneralUtil.minOfOrNull(section.blocks, this::distanceToSqr);
            return d == null ? Double.NaN : Math.sqrt(d);
        }

        public double distanceToSqr(ContiguousSection section) {
            Double d = GeneralUtil.minOfOrNull(section.blocks, this::distanceToSqr);
            return d == null ? Double.NaN : d;
        }

        public List<BlockPos> blocks;

        public ListTag serializeNBT() {
            ListTag nbt = new ListTag();
            for (BlockPos pos : blocks) {
                nbt.add(NbtUtils.writeBlockPos(pos));
            }
            return nbt;
        }

        public static ContiguousSection deserializeNBT(ListTag list, ContiguousSectionTracker tracker) {
            List<BlockPos> l = new ArrayList<>();
            for (Tag t : list) {
               if (t instanceof CompoundTag ct) l.add(NbtUtils.readBlockPos(ct));
            }
            return tracker.new ContiguousSection(l);
        }

        public boolean removed = false;

        public boolean exists() {
            return !removed && sections.contains(this);
        }

        public void remove() {
            removed = false;
            sections.remove(this);
        }

        public ContiguousSection(BlockPos pos) {
            blocks = new ArrayList<>(List.of(pos));
        }

        public ContiguousSection(List<BlockPos> blocks) {
            this.blocks = blocks;
        }

        public boolean contains(BlockPos pos) {
            return blocks.contains(pos);
        }

        public boolean touches(BlockPos pos) {
            return GeneralUtil.any(blocks, (it) -> GeneralUtil.cornerAdjacent(it, pos));
        }

        public boolean touches(ContiguousSection section) {
            return GeneralUtil.any(blocks, section::touches);
        }

        public boolean touchesOrContains(BlockPos pos) {
            return GeneralUtil.any(blocks, (it) -> it.equals(pos) || connectionCheck(it, pos));
        }

        public void add(BlockPos pos) {
            blocks.add(pos);
        }

        public void remove(BlockPos pos) {
            blocks.remove(pos);
        }

        public boolean isValid(BlockPos pos) {
            boolean b = blocks.size() < 256 && world.getBlockState(pos).is(tag) && (GeneralUtil.any(blocks, (it) -> connectionCheck(it, pos)) || blocks.size() == 0 || (blocks.size() == 1 && blocks.get(0) == pos));
            //System.out.println("block " + pos + " is " + (b ? "" : "not ") + "valid to add to " + this);
            return b;
        }

        public void absorb(ContiguousSection section) {
            boolean flag;
            do {
                flag = false;
                List<BlockPos> list = new ArrayList<>(section.blocks);
                for (BlockPos pos : list) {
                    if (isValid(pos)) {
                        if (!contains(pos)) add(pos);
                        section.remove(pos);
                        if (section.blocks.size() == 0) {
                            section.remove();
                        }
                        flag = true;
                    }
                }
            } while (flag);
        }

        public void update(@Nullable BlockPos updatePosition) {
            if (exists()) {
                if (updatePosition != null && !contains(updatePosition) && isValid(updatePosition)) add(updatePosition);
                if (updatePosition != null && contains(updatePosition) && !isValid(updatePosition)) remove(updatePosition);
                if (!isConnected()) {
                    List<ContiguousSection> frags = fragment();
                    replace(this, frags);
                }
            }
            if (exists() && blocks.size() == 0) remove();
        }

        public boolean isConnected() {
            return blocks.size() <= 1 || GeneralUtil.graphIsConnected(blocks, this::connectionCheck);
        }

        public List<ContiguousSection> fragment() {
            BlockPos start = blocks.get(0);
            List<BlockPos> checked = new ArrayList<>(List.of(start));
            List<BlockPos> unchecked = new ArrayList<>(blocks);
            unchecked.remove(0);
            while (true) {
                boolean foundMatch = false;
                for (int i = 0; i < unchecked.size();) {
                    BlockPos pos = unchecked.get(i);
                    boolean modified = false;
                    for (BlockPos pos1 : checked) {
                        if (connectionCheck(pos1, pos)) {
                            checked.add(pos);
                            unchecked.remove(pos);
                            foundMatch = true;
                            modified = true;
                            break;
                        }
                    }
                    if (!modified) i++;
                }
                if (!foundMatch) break;
            }
            ContiguousSection frag = new ContiguousSection(checked);
            if (checked.size() == blocks.size()) return new ArrayList<>(List.of(frag));
            else {
                List<ContiguousSection> parts = new ContiguousSection(GeneralUtil.filter(blocks, (it) -> !checked.contains(it))).fragment();
                parts.add(frag);
                return parts;
            }
        }

        @Override
        public String toString() {
            return "Section" + blocks.size();
        }

        public boolean connectionCheck(BlockPos a, BlockPos b) {
            if (a == b) return true;
            if (tag == RevampTags.MAGMA_NYLIUM_CONNECTABLE.get()) {
                BlockState aState = world.getBlockState(a);
                BlockState bState = world.getBlockState(b);
                if (aState.is(Registry.BlockRegistry.MAGMA_NYLIUM_BLOCK.get()) && bState.is(Registry.BlockRegistry.MAGMA_NYLIUM_BLOCK.get())) {
                    Vec3i diff = a.subtract(b);
                    double len = diff.getX() * diff.getX() + diff.getY() * diff.getY() + diff.getZ() * diff.getZ();
                    if (Math.abs(len - 1) < Constants.EPSILON) {
                        return diff.getY() == 0;
                    } else if (Math.abs(len - 2) < Constants.EPSILON) {
                        return diff.getY() != 0;
                    } else return false;
                } else if ((aState.is(Registry.BlockRegistry.MAGMA_NYLIUM_BLOCK.get()) && bState.is(RevampTags.MAGMA_STEMS.get())) || (bState.is(Registry.BlockRegistry.MAGMA_NYLIUM_BLOCK.get()) && aState.is(RevampTags.MAGMA_STEMS.get()))) {
                    BlockPos stem, nylium;
                    if (aState.is(Registry.BlockRegistry.MAGMA_NYLIUM_BLOCK.get())) {
                        stem = b;
                        nylium = a;
                    } else {
                        stem = a;
                        nylium = b;
                    }
                    BlockPos diff = stem.subtract(nylium);
                    return Math.abs(GeneralUtil.norm(diff) - 1) < Constants.EPSILON && diff.getY() != -1;
                } else if (aState.is(RevampTags.MAGMA_STEMS.get()) && bState.is(RevampTags.MAGMA_STEMS.get())) {
                    BlockPos diff = a.subtract(b);
                    return Math.abs(GeneralUtil.norm(diff) - 1) < Constants.EPSILON && aState.getValue(MagmaStemBlock.AXIS) == bState.getValue(MagmaStemBlock.AXIS);
                } else {
                    return GeneralUtil.cornerAdjacent(a, b);
                }
            } else return GeneralUtil.adjacent(a, b);
        }
    }
}
