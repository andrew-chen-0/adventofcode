package com.leetcode;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreeSumTest {

    @Test
    void findsUniqueTripletsThatSumToZero() {
        var actual = ThreeSum.threeSum(new int[]{-1, 0, 1, 2, -1, -4});

        var expected = Set.of(
                List.of(-1, -1, 2),
                List.of(-1, 0, 1));
        assertEquals(expected, new HashSet<>(actual));
    }

    @Test
    void returnsEmptyListWhenNoTripletsSumToZero() {
        var actual = ThreeSum.threeSum(new int[]{0, 1, 1});

        assertEquals(List.of(), actual);
    }
}