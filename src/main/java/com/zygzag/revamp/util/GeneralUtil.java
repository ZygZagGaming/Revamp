package com.zygzag.revamp.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.math.Matrix3f;
import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.charge.EnergyCharge;
import com.zygzag.revamp.common.registry.ParticleTypeRegistry;
import com.zygzag.revamp.common.tag.RevampTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GeneralUtil {

    /**
     * RandomSource number generator.
     * @param leftInclusive The left bound for the random number.
     * @param rightInclusive The right bound for the random number.
     * @return A random number in the range <code>leftInclusive..rightInclusive</code>, inclusive.
     */
    public static int randomInt(int leftInclusive, int rightInclusive) {
        return (int) (Math.random() * ((rightInclusive - leftInclusive) + 1)) + leftInclusive;
    }

    /**
     * RandomSource element from a list.
     * @param list The list to get a random element from.
     * @param <T> The type of the list.
     * @return A random element from the list.
     */
    @Nullable
    public static <T> T randomFromList(List<T> list) {
        if (list.size() == 0) return null;
        return list.get(randomInt(0, list.size() - 1));
    }

    public static int getColor(BlockState state) {
        if (state.is(RevampTags.COPPER_ORES.get())) return Constants.COPPER_ORE_COLOR;
        else if (state.is(Tags.Blocks.ORES_COAL)) return Constants.COAL_ORE_COLOR;
        else if (state.is(Tags.Blocks.ORES_IRON)) return Constants.IRON_ORE_COLOR;
        else if (state.is(Tags.Blocks.ORES_GOLD)) return Constants.GOLD_ORE_COLOR;
        else if (state.is(Tags.Blocks.ORES_NETHERITE_SCRAP)) return Constants.NETHERITE_ORE_COLOR;
        else if (state.is(Tags.Blocks.ORES_DIAMOND)) return Constants.DIAMOND_ORE_COLOR;
        else if (state.is(Tags.Blocks.ORES_QUARTZ)) return Constants.QUARTZ_ORE_COLOR;
        else if (state.is(Tags.Blocks.ORES_LAPIS)) return Constants.LAPIS_ORE_COLOR;
        else if (state.is(Tags.Blocks.ORES_REDSTONE)) return Constants.REDSTONE_ORE_COLOR;

        return 0;
    }

    @Nullable
    public static <T> T weightedRandom(T[] elements, int[] weights) {
        if (elements.length == 0) return null;
        int total = 0;
        for (int weight : weights) {
            total += weight;
        }
        return weightedRandom(elements, weights, total);
    }

    @Nullable
    public static <T> T weightedRandom(T[] elements, int[] weights, int totalWeight) {
        if (elements.length == 0) return null;
        int n = (int) (Math.random() * totalWeight + 1);
        int i;
        for (i = 0; n > 0; i++) {
            if (n <= weights[i]) {
                return elements[i];
            }
            n -= weights[i];
        }
        return elements[i];
    }

    public static double degreesToRadians(double deg) {
        return deg / 180.0 * Math.PI;
    }

    public static double radiansToDegrees(double rad) {
        return rad * 180.0 / Math.PI;
    }

    public static float degreesToRadians(float deg) {
        return (float) (deg / 180.0 * Math.PI);
    }
    public static float radiansToDegrees(float rad) {
        return (float) (rad * 180.0 / Math.PI);
    }

    public static Vec3 lerp(Vec3 a, Vec3 b, double t) {
        return a.scale(t).add(b.scale(1 - t));
    }

    public static double lerp(double a, double b, double t) {
        return a * t + b * (1 - t);
    }

    public static <T> List<T> subList(List<T> list, int start, int endExclusive) {
        List<T> l = new ArrayList<>();
        for (int i = start; i < endExclusive; i++) {
            l.add(list.get(i));
        }
        return l;
    }

    public static Vec3 pointAlongBezier(List<Vec3> defining, float t) {
        int n = defining.size() - 1;
        return sumOfVec(0, n + 1, (i) -> defining.get(i).scale(nCk(n, i) * continuousPow(1 - t, n - i) * continuousPow(t, i)));
    }

    private static double continuousPow(double n, double p) {
        if (p == 0) return 1;
        return Math.pow(n, p);
    }

    public static Vec3 sumOfVec(int start, int endExclusive, Function<Integer, Vec3> function) {
        Vec3 vec = Vec3.ZERO;
        for (int i = start; i < endExclusive; i++) {
            vec = vec.add(function.apply(i));
        }
        return vec;
    }

    public static double sumOf(int start, int endExclusive, Function<Integer, Double> function) {
        double sum = 0;
        for (int i = start; i < endExclusive; i++) {
            sum += function.apply(i);
        }
        return sum;
    }

    public static <T extends ICapabilityProvider, C> void ifCapability(T t, Capability<C> capability, Consumer<C> function) {
        Optional<C> op = t.getCapability(capability).resolve();
        op.ifPresent(function);
    }

    @Nullable
    public static <T extends ICapabilityProvider, C, O> O ifCapabilityMap(T t, Capability<C> capability, Function<C, O> function) {
        Optional<C> op = t.getCapability(capability).resolve();
        return op.map(function).orElse(null);
    }

    public static Vec3 randVectorNormalized() {
        return new Vec3(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5).normalize();
    }
    public static Vec3 randVectorNormalized(RandomSource rng) {
        return new Vec3(rng.nextDouble() - 0.5, rng.nextDouble() - 0.5, rng.nextDouble() - 0.5).normalize();
    }
    public static Vec3 randVectorNormalized(Random rng) {
        return new Vec3(rng.nextDouble() - 0.5, rng.nextDouble() - 0.5, rng.nextDouble() - 0.5).normalize();
    }

    public static Vec3 randomPointOnCube(BlockPos pos, RandomSource rng) {
        int side = rng.nextInt(6);
        int axis = side % 3;
        Vec3 result = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        result = add(result, axis, side > 2 ? 1 : 0);
        result = add(result, (axis + 1) % 3, rng.nextDouble());
        result = add(result, (axis + 2) % 3, rng.nextDouble());
        return result;
    }

    public static Vec3 add(Vec3 vec, int index, double value) {
        if (index == 0) return vec.add(value, 0, 0);
        if (index == 1) return vec.add(0, value, 0);
        return vec.add(0, 0, value);
    }

    public static Vec3 add(Vec3 vec, Direction.Axis axis, double value) {
        if (axis == Direction.Axis.X) return vec.add(value, 0, 0);
        if (axis == Direction.Axis.Y) return vec.add(0, value, 0);
        return vec.add(0, 0, value);
    }

    public static Vec3 randomPointOnAABB(AABB aabb, RandomSource rng) {
        double sum = aabb.getXsize() + aabb.getYsize() + aabb.getZsize();
        boolean reversed = rng.nextBoolean();
        double r = rng.nextDouble() * sum;
        Direction.Axis axis;
        if (r < aabb.getXsize()) {
            axis = Direction.Axis.X;
        } else if (r < aabb.getXsize() + aabb.getYsize()) {
            axis = Direction.Axis.Y;
        } else {
            axis = Direction.Axis.Z;
        }
        Direction.Axis a2 = axis == Direction.Axis.Y ? Direction.Axis.X : Direction.Axis.Y;
        Direction.Axis a3 = axis == Direction.Axis.Z ? Direction.Axis.X : Direction.Axis.Z;
        double localX = rng.nextDouble() * size(aabb, a2);
        double localZ = rng.nextDouble() * size(aabb, a3);
        Vec3 vec = Vec3.ZERO;//new Vec3(-aabb.getXsize() / 2, -aabb.getYsize() / 2, -aabb.getZsize() / 2);
        vec = add(vec, axis, reversed ? size(aabb, axis) : 0);
        vec = add(vec, a2, localX);
        vec = add(vec, a3, localZ);
        return vec;
    }

    public static double size(AABB aabb, Direction.Axis axis) {
        if (axis == Direction.Axis.X) return aabb.getXsize();
        else if (axis == Direction.Axis.Y) return aabb.getYsize();
        else return aabb.getZsize();
    }

    static HashMap<Pair<Integer, Integer>, Integer> nCkCache = new HashMap<>();

    public static int nCk(int n, int k) {
        if (k == 0 || k == n) return 1;
        Pair<Integer, Integer> pair = new Pair<>(n, k);
        if (!nCkCache.containsKey(pair)) nCkCache.put(pair, nCk(n - 1, k - 1) + nCk(n - 1, k));
        return nCkCache.get(pair);
    }

    static HashMap<Integer, Integer> factorialCache = new HashMap<>();

    public static int factorial(int n) {
        if (!factorialCache.containsKey(n)) {
            if (n <= 1) factorialCache.put(n, 1);
            else factorialCache.put(n, factorial(n - 1) * n);
        }
        return factorialCache.get(n);
    }

    public static <T> List<T> rotate(List<T> list, int amount) {
        List<T> newList = new ArrayList<>();
        for (int i = amount; i < list.size(); i++) {
            newList.add(list.get(i));
        }
        for (int i = 0; i < amount; i++) {
            newList.add(list.get(i));
        }
        return newList;
    }

    @Nullable
    public static <T> T minByOrNull(List<T> list, Function<T, Double> function) {
        if (list.size() < 1) return null;
        T minT = list.get(0);
        double min = function.apply(minT);
        for (T elem : list) {
            double n = function.apply(elem);
            if (n < min) {
                min = n;
                minT = elem;
            }
        }
        return minT;
    }

    @Nullable
    public static <T> T maxByOrNull(List<T> list, Function<T, Double> function) {
        if (list.size() < 1) return null;
        T maxT = list.get(0);
        double max = function.apply(maxT);
        for (T elem : list) {
            double n = function.apply(elem);
            if (n > max) {
                max = n;
                maxT = elem;
            }
        }
        return maxT;
    }

    public static <T, O> O ifNonNullMapOrElse(@Nullable T t, O o, Function<T, O> function) {
        if (t == null) return o;
        return function.apply(t);
    }

    public static <T> void ifNonNull(@Nullable T t, Consumer<T> function) {
        if (t != null) function.accept(t);
    }

    public static SimpleParticleType particle(double charge) {
        if (charge > 0) return ParticleTypeRegistry.CHARGE_PARTICLE_TYPE_POSITIVE.get();
        else return ParticleTypeRegistry.CHARGE_PARTICLE_TYPE_NEGATIVE.get();
    }

    public static float getChargeAt(Level world, BlockPos pos) {
        Float f = ifCapabilityMap(world.getChunkAt(pos), Revamp.CHUNK_CHARGE_CAPABILITY, (handler) -> {
            if (handler.charges.get(pos) == null) return 0f;
            else return handler.charges.get(pos).getCharge();
        });
        if (f == null) return 0;
        return f;
    }

    public static float getHandlerAt(Level world, BlockPos pos) {
        Float f = ifCapabilityMap(world.getChunkAt(pos), Revamp.CHUNK_CHARGE_CAPABILITY, (handler) -> {
            if (handler.charges.get(pos) == null) return 0f;
            else return handler.charges.get(pos).getCharge();
        });
        if (f == null) return 0;
        return f;
    }

    public static void setChargeAt(Level world, BlockPos pos, float charge) {
        ifCapability(world.getChunkAt(pos), Revamp.CHUNK_CHARGE_CAPABILITY, (handler) -> {
            if (handler.charges.get(pos) == null) handler.add(new EnergyCharge(charge, pos, handler));
            else handler.charges.get(pos).setCharge(charge);
        });
    }

    public static Color lerpColor(Color a, Color b, float t) {
        return new Color(Math.round(lerp(a.getRed(), b.getRed(), t)), Math.round(lerp(a.getGreen(), b.getGreen(), t)), Math.round(lerp(a.getBlue(), b.getBlue(), t)));
    }
    public static int lerpColor(int a, int b, float t) {
        return hsvToRgb(lerpColor(rgbToHsv(a), rgbToHsv(b), t));
    }

    public static HsvColor lerpColor(HsvColor a, HsvColor b, float t) {
        float h1 = a.h();
        float h2 = b.h();
        float s1 = a.s();
        float s2 = b.s();
        float v1 = a.v();
        float v2 = b.v();
        return new HsvColor(
                Math.round(lerp(h1, h2, t)),
                Math.round(lerp(s1, s2, t)),
                Math.round(lerp(v1, v2, t))
        );
    }

    public static Vec3 normal(Vec3 a, Vec3 b, Vec3 c) {
        Vec3 u = a.subtract(b);
        Vec3 v = c.subtract(b);
        return u.cross(v).normalize();
    }

    public static HsvColor rgbToHsv(int rgb) {
        float r = ((rgb >> 16) & 0xff) / 255f;
        float g = ((rgb >> 8) & 0xff) / 255f;
        float b = (rgb & 0xff) / 255f;
        float cmax = max(r, g, b);
        float cmin = min(r, g, b);
        float diff = cmax - cmin;
        float h = 0;
        if (cmax == 0 && cmin == 0) h = 0;
        else if (cmax == r) h = (60 * ((g - b) / diff) + 360) % 360;
        else if (cmax == g) h = (60 * ((b - r) / diff) + 120) % 360;
        else if (cmax == b) h = (60 * ((r - g) / diff) + 240) % 360;
        float s;
        if (cmax == 0) s = 0;
        else s = (diff / cmax) * 100;
        float v = cmax * 100;
        return new HsvColor(h, s, v);
    }

    public static int max(int... ints) {
        return maxHelper(ints, 0);
    }

    public static int min(int... ints) {
        return minHelper(ints, 0);
    }

    private static int maxHelper(int[] ints, int start) {
        if (start >= ints.length - 1) return ints[ints.length - 1];
        return Math.max(ints[start], maxHelper(ints, start + 1));
    }

    private static int minHelper(int[] ints, int start) {
        if (start >= ints.length - 1) return ints[ints.length - 1];
        return Math.min(ints[start], minHelper(ints, start + 1));
    }

    public static float max(float... floats) {
        return maxHelper(floats, 0);
    }

    public static float min(float... floats) {
        return minHelper(floats, 0);
    }

    private static float maxHelper(float[] floats, int start) {
        if (start >= floats.length - 1) return floats[floats.length - 1];
        return Math.max(floats[start], maxHelper(floats, start + 1));
    }

    private static float minHelper(float[] floats, int start) {
        if (start >= floats.length - 1) return floats[floats.length - 1];
        return Math.min(floats[start], minHelper(floats, start + 1));
    }

    public static int hsvToRgb(HsvColor hsv) {
        return hsvToRgb(hsv.h(), hsv.s(), hsv.v());
    }

    public static int hsvToRgb(float h, float s, float v) {
        float c = v * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = v - c;
        float r1, g1, b1;
        if (within(h, 0, 60)) {
            r1 = c;
            g1 = x;
            b1 = 0;
        } else if (within(h, 60, 120)) {
            r1 = x;
            g1 = c;
            b1 = 0;
        } else if (within(h, 120, 180)) {
            r1 = 0;
            g1 = c;
            b1 = x;
        } else if (within(h, 180, 240)) {
            r1 = x;
            g1 = x;
            b1 = c;
        } else if (within(h, 240, 300)) {
            r1 = x;
            g1 = 0;
            b1 = c;
        } else {
            r1 = c;
            g1 = 0;
            b1 = x;
        }
        int r = Math.round((r1 + m) * 255);
        int g = Math.round((g1 + m) * 255);
        int b = Math.round((b1 + m) * 255);
        return (r << 16) | (g << 8) | b;
    }

    public static boolean within(float x, float a, float b) {
        return a <= x && x < b;
    }

    public static final Random RANDOM = new Random();

    public static <T> int count(List<T> list, Predicate<T> pred) {
        int c = 0;
        for (T elem : list) {
            if (pred.test(elem)) c++;
        }
        return c;
    }

    public static <T> int count(T[] list, Predicate<T> pred) {
        int c = 0;
        for (T elem : list) {
            if (pred.test(elem)) c++;
        }
        return c;
    }

    public static <T> List<T> filter(T[] arr, Predicate<T> pred) {
        List<T> list = new ArrayList<T>();
        for (T elem : arr) {
            if (pred.test(elem)) {
                list.add(elem);
            }
        }
        return list;
    }

    public static <T> List<T> filter(List<T> arr, Predicate<T> pred) {
        List<T> list = new ArrayList<T>();
        for (T elem : arr) {
            if (pred.test(elem)) {
                list.add(elem);
            }
        }
        return list;
    }

    public static double rem(double a, double b) {
        return ((a % b) + b) % b;
    }

    public static boolean clearPathExists(Vec3 a, Vec3 b, Level world) {
        return clearPathExists(a, b, world, null);
    }

    public static boolean clearPathExists(Vec3 a, Vec3 b, Level world, @Nullable Entity entity) {
        return world.clip(new ClipContext(a, b, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS;
    }

    public static Vec3 rotateAbout(Vec3 pt, Vec3 origin, double xRot, double yRot, double zRot) {
        return mul(rotationMatrix(xRot, yRot, zRot), pt.subtract(origin)).add(origin);
    }

    public static Vec3 rotateAbout(Vec3 pt, Vec3 origin, double xRot, double yRot) {
        return mul(mul(xRotMatrix(xRot), yRotMatrix(yRot)), pt.subtract(origin)).add(origin);
    }

    public static Vec3 rectangularToSpherical(Vec3 pt) {
        double k = pt.x * pt.x + pt.y * pt.y + pt.z * pt.z;
        if (k < 1e-7) return new Vec3(0, 0, 0);
        else return new Vec3(Math.sqrt(k), Math.atan2(pt.z, pt.x), Math.acos(pt.y / Math.sqrt(k)));

    }

    public static Vec3 sphericalToRectangular(Vec3 pt) {
        return new Vec3(pt.x * Math.sin(pt.z + Math.PI / 2) * Math.cos(pt.y), pt.x * Math.cos(pt.z + Math.PI / 2), pt.x * Math.sin(pt.z + Math.PI / 2) * Math.sin(pt.y));
    }

    public static Matrix3f xRotMatrix(double xRot) {
        Matrix3f matr = new Matrix3f();
        matr.set(1, 1, (float) Math.cos(xRot));
        matr.set(1, 2, (float) -Math.sin(xRot));
        matr.set(2, 1, (float) Math.sin(xRot));
        matr.set(2, 2, (float) Math.cos(xRot));
        matr.set(0, 0, 1);
        return matr;
    }

    public static Matrix3f yRotMatrix(double yRot) {
        Matrix3f matr = new Matrix3f();
        matr.set(0, 0, (float) Math.cos(yRot));
        matr.set(0, 2, (float) Math.sin(yRot));
        matr.set(2, 0, (float) -Math.sin(yRot));
        matr.set(2, 2, (float) Math.cos(yRot));
        matr.set(1, 1, 1);
        return matr;
    }

    public static Matrix3f zRotMatrix(double zRot) {
        Matrix3f matr = new Matrix3f();
        matr.set(0, 0, (float) Math.cos(zRot));
        matr.set(0, 1, (float) -Math.sin(zRot));
        matr.set(1, 0, (float) Math.sin(zRot));
        matr.set(1, 1, (float) Math.cos(zRot));
        matr.set(2, 2, 1);
        return matr;
    }

    public static Matrix3f rotationMatrix(double xRot, double yRot, double zRot) {
        return mul(mul(xRotMatrix(xRot), yRotMatrix(yRot)), zRotMatrix(zRot));
    }

    public static Vec3 mul(Matrix3f matr, Vec3 vec) {
        double x = matr.m00 * vec.x + matr.m01 * vec.y + matr.m02 * vec.z;
        double y = matr.m10 * vec.x + matr.m11 * vec.y + matr.m12 * vec.z;
        double z = matr.m20 * vec.x + matr.m21 * vec.y + matr.m22 * vec.z;
        return new Vec3(x, y, z);
    }

    public static Matrix3f mul(Matrix3f a, Matrix3f b) {
        Matrix3f matr = new Matrix3f();
        for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) {
            float sum = 0;
            for (int k = 0; k < 3; k++) {
                sum += get(a, i, k) * get(b, k, j);
            }
            matr.set(i, j, sum);
        }
        return matr;
    }

    public static float get(Matrix3f matr, int i, int j) { // copied code from the set method
        if (i == 0) {
            if (j == 0) {
                return matr.m00;
            } else if (j == 1) {
                return matr.m01;
            } else {
                return matr.m02;
            }
        } else if (i == 1) {
            if (j == 0) {
                return matr.m10;
            } else if (j == 1) {
                return matr.m11;
            } else {
                return matr.m12;
            }
        } else if (j == 0) {
            return matr.m20;
        } else if (j == 1) {
            return matr.m21;
        } else {
            return matr.m22;
        }
    }

    public static <T> boolean any(Iterable<T> list, Predicate<T> predicate) {
        for (T elem : list) if (predicate.test(elem)) return true;
        return false;
    }

    public static <T> Optional<T> first(Iterable<T> list, Predicate<T> predicate) {
        for (T elem : list) if (predicate.test(elem)) return Optional.of(elem);
        return Optional.empty();
    }

    public static boolean adjacent(BlockPos a, BlockPos b) {
        return Math.abs(a.distSqr(b) - 1) < Constants.EPSILON;
    }

    public static boolean cornerAdjacent(BlockPos a, BlockPos b) {
        return a.distSqr(b) < 3 + Constants.EPSILON;
    }

    public static <T> boolean graphIsConnected(List<T> nodes, BiPredicate<T, T> connectPredicate) {
        T start = nodes.get(0);
        List<T> checked = new ArrayList<>(List.of(start));
        List<T> unchecked = new ArrayList<>(nodes);
        unchecked.remove(0);
        while (true) {
            boolean foundMatch = false;
            for (int i = 0; i < unchecked.size();) {
                T pos = unchecked.get(i);
                boolean modified = false;
                for (T pos1 : checked) {
                    if (connectPredicate.test(pos1, pos)) {
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
        return checked.size() == nodes.size();
    }

    public static <T> Map<T, List<T>> getEdges(List<T> nodes, BiPredicate<T, T> connectPredicate) {
        Map<T, List<T>> connections = new HashMap<>();
        for (T node : nodes) {
            List<T> neighbors = new LinkedList<>();
            for (T other : nodes) {
                if (connectPredicate.test(node, other)) neighbors.add(other);
            }
            connections.put(node, neighbors);
        }
        return connections;
    }

    public static <T> boolean pathExists(T a, T b, List<T> nodes, Map<T, List<T>> edges) {
        LinkedList<T> checked = new LinkedList<>();
        LinkedList<T> queue = new LinkedList<>(List.of(a));
        while (!queue.isEmpty()) {
            T node = queue.pop();
            checked.add(node);
            List<T> neighbors = edges.get(node);
            for (T neighbor : neighbors) {
                if (neighbor == b) return true;
                if (!checked.contains(neighbor)) queue.add(neighbor);
            }
        }
        return false;
    }

    public static <T> boolean pathExists(T a, T b, List<T> nodes, BiPredicate<T, T> connectPredicate) {
        return pathExists(a, b, nodes, getEdges(nodes, connectPredicate));
    }

    public static <A, B> List<B> map(List<A> list, Function<A, B> func) {
        List<B> l = new ArrayList<>();
        for (A elem : list) l.add(func.apply(elem));
        return l;
    }

    @Nullable public static <T extends Comparable<T>> T minOrNull(List<T> list) {
        if (list.size() == 0) return null;
        T min = list.get(0);
        for (T elem : list) if (elem.compareTo(min) < 0) min = elem;
        return min;
    }

    @Nullable public static <A, B extends Comparable<B>> B minOfOrNull(List<A> list, Function<A, B> function) {
        return minOrNull(map(list, function));
    }

    public static double norm(Vec3i vec) {
        return vec.getX() * vec.getX() + vec.getY() * vec.getY() + vec.getZ() * vec.getZ();
    }

    public static <A, B> B fold(List<A> list, B start, BiFunction<B, A, B> func) {
        B acc = start;
        for (A elem : list) acc = func.apply(acc, elem);
        return acc;
    }

    public static int staticRandom(double chance, RandomSource rng) {
        return ((int) chance) + (rng.nextDouble() < chance % 1 ? 1 : 0);
    }

    public static void repeat(int times, Runnable func) {
        for (int i = 0; i < times; i++) func.run();
    }

    public static <T, O extends Comparable<O>> List<T> orderedBy(List<T> list, Function<T, O> func) {
        if (list.size() <= 1) return list;
        int pivotIndex = list.size() / 2;
        T pivot = list.get(pivotIndex);
        O comp = func.apply(pivot);
        int i = 0;
        List<T> lower = new ArrayList<>(), greater = new ArrayList<>();
        while (i < list.size()) {
            T elem = list.get(i);
            if (func.apply(elem).compareTo(comp) < 0) lower.add(elem);
            else greater.add(elem);

            if (i == pivotIndex) i += 2;
            else i++;
        }
        List<T> fin = orderedBy(lower, func);
        fin.add(pivot);
        fin.addAll(orderedBy(greater, func));
        return fin;
    }

    public static <T extends Comparable<T>> List<T> ordered(List<T> list) {
        return orderedBy(list, (it) -> it);
    }

    public static <T extends Comparable<T>> void insert(List<T> list, T elem) {
        int i = 0;
        while (list.get(i).compareTo(elem) < 0) i++;
        list.add(i, elem);
    }

    public static <T, O extends Comparable<O>> void insertBy(List<T> list, T elem, Function<T, O> func) {
        int i = 0;
        O k = func.apply(elem);
        while (func.apply(list.get(i)).compareTo(k) < 0) i++;
        list.add(i, elem);
    }

    public static <T extends Comparable<T>> void insertAll(List<T> list, List<T> toInsert) {
        for (T elem : toInsert) insert(list, elem);
    }

    public static <T, O extends Comparable<O>> void insertAllBy(List<T> list, List<T> toInsert, Function<T, O> func) {
        for (T elem : toInsert) insertBy(list, elem, func);
    }

    public static <T> List<T> search(List<T> nodes, BiPredicate<T, T> connectionPredicate) {
        return search(nodes, nodes.get(0), getEdges(nodes, connectionPredicate));
    }

    public static <T> List<T> search(List<T> nodes, Map<T, List<T>> edges) {
        return search(nodes, nodes.get(0), edges);
    }

    public static <T> List<T> search(List<T> nodes, T startingNode, BiPredicate<T, T> connectionPredicate) {
        return search(nodes, startingNode, getEdges(nodes, connectionPredicate));
    }

    public static <T> List<T> search(List<T> nodes, T startingNode, Map<T, List<T>> edges) {
        LinkedList<T> checked = new LinkedList<>();
        LinkedList<T> queue = new LinkedList<>(List.of(startingNode));
        while (!queue.isEmpty()) {
            T nodeToCheck = queue.pop();
            checked.add(nodeToCheck);
            queue.addAll(edges.get(nodeToCheck));
        }
        return checked;
    }
}
