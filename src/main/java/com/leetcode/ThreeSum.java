package com.leetcode;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ThreeSum implements ILeetCodeProblem {

    // Given a list of numbers return all pairs that add up to the target
    public static List<List<Integer>> twoSumExtended(int[] nums, int target) {
        List<List<Integer>> results = new ArrayList<>();
        if (nums.length < 2) {
            return results;
        }

        int smallPointer = 0;
        for (int bigPointer = 1; bigPointer < nums.length; bigPointer++) {
            if (nums[bigPointer] > target) {
                break;
            }

            for(;smallPointer >= 0; smallPointer--) {
                if (nums[bigPointer] + nums[smallPointer] < target) {
                    smallPointer++;
                    break;
                }else if (nums[bigPointer] + nums[smallPointer] == target) {
                    results.add(List.of(nums[smallPointer], nums[bigPointer]));
                    break;
                }
            }

            if (smallPointer ==  -1) {
                break;
            }
        }
        return results;
    }


    public static List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        var results = new ArrayList<List<Integer>>();

        // Both are inclusive
        int negativeNumberEndIdx = -1;
        int positiveNumberStartIdx = -1;
        int num_of_zeros = 0;
        for(int i = 0; i < nums.length; i++) {
            if (nums[i] < 0) {
                negativeNumberEndIdx = i;
            }
            if (nums[i] == 0) {
                num_of_zeros++;
            }
            if (nums[i] > 0) {
                positiveNumberStartIdx = i;
                break;
            }
        }
        if (num_of_zeros > 2) {
            results.add(List.of(0, 0, 0));
        }

        // Either missing positive numbers or negative numbers so cannot be used to cancel out each other when summing
        if (positiveNumberStartIdx == -1 || negativeNumberEndIdx == -1) {
            return results;
        }

        // Need at least 2 numbers to do twoSum
        var include_zeros = num_of_zeros > 0 ? 1 : 0;
        var negativeNumbers = Arrays.stream(Arrays.copyOfRange(nums, 0, negativeNumberEndIdx + 1 + include_zeros)).map(Math::abs).toArray();
        reverseArray(negativeNumbers);
        var positiveNumbers = Arrays.copyOfRange(nums, positiveNumberStartIdx - include_zeros, nums.length);
        if (negativeNumbers.length > 1) {
            Arrays.stream(positiveNumbers).forEach(i -> {
                var values = twoSumExtended(negativeNumbers, i);
                values.forEach(pair -> {
                    var smaller = Math.min(-pair.get(0), -pair.get(1));
                    var larger = Math.max(-pair.get(0), -pair.get(1));
                    results.add(List.of(smaller, larger, i));
                });
            });
        }

        if (positiveNumbers.length > 1) {
            Arrays.stream(negativeNumbers).forEach(i -> {
                var values = twoSumExtended(positiveNumbers, i);
                values.forEach(pair -> {
                    results.add(List.of(-i, pair.get(0), pair.get(1)));
                });
            });
        }
        return results.stream().distinct().toList();
    }

    private static void reverseArray(int [] arr) {
        for (int i = 0; i < arr.length / 2; i++) {
            int temp = arr[i];
            arr[i] = arr[arr.length - 1 - i];
            arr[arr.length - 1 - i] = temp;
        }
    }

    @Override
    public void RunTestCases() {
        System.out.println(threeSum(new int[]{-1, 0, 1, 2, -1, -4 }));
        System.out.println(threeSum(new int[]{0, 1, 1 }));
    }
}
