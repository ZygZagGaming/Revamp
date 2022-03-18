package com.zygzag.revamp.util;

import com.zygzag.revamp.common.block.tag.RevampTags;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GeneralUtil {

    /**
     * Random number generator.
     * @param leftInclusive The left bound for the random number.
     * @param rightInclusive The right bound for the random number.
     * @return A random number in the range <code>leftInclusive..rightInclusive</code>, inclusive.
     */
    public static int randomInt(int leftInclusive, int rightInclusive) {
        return (int) (Math.random() * ((rightInclusive - leftInclusive) + 1)) + leftInclusive;
    }

    /**
     * Random element from a list.
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
}
