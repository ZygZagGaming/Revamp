package com.zygzag.revamp.common.capability;

import com.zygzag.revamp.common.block.MagmaStemBlock;
import com.zygzag.revamp.common.registry.BlockRegistry;
import com.zygzag.revamp.common.tag.RevampTags;
import com.zygzag.revamp.util.Constants;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
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
import java.util.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConnectionTracker {
    public List<Connection> connections;
    public TagKey<Block> tag;
    public Level world;

    public static List<BlockPos> CUBE = new ArrayList<>();

    static {
        for (int x = -1; x < 2; x++) for (int y = -1; y < 2; y++) for (int z = -1; z < 2; z++) if (x != 0 && y != 0 && z != 0) CUBE.add(new BlockPos(x, y, z));
    }

    public ConnectionTracker(TagKey<Block> tag, Level world) {
        this.tag = tag;
        this.connections = new ArrayList<>();
        this.world = world;
    }

    public void replace(Connection section, List<Connection> list) {
        section.remove();
        connections.addAll(list);
    }

    public void addNewConnection(BlockPos a, BlockPos b) {
        Connection connection = pathfind(a, b);
        if (connection != null) connections.add(connection);
    }

    public void update(@Nullable BlockPos updatePosition, Level world) {
        List<Connection> connectionsToUpdate = new ArrayList<>(connections);
        for (Connection connection : connectionsToUpdate) {
            if (updatePosition == null || connection.touches(updatePosition)) {
                if (!connection.isValid()) {
                    connection.remake();
                }
            }
        }
    }

    public List<BlockPos> nodes() {
        List<BlockPos> nodes = new ArrayList<>();
        for (Connection connection : connections) {
            if (!nodes.contains(connection.start)) nodes.add(connection.start);
            if (!nodes.contains(connection.end)) nodes.add(connection.end);
        }
        return nodes;
    }

    public boolean directPathExists(BlockPos a, BlockPos b) {
        for (Connection connection : connections) {
            if (connection.contains(a) && connection.contains(b)) return true;
        }
        return false;
    }

    public boolean pathExists(BlockPos a, BlockPos b) {
        List<BlockPos> nodes = nodes();
        if (!nodes.contains(a) || !nodes.contains(b)) return false;
        return GeneralUtil.pathExists(a, b, nodes, this::directPathExists);
    }

    public ListTag serializeNBT() {
        ListTag nbt = new ListTag();
        for (Connection section : connections) {
            nbt.add(section.serializeNBT());
        }
        return nbt;
    }

    public static ConnectionTracker deserializeNBT(ListTag nbt, TagKey<Block> tag, Level world) {
        ConnectionTracker tracker = new ConnectionTracker(tag, world);
        for (Tag t : nbt) {
            if (t instanceof ListTag l) {
                tracker.connections.add(Connection.deserializeNBT(l, tracker));
            }
        }
        return tracker;
    }

    String lastTick = "";
    public void printSections() {
        String s = connections.toString();
        if (!s.equals(lastTick)) {
            lastTick = s;
            System.out.println(s);
        }
    }

    public boolean connectionCheck(BlockPos a, BlockPos b) {
        if (a == b) return true;
        if (tag == RevampTags.MAGMA_NYLIUM_CONNECTABLE.get()) {
            BlockState aState = world.getBlockState(a);
            BlockState bState = world.getBlockState(b);
            if (aState.is(BlockRegistry.MAGMA_NYLIUM_BLOCK.get()) && bState.is(BlockRegistry.MAGMA_NYLIUM_BLOCK.get())) {
                Vec3i diff = a.subtract(b);
                double len = diff.getX() * diff.getX() + diff.getY() * diff.getY() + diff.getZ() * diff.getZ();
                if (Math.abs(len - 1) < Constants.EPSILON) {
                    return diff.getY() == 0;
                } else if (Math.abs(len - 2) < Constants.EPSILON) {
                    return diff.getY() != 0;
                } else return false;
            } else if ((aState.is(BlockRegistry.MAGMA_NYLIUM_BLOCK.get()) && bState.is(RevampTags.MAGMA_STEMS.get())) || (bState.is(BlockRegistry.MAGMA_NYLIUM_BLOCK.get()) && aState.is(RevampTags.MAGMA_STEMS.get()))) {
                BlockPos stem, nylium;
                if (aState.is(BlockRegistry.MAGMA_NYLIUM_BLOCK.get())) {
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

    @Nullable
    public Connection pathfind(BlockPos a, BlockPos b) {
        // search for positions
        if (a.equals(b)) return new Connection(List.of(a));
        List<BlockPos> checked = new ArrayList<>();
        Map<BlockPos, Integer> distances = new HashMap<>();
        LinkedList<BlockPos> queue = new LinkedList<>(List.of(a)); // implements Queue
        int step = 0;
        while (queue.size() > 0) {
            BlockPos toCheck = queue.pop();
            if (toCheck.equals(b)) {
                checked.add(b);
                break;
            }
            GeneralUtil.insertAllBy(
                    queue,
                    GeneralUtil.filter(
                            GeneralUtil.orderedBy(
                                    GeneralUtil.map(
                                            CUBE,
                                            toCheck::offset
                                    ),
                                    b::distManhattan
                            ),
                            (pos) -> !checked.contains(pos) && !queue.contains(pos) && connectionCheck(toCheck, pos)
                    ),
                    (pos) -> b.distManhattan(pos)
            );
            checked.add(toCheck);
            distances.put(toCheck, step);
        }
        if (!checked.contains(b)) return null; // only if it can't reach b

        // tracing positions towards a
        List<BlockPos> f = new ArrayList<>(List.of(b));
        boolean found = true;
        outer: while (found) {
            found = false;
            for (BlockPos elem : checked) {
                if (connectionCheck(elem, f.get(0)) && distances.get(elem) == distances.get(f.get(0)) + 1) {// if distance decreasing
                    f.add(0, elem);
                    found = true;
                    if (elem == a) break outer;
                }
            }
        }
        return new Connection(f);
    }

    public void removeConnectionsInvolving(BlockPos pos) {
        for (Connection connection : connections) {
            if (connection.contains(pos)) connection.remove();
        }
    }

    public class Connection {
        public List<BlockPos> positions;
        public boolean removed = false;
        public BlockPos start, end;

        public Connection(List<BlockPos> positions) {
            this.positions = positions;
            this.start = positions.get(0);
            this.end = positions.get(positions.size() - 1);
        }

        public boolean isValid() {
            if (positions.size() <= 1) return true;
            for (int i = 1; i < positions.size(); i++) {
                if (!connectionCheck(positions.get(i), positions.get(i - 1))) return false;
            }
            return true;
        }

        public void remake() {
            connections.remove(this);
            addNewConnection(start, end);
        }

        public void remove() {
            connections.remove(this);
            removed = true;
        }

        public boolean contains(BlockPos pos) {
            return positions.contains(pos);
        }

        public boolean touches(BlockPos pos) {
            return GeneralUtil.any(positions, (it) -> GeneralUtil.adjacent(pos, it));
        }

        public ListTag serializeNBT() {
            ListTag tag = new ListTag();
            for (BlockPos pos : positions) tag.add(NbtUtils.writeBlockPos(pos));
            return tag;
        }

        public static Connection deserializeNBT(ListTag tag, ConnectionTracker tracker) {
            List<BlockPos> list = new ArrayList<>();
            for (Tag t : tag) if (t instanceof CompoundTag ct) list.add(NbtUtils.readBlockPos(ct));
            return tracker.new Connection(list);
        }
    }
}
