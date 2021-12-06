package com.zygzag.revamp.util;

import net.minecraft.MethodsReturnNonnullByDefault;

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
}
