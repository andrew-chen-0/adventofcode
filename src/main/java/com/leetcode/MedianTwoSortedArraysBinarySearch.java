package com.leetcode;

public class MedianTwoSortedArraysBinarySearch {

    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) {
            return findMedianSortedArrays(nums2, nums1);
        }
        if (nums1.length == 0 && nums2.length == 0) {
            return 0.0;
        }

        int leftSize = (nums1.length + nums2.length + 1) / 2;
        int low = 0;
        int high = nums1.length;

        while (low <= high) {
            int firstPartition = low + (high - low) / 2;
            int secondPartition = leftSize - firstPartition;

            long firstLeft = firstPartition == 0 ? Long.MIN_VALUE : nums1[firstPartition - 1];
            long firstRight = firstPartition == nums1.length ? Long.MAX_VALUE : nums1[firstPartition];
            long secondLeft = secondPartition == 0 ? Long.MIN_VALUE : nums2[secondPartition - 1];
            long secondRight = secondPartition == nums2.length ? Long.MAX_VALUE : nums2[secondPartition];

            if (firstLeft > secondRight) {
                high = firstPartition - 1;
            } else if (secondLeft > firstRight) {
                low = firstPartition + 1;
            } else {
                long leftMiddle = Math.max(firstLeft, secondLeft);
                if ((nums1.length + nums2.length) % 2 == 1) {
                    return leftMiddle;
                }

                long rightMiddle = Math.min(firstRight, secondRight);
                return (leftMiddle + rightMiddle) / 2.0;
            }
        }

        throw new IllegalArgumentException("Input arrays must be sorted");
    }
}