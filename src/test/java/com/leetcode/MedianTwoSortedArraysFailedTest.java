package com.leetcode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MedianTwoSortedArraysFailedTest {

    private final MedianTwoSortedArraysFailed medianFinder = new MedianTwoSortedArraysFailed();

    @Test
    void findsMedianWhenCombinedLengthIsOdd() {
        var nums1 = new int[]{1, 3};
        var nums2 = new int[]{2};
        var expected = medianFinder.findMedianSortedArraysOld(nums1, nums2);
        var actual = medianFinder.findMedianSortedArrays(nums1, nums2);

        assertEquals(expected, actual);
    }

    @Test
    void findsMedianWhenCombinedLengthIsEven() {
        var nums1 = new int[]{1, 2};
        var nums2 = new int[]{3, 4};
        var expected = medianFinder.findMedianSortedArraysOld(nums1, nums2);
        var actual = medianFinder.findMedianSortedArrays(nums1, nums2);

        assertEquals(expected, actual);
    }

    @Test
    void findsMedianWhenOneArrayIsEmpty() {
        var nums1 = new int[]{};
        var nums2 = new int[]{2, 3};
        var expected = medianFinder.findMedianSortedArraysOld(nums1, nums2);
        var actual = medianFinder.findMedianSortedArrays(nums1, nums2);

        assertEquals(expected, actual);
    }

    @Test
    void returnsZeroWhenBothArraysAreEmpty() {
        var nums1 = new int[]{};
        var nums2 = new int[]{};
        var expected = medianFinder.findMedianSortedArraysOld(nums1, nums2);
        var actual = medianFinder.findMedianSortedArrays(nums1, nums2);

        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("complexMedianCases")
    void findsMedianForComplexInputs(String description, int[] nums1, int[] nums2) {
        var expected = medianFinder.findMedianSortedArraysOld(nums1, nums2);
        var actual = medianFinder.findMedianSortedArrays(nums1, nums2);

        assertEquals(expected, actual);
    }

    private static Stream<Arguments> complexMedianCases() {
        return Stream.of(
                Arguments.of(
                        "highly uneven array lengths",
                        new int[]{1},
                        new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10}),
                Arguments.of(
                        "duplicates span both middle positions",
                        new int[]{1, 2, 2, 2, 9},
                        new int[]{2, 2, 3, 4, 5}),
                Arguments.of(
                        "negative values and duplicates",
                        new int[]{-10, -5, -5, -1},
                        new int[]{-6, -5, 0, 8, 20}),
                Arguments.of(
                        "non-overlapping value ranges",
                        new int[]{-100, -90, -80, -70},
                        new int[]{10, 20, 30, 40}),
                Arguments.of(
                        "additional test case",
                        new int[]{1, 2},
                        new int[]{-1 ,3}),
                Arguments.of(
                        "additional test case edge case with single number",
                        new int[]{2},
                        new int[]{1 ,3, 4}),
                Arguments.of(
                        "additional test case edge case with single number",
                        new int[]{3},
                        new int[]{1 ,2, 4}),
                Arguments.of(
                        "additional test case edge case with single number",
                        new int[]{5},
                        new int[]{1 ,2, 3, 4, 6}),
                Arguments.of(
                        "additional test case edge case with single number",
                        new int[]{1, 3},
                        new int[]{2, 4, 5, 6}),
                Arguments.of(
                        "additional test case edge case with single number",
                        new int[]{3, 5},
                        new int[]{1, 2, 4, 6}),
                Arguments.of(
                        "additional test case edge case with single number",
                        new int[]{1, 2, 4},
                        new int[]{3, 5, 6}),
                Arguments.of(
                        "additional test case edge case with single number",
                        new int[]{1, 2, 4},
                        new int[]{3, 5, 6, 7}),
                Arguments.of(
                        "additional test case edge case with single number",
                        new int[]{1, 2, 5},
                        new int[]{3, 4, 6, 7})
        );
    }
}