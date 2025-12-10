package main.java.problems;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * IDs are considered invalid if they repeat twice ex 1010, 123123, 99
 * You are given a min and a max number inclusive. Find the sum of all the invalid IDs
 * Numbers must repeat twice and IDs cannot start with 0.
 */
public class RepeatingNumbersProblem extends AdventOfCode {

    private static List<Long> PRIME_NUMBER_CACHE = new ArrayList<>(List.of(2L, 3L, 5L, 7L, 11L));

    public RepeatingNumbersProblem(String filename, boolean useExample) {
        super(filename, useExample);
    }

    private List<long[]> LoadTextFile() {
        try (InputStream in = ReadFile()) {
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

    public long findTotalOfNumbersRepeatingTwiceSlow(long min, long max) {
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

    public long findTotalOfNumbersRepeatingTwiceFast(long min, long max) {
        var minNumOfDigits = getNumberOfDigits(min);
        var maxNumOfDigits = getNumberOfDigits(max);
        if (maxNumOfDigits == 1) {
            return 0;
        }

        // 4365 becomes 4 which becomes 10^2 which can be used to seperate 43 and 65
        // Also for an odd number of digits it means we push the exponent to the nearest even number
        var minHalfwaySeperator = minNumOfDigits > 1 ? (long)Math.pow(10, (minNumOfDigits + 1) / 2) : 1;
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


    // Find given a max and min number the sum of all numbers that have repeating digits of any kind
    // So, 12341234 (1234 two times), 123123123 (123 three times), 1212121212 (12 five times), and 1111111 (1 seven times) are all counted.
    public long findTotalOfAnyRepeatingDigit(long min, long max) {
        var minNumDigits = getNumberOfDigits(min);
        var maxNumDigits = getNumberOfDigits(max);
        var total = -totalOfRepeatingNumbersWithSameNumberOfDigits(min - 1);
        for(var i = minNumDigits; i <= maxNumDigits; i++) {
            var maxValue = maxNumDigits == i ? max : (long)Math.pow(10, i) - 1;
            total += totalOfRepeatingNumbersWithSameNumberOfDigits(maxValue);
        }
        return total;
    }

    // Given a N digit number such as 123456 we want to now find all the
    // Repeating numbers between 100000 and that number. We do this by finding the factors in this case 1,2,3
    // then creating a window which we use to find the smallest number
    // Ex. 123456
    // 1 = [1,2,3,4,5,6] = 1 is the smallest
    // 2 = [12, 34, 56] = 12 is the smallest
    // 3 = [123, 456] = 123 is the smallest
    // Given a number we can extrapolate n - 10^(floor(log10(n)) - 1) + 1
    // In this case 12 => 12 - 10 + 1 = 3 numbers 121212, 111111, 101010
    // Another example 223 => 223 - 100 + 1 = 124 numbers
    public long totalOfRepeatingNumbersWithSameNumberOfDigits(long number) {
        var numDigits = getNumberOfDigits(number);
        var factors = getFactors(numDigits);
        if (numDigits < 2) {
            return 0;
        }
        factors.add(1L); // Can't forget 1
        var sum_of_all_patterns = factors.stream()
            .map(factor -> {
                var upper_bound = number/(long)Math.pow(10, numDigits - factor);

                if (buildNumber(upper_bound, factor, numDigits / factor) > number) {
                    upper_bound--;
                }

                if (getNumberOfDigits(upper_bound) != factor) {
                    return 0L;
                }

                var sum = calculateSumOfConsecutiveIntegers((long)Math.pow(10, factor - 1), upper_bound);
                return buildNumber(sum, factor, numDigits / factor);
            }).toList();
        var sum_of_all_patterns_list = new ArrayList<>(sum_of_all_patterns);

        // Here we have the unfortunate scenario of double counting
        // for example 222222 is also 2 2 2 2 2 2 or 22 22 22 22 22 22 or 222 222 and is counted multiple times
        // we get rid of this by subtracting factors from the count
        for(var i = 0; i < factors.size(); i++) {
            for(var j = 0; j < i; j++) {
                if (factors.get(i) % factors.get(j) == 0) {
                    sum_of_all_patterns_list.set(i, sum_of_all_patterns.get(i) - sum_of_all_patterns.get(j));
                }
            }
        }

        var repeating_numbers = new HashSet<Long>();
        factors.stream().forEach(factor -> {
            var upper_bound = number/(long)Math.pow(10, numDigits - factor);
            if (buildNumber(upper_bound, factor, numDigits / factor) > number) {
                upper_bound--;
            }
            if (getNumberOfDigits(upper_bound) != factor) {
                return;
            }
            for(var i = (long)Math.pow(10, factor - 1); i <= upper_bound; i++) {
                repeating_numbers.add(buildNumber(i, factor, numDigits / factor));
            }
        });

        var alt = repeating_numbers.stream().reduce(0L, Long::sum);
        var result = sum_of_all_patterns_list.stream().reduce(0L, Long::sum);
        return alt;
    }

    public long buildNumber(long number, long numDigits, long count) {
        var result = 0L;
        for(var i = 0; i < count; i++) {
            result += number * (long)Math.pow(10, i * numDigits);
        }
        return result;
    }

    // Returns a list of the prime factors of a given number
    // Ex. 21 = [3,7]         I know 1 is technically not a prime number
    private List<Long> getFactors(long number) {
        var factor_list = new ArrayList<Long>();

        // If half the number is bigger than our prime cache we need to generate prime numbers up
        // to half the number because they can be potential factors
        if (number / 2 > PRIME_NUMBER_CACHE.get(PRIME_NUMBER_CACHE.size() - 1)) {
            generatePrimesForCache(number / 2);
        }

        for (var prime : PRIME_NUMBER_CACHE) {
            if (number % prime == 0 && number != prime) {
                factor_list.add(prime);
                factor_list.add(number/prime);
            }
        }
        return new ArrayList<>(factor_list.stream().distinct().toList());
    }

    private void generatePrimesForCache(long max_value) {
        var current_max = PRIME_NUMBER_CACHE.get(PRIME_NUMBER_CACHE.size() - 1);
        for (var i = current_max; i <= max_value; i += 2) {
            boolean isPrime = true;
            for(var prime : PRIME_NUMBER_CACHE) {
                if (i % prime == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                PRIME_NUMBER_CACHE.add(i);
            }
        }
    }

    // Given a number this returns the number of digits 123 = 3, 2345 = 4
    private long getNumberOfDigits(long number) {
        return (long)Math.floor(Math.log10(number)) + 1;
    }


    public long calculateSumOfConsecutiveIntegers(long min, long max) {
        var average = (max + min) / 2.0;
        return (long)(average * (max - min + 1));
    }

    @Override
    public Number solvePart1() {
        var minMaxPairs = LoadTextFile();
        return minMaxPairs.stream()
                .map(pair -> findTotalOfNumbersRepeatingTwiceFast(pair[0], pair[1]))
                .reduce(0L, Long::sum);
    }

    @Override
    public Number solvePart2() {
        var minMaxPairs = LoadTextFile();
        return minMaxPairs.stream()
                .map(pair -> findTotalOfAnyRepeatingDigit(pair[0], pair[1]))
                .reduce(0L, Long::sum);
    }
}
