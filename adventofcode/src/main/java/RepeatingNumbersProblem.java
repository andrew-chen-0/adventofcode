package main.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * IDs are considered invalid if they repeat twice ex 1010, 123123, 99
 * You are given a min and a max number inclusive. Find the sum of all the invalid IDs
 * Numbers must repeat twice and IDs cannot start with 0.
 */
public class RepeatingNumbersProblem {

    private List<long[]> LoadTextFile() {
        try (InputStream in = RotationLockProblem.class.getResourceAsStream("/data/repeatingnumbers.txt")) {
            if (in == null) throw new FileNotFoundException("Resource not found");

            var content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return Arrays.stream(content.split(",")).map(s -> {
                var minMaxPair = s.split("-");
                return new long[]{Long.parseLong(minMaxPair[0]), Long.parseLong(minMaxPair[1]) };
            }).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void findTotalOfRepeatingNumbersInFile() {
        var minMaxPairs = LoadTextFile();
        var result1 = minMaxPairs.stream()
                .map(pair -> findTotalOfRepeatingNumbersSlow(pair[0], pair[1]))
                .reduce(0L, Long::sum);
        var result2 = minMaxPairs.stream()
                .map(pair -> findTotalOfRepeatingNumbersFast(pair[0], pair[1]))
                .reduce(0L, Long::sum);
        System.out.println(result1);
        System.out.println(result2);
    }

    public long findTotalOfRepeatingNumbersSlow(long min, long max) {
        var res = 0L;
        for(long i = min; i <= max; i++) {
            var numOfDigits = (long)Math.floor(Math.log10(i)) + 1;
            if (numOfDigits % 2 != 0) {
                continue;
            }
            var base_increment = (long)Math.pow(10, numOfDigits / 2);
            var top_half = i / base_increment;
            var bottom_half = i - (top_half * base_increment);
            if (top_half == bottom_half) {
                res += i;
            }
        }
        return res;
    }

    private long getNumberOfDigits(long number) {
        return (long)Math.floor(Math.log10(number)) + 1;
    }

    public long findTotalOfRepeatingNumbersFast(long min, long max) {
        var minNumOfDigits = getNumberOfDigits(min);
        var maxNumOfDigits = getNumberOfDigits(max);
        if (maxNumOfDigits == 1) {
            return 0;
        }

        // 4365 becomes 4 which becomes 10^2 which can be used to seperate 43 and 65
        // Also for an odd number of digits it means we push the exponent to the nearest even number
        var minHalfwaySeperator = minNumOfDigits > 1 ? (long)Math.pow(10, Math.ceilDiv(minNumOfDigits, 2)) : 1;
        var maxHalfwaySeperator = (long)Math.pow(10, Math.floorDiv(maxNumOfDigits, 2));

        // Here if we have a number like 5170
        // for 5100 we expect 5151 to exist however if the min is higher
        // we can just increment it to 5200 as the new lower bound.
        // Upper and lower bounds are represented as their unrepeated value e.g. 5252 is 52
        // If the min had an odd number of digits we set it as the minimum value of number of digits plus 1 which is even
        // We do the inverse for maximum
        var lower_bound = minHalfwaySeperator;
        if (minNumOfDigits % 2 == 0) {
            lower_bound = min / minHalfwaySeperator; // Strip top half
            if (lower_bound * minHalfwaySeperator + lower_bound < min) {
                lower_bound += 1;
            }
        }

        var upper_bound = maxHalfwaySeperator - 1; // 10 becomes 9
        if (maxNumOfDigits % 2 == 0) {
            upper_bound = max / maxHalfwaySeperator;
            if (upper_bound * maxHalfwaySeperator + upper_bound > max) {
                upper_bound -= 1;
            }
        }

        if (upper_bound < lower_bound) {
            return 0;
        }

        minNumOfDigits = getNumberOfDigits(lower_bound);
        maxNumOfDigits = getNumberOfDigits(upper_bound);

        var sumMinToMaxRepeatingNumbers = 0L;
        for(var i = minNumOfDigits; i <= maxNumOfDigits; i++) {
            var minValue = i == minNumOfDigits ? lower_bound : (long)Math.pow(10, i);            // Min value for 2 digits is 10
            var maxValue = i == maxNumOfDigits ? upper_bound : (long)Math.pow(10, i + 1) - 1;    // Max value for 2 digits is 99
            var sumMinToMax = calculateSumOfConsecutiveIntegers(minValue, maxValue);
            sumMinToMaxRepeatingNumbers = sumMinToMax * (long)Math.pow(10, i) + sumMinToMax;
        }

        return sumMinToMaxRepeatingNumbers;
    }

    private long calculateSumOfConsecutiveIntegers(long min, long max) {
        var average = (max + min) / 2.0f;
        return Math.round(average * (max - min + 1));
    }
}
