package com.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/***
 * This fails because splitting arrays in to two parts is inconsistent since some lists will alternate between odd and even
 * altering the definition of the median for example 17 goes 17, 9, 5, 3 whereas 8 goes 8, 4, 2, 1 where 17 will have 4 additional numbers included over 8 meaning it is not the median.
 */
public class MedianTwoSortedArraysFailed {

    static class IntArraySlice {
        private final int[] underlyingArray;
        private final int startOffset;
        private final int endOffset;

        public IntArraySlice(int[] array, int start, int end) {
            if (start < 0 || start >= array.length || end < 0 || end >= array.length) {
                throw new ArrayIndexOutOfBoundsException("Start Index: " + start + " or " + "End Index: " + end + " out of bounds for array length " + array.length);
            }
            this.underlyingArray = array;
            this.startOffset = start;
            this.endOffset = end;

        }

        public IntArraySlice(int[] array) {
            this.underlyingArray = array;
            this.startOffset = 0;
            this.endOffset = array.length - 1;
        }

        public int get(int idx) {
            return underlyingArray[startOffset + idx];
        }

        public int size() {
            return endOffset - startOffset + 1;
        }

        public int getOriginalSize() {
            return underlyingArray.length;
        }

        public IntArraySlice slice(int start, int end) {
            return new IntArraySlice(this.underlyingArray, startOffset + start, startOffset + end);
        }

        public double getMedian() {
            if (this.size() == 0) {
                return 0.0;
            }

            // If array size is even
            if (this.size() % 2 == 0) {
                var index = this.size() / 2 - 1;
                return (this.get(index) + this.get(index + 1)) / 2.0;
            }

            // If array size is odd
            return this.get(this.size() / 2);
        }
    }

    public double findMedianSortedArraysOld(int[] nums1, int[] nums2) {
        var all_numbers = new ArrayList<>(Arrays.stream(nums1).boxed().toList());
        all_numbers.addAll(Arrays.stream(nums2).boxed().toList());
        Collections.sort(all_numbers);
        return new IntArraySlice(all_numbers.stream().mapToInt(Integer::intValue).toArray()).getMedian();
    }


    private boolean firstArrayIsSmaller(IntArraySlice first_arr, IntArraySlice second_arr) {
        return first_arr.get(first_arr.size() - 1) < second_arr.get(0);
    }

    // Assumes that the first_arr is smaller
    private double findMedianIfNotOverlapping(IntArraySlice first_arr, IntArraySlice second_arr) {
        if (!firstArrayIsSmaller(first_arr, second_arr)) {
            throw new IllegalArgumentException("First array must be smaller than the Second array");
        }
        var size = first_arr.size() + second_arr.size();
        int idx1 = size / 2;
        if (size % 2 == 0) {
            int idx2 = idx1 - 1;
            var value1 = getValueFromTwoNonOverlappingArrays(idx1, first_arr, second_arr);
            var value2  = getValueFromTwoNonOverlappingArrays(idx2, first_arr, second_arr);
            return (value1 + value2) / 2.0;
        }
        return getValueFromTwoNonOverlappingArrays(idx1, first_arr, second_arr);
    }

    private int getValueFromTwoNonOverlappingArrays(int idx, IntArraySlice first_arr, IntArraySlice second_arr) {
        var size = first_arr.size() + second_arr.size();
        if (idx > size - 1 || idx < 0) {
            throw new ArrayIndexOutOfBoundsException("Index was out of bounds for arrays");
        }
        IntArraySlice arr = idx < first_arr.size() ? first_arr : second_arr;
        idx = idx < first_arr.size() ? idx : idx - first_arr.size();

        return arr.get(idx);
    }

    /// Divides the array into half including the median if the size is odd.
    private IntArraySlice getHalfOfArray(IntArraySlice array, boolean upper_half) {
        var half = array.size() / 2;
        if (array.size() % 2 == 0) {
            return upper_half ? array.slice(half, array.size() - 1) : array.slice(0, half - 1);
        } else {
            return upper_half ? array.slice(half, array.size() - 1) : array.slice(0, half);
        }
    }

    private boolean withinRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    private int getValueFromTwoSortedArrays(int index, IntArraySlice firstArray, IntArraySlice secondArray) {
        int firstIndex = 0;
        int secondIndex = 0;

        for (int currentIndex = 0; currentIndex <= index; currentIndex++) {
            boolean takeFromFirst = secondIndex >= secondArray.size()
                    || firstIndex < firstArray.size() && firstArray.get(firstIndex) <= secondArray.get(secondIndex);
            if (takeFromFirst) {
                if (currentIndex == index) {
                    return firstArray.get(firstIndex);
                }
                firstIndex++;
            } else {
                if (currentIndex == index) {
                    return secondArray.get(secondIndex);
                }
                secondIndex++;
            }
        }
        throw new ArrayIndexOutOfBoundsException("Index was out of bounds for arrays");
    }

    private double adjustMedianForOriginalSize(double currentMedian, IntArraySlice firstArray,
                                               IntArraySlice secondArray) {
        int originalSize = firstArray.getOriginalSize() + secondArray.getOriginalSize();
        int currentSize = firstArray.size() + secondArray.size();
        if (originalSize % 2 == currentSize % 2) {
            return currentMedian;
        }

        int removedFromStart = firstArray.startOffset + secondArray.startOffset;
        int upperMiddleIndex = originalSize / 2 - removedFromStart;
        int upperMiddleValue = getValueFromTwoSortedArrays(upperMiddleIndex, firstArray, secondArray);
        if (originalSize % 2 == 1) {
            return upperMiddleValue;
        }

        int lowerMiddleValue = getValueFromTwoSortedArrays(upperMiddleIndex - 1, firstArray, secondArray);
        return (lowerMiddleValue + upperMiddleValue) / 2.0;
    }

    // For example for [-1,3] and [1,2] the 1,2 fits within the range -1,3
    public boolean isMedianWithinEvenRange(IntArraySlice evenArray, IntArraySlice targetArray) {
        var min = evenArray.get(evenArray.size() / 2 - 1);
        var max = evenArray.get(evenArray.size() / 2);

        var target_median = targetArray.get(targetArray.size() / 2);
        boolean isWithin = withinRange(target_median, min, max);
        if (isWithin && targetArray.size() % 2 == 0) {
            target_median = targetArray.get(targetArray.size() / 2 - 1);
            isWithin = withinRange(target_median, min, max);
        }
        return isWithin;
    }

    public double getMedianOfArrayWithAdditionalNumber(IntArraySlice array, IntArraySlice single_number_arr) {
        var single_number = single_number_arr.get(0);

        if (array.size() == 1) {
            return (array.get(0) + single_number) / 2.0;
        }

        double result;
        if (array.size() % 2 == 0) {
            var min = array.get(array.size() / 2 - 1);
            var max = array.get(array.size() / 2);
            if (withinRange(single_number, min, max)) {
                result = single_number;
            } else if (single_number < min) {
                result = min;
            } else {
                result = max;
            }
        } else {
            var idx = array.size() / 2;
            var left_of_median = array.get(idx - 1);
            var median = array.get(idx);
            var right_of_median = array.get(idx + 1);
            if (withinRange(single_number, left_of_median, right_of_median)) {
                result =  (median + single_number) / 2.0;
            } else if (single_number < left_of_median) {
                result = (median + left_of_median) / 2.0;
            } else {
                result = (median + right_of_median) / 2.0;
            }
        }
        return result;
    }


    /**
     * There's really a few terminating cases that end for this recursive function
     * 1. The arrays provided don't overlap for example, all numbers in nums1 are less than the minimum in nums2
     * 2. One of the arrays provided of size 1, however, this can be checked by 1.
     * 3. Medians are the same number
     * 4. Median of one array fits within a specific range because the other array is even sized.
     * The idea behind this algorithm is that given the position of the median for each of the two arrays
     * we can discard half of the numbers of each array by checking if the other arrays median is bigger or smaller
     * that the median in the current array.
     * @return The median of the two arrays.
     */
    private double findMedianSortedArrays(IntArraySlice nums1, IntArraySlice nums2) {

        var median1=  nums1.getMedian();
        var median2 = nums2.getMedian();

        // Empty case
        if (nums1.size() == 0 || nums2.size() == 0) {
            double result = nums1.size() == 0 ? median2 : median1;
            return adjustMedianForOriginalSize(result, nums1, nums2);
        }

        // If an array is just one number we can solve for median here
        if (nums1.size() == 1 || nums2.size() == 1) {
            var single_number_arr = nums1.size() == 1 ? nums1 : nums2;
            var other_arr = nums1.size() == 1 ? nums2 : nums1;
            double result = getMedianOfArrayWithAdditionalNumber(other_arr, single_number_arr);
            return adjustMedianForOriginalSize(result, nums1, nums2);
        }

        // Within Range case
        if (nums1.size() % 2 == 0 && isMedianWithinEvenRange(nums1, nums2)) {
            return adjustMedianForOriginalSize(median2, nums1, nums2);
        } else if (nums2.size() % 2 ==0 && isMedianWithinEvenRange(nums2, nums1)) {
            return adjustMedianForOriginalSize(median1, nums1, nums2);
        }

        // Non-overlapping case
        if (firstArrayIsSmaller(nums1, nums2)) {
            return findMedianIfNotOverlapping(nums1, nums2);
        } else if (firstArrayIsSmaller(nums2, nums1)) {
            return findMedianIfNotOverlapping(nums2, nums1);
        }

        // Overlapping cases
        var smaller_arr = median1 < median2 ? nums1 : nums2;
        var larger_arr = median1 < median2 ? nums2 : nums1;
        return findMedianSortedArrays(getHalfOfArray(smaller_arr, true), getHalfOfArray(larger_arr, false));
    }

    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        var arr1 = new IntArraySlice(nums1);
        var arr2 = new IntArraySlice(nums2);

        return findMedianSortedArrays(arr1, arr2);
    }
}
