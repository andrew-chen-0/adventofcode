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
                .map(pair -> findTotalOfEvenDigitRepeatingNumbersSlow(pair[0], pair[1]))
                .reduce(0L, Long::sum);
        var result2 = minMaxPairs.stream()
                .map(pair -> findTotalOfEvenDigitRepeatingNumbersFast(pair[0], pair[1]))
                .reduce(0L, Long::sum);
        var oddNumberTotal = minMaxPairs.stream()
                .map(pair -> {
                    System.out.println(pair[0] + "\t" + pair[1]);
                    var res = findTotalOfOddDigitRepeatingNumbers(pair[0], pair[1]);
                    var res2 = findTotalOfOddDigitRepeatingNumbersSlow(pair[0], pair[1]);
                    System.out.println("Res1\t" + res);
                    System.out.println("Res2\t" + res2);
                    return res;
                })
                .reduce(0L, Long::sum);
        var oddNumberSlowTotal = minMaxPairs.stream()
                .map(pair -> findTotalOfOddDigitRepeatingNumbersSlow(pair[0], pair[1]))
                .reduce(0L, Long::sum);
        System.out.println("Slow result:\t" + result1);
        System.out.println("Fast result:\t" + result2);
        System.out.println("With odds:\t" + (oddNumberTotal));
        System.out.println("Odds slow:\t" + oddNumberSlowTotal);
    }

    public long findTotalOfEvenDigitRepeatingNumbersSlow(long min, long max) {
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

    public long findTotalOfEvenDigitRepeatingNumbersFast(long min, long max) {
        var minNumOfDigits = getNumberOfDigits(min);
        var maxNumOfDigits = getNumberOfDigits(max);
        if (maxNumOfDigits == 1) {
            return 0;
        }

        // 4365 becomes 4 which becomes 10^2 which can be used to seperate 43 and 65
        // Also for an odd number of digits it means we push the exponent to the nearest even number
        var minHalfwaySeperator = minNumOfDigits > 1 ? (long)Math.pow(10, Math.ceilDiv(minNumOfDigits, 2)) : 1;
        var maxHalfwaySeperator = (long)Math.pow(10, Math.floorDiv(maxNumOfDigits, 2));


        // If it is an odd number of digits we set the lower bound as the halfway seperator
        // Ex. 123 becomes 10 since the next repeating digit after 123 is 1010.
        var lower_bound = minHalfwaySeperator;
        if (minNumOfDigits % 2 == 0) {
            // Here if we have a number like 5170
            // We first take the first half 51 we then check if the repeating number 5151 is smaller than min
            // if it is we increment it by 1 to become 52 since 5252 is greater than the min.
            // Upper and lower bounds are represented as their unrepeated value e.g. 5252 is 52
            // If the min had an odd number of digits we set it as the minimum value of number of digits plus 1 which is even
            // We do the inverse for maximum
            lower_bound = min / minHalfwaySeperator; // Strip top half
            if (lower_bound * minHalfwaySeperator + lower_bound < min) {
                lower_bound += 1;
            }
        }

        // We do inverse for max Ex. 123 becomes 9 because 99 is the next repeating digit less than 123
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

    public long findTotalOfOddDigitRepeatingNumbersSlow(long min, long max) {
        var res = 0;
        for(var i = min; i <= max; i++) {
            var numDigits = getNumberOfDigits(i);
            if (numDigits % 2 == 0 || numDigits == 1) {
                continue;
            }
            if (i % getDigitsAsOnes(numDigits) == 0 ) {
                res += i;
            }
        }
        return res;
    }

    // For this given a min and a max find the sum of all the numbers with an odd number of digits
    // That repeat like so
    // 111, 22222, 3333333 they must all be the same digit and must have an odd number of digits
    public long findTotalOfOddDigitRepeatingNumbers(long min, long max) {
        // The number 1 by itself does not qualify as repeating so we have a minimum of 111
        min = Math.max(111, min);

        var minNumOfDigits = getNumberOfDigits(min);
        var maxNumOfDigits = getNumberOfDigits(max);
        if (maxNumOfDigits < 3) {
            return 0;
        }

        // Get the first digit ex 532 is 5
        var lower_bound = min / (long)Math.pow(10, minNumOfDigits - 1);
        var upper_bound = max / (long)Math.pow(10, maxNumOfDigits - 1);

        // If the number of digits is even we increase the number of digits and reset lower bound to 1
        // Ex. A min of 13 becomes the lower bound of 111 because its the next nearest number
        if (minNumOfDigits % 2 == 0) {
            minNumOfDigits++;
            lower_bound = 1;
        }

        if (maxNumOfDigits % 2 == 0) {
            maxNumOfDigits--;
            upper_bound = 9;
        }

        // At this point we know that the lower and upper bounds both have an odd number of digits
        // now we need to check that they actually fit within the max and mins
        // Check if lower_bound is in breach of min
        if (lower_bound * getDigitsAsOnes(minNumOfDigits) < min) {
            lower_bound++;
            if (lower_bound == 10) {
                lower_bound = 1;
                minNumOfDigits += 2;
            }
        }

        if (upper_bound * getDigitsAsOnes(maxNumOfDigits) > max) {
            upper_bound--;
            if (upper_bound == 0) {
                upper_bound = 9;
                maxNumOfDigits -= 2;
            }
        }

        if (minNumOfDigits > maxNumOfDigits) {
            return 0;
        }
        var total = 0;
        for(var i = minNumOfDigits; i <= maxNumOfDigits; i+=2) {
            var minForIteration = i == minNumOfDigits ? lower_bound : 1;
            var maxForIteration = i == maxNumOfDigits ? upper_bound : 9;
            total += calculateSumOfConsecutiveIntegers(minForIteration, maxForIteration) * getDigitsAsOnes(i);
        }
        return total;
    }

    // Given a number returns the number of digits 123 = 3, 2345 = 4
    private long getNumberOfDigits(long number) {
        return (long)Math.floor(Math.log10(number)) + 1;
    }

    // Given a number like 3 returns same number of digits as ones like 3 = 111, 4 = 1111.
    private long getDigitsAsOnes(long numDigits) {
        return ((long)Math.pow(10, numDigits) - 1) / 9;
    }

    private long calculateSumOfConsecutiveIntegers(long min, long max) {
        var average = (max + min) / 2.0f;
        return Math.round(average * (max - min + 1));
    }
}
