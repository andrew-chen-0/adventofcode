package main.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class SlidingTwoMaximumNumbers {

    class FixedSizeQueue {
        long[] array;
        public FixedSizeQueue(int size) {
            array = new long[size];
        }

        public void add(long num) {
            // push all numbers back
            boolean isShifting  = false;
            for(var i = 0; i < array.length - 1; i++) {
                if (!isShifting && array[i] < array[i + 1]) {
                    isShifting = true;
                }
                if (isShifting) {
                    array[i] = array[i + 1];
                }
            }
            if (isShifting || array[array.length - 1] <  num) {
                array[array.length - 1] = num;
            }

        }

        public long getResult() {
            var res = 0L;
            for(var i = 0; i < array.length; i++) {
                res = res * 10 + array[i];
            }
            return res;
        }
    }

    private List<int[]> LoadTextFile() {
        try (InputStream in = RotationLockProblem.class.getResourceAsStream("/data/slidingtoptwonumbers.txt")) {
            if (in == null) throw new FileNotFoundException("Resource not found");

            var content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return Arrays.stream(content.split("\r\n")).map(s -> {
                return s.chars().map(i -> i - '0').toArray();
            }).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long solvePart1() {
        return findTwoDigitsToMakeLargestNumber(2);
    }

    public long solvePart2() {
        return findTwoDigitsToMakeLargestNumber(12);
    }

    // Given a number 987654321111111 we want to find the two digits that will make the biggest number in this case 98
    // In 811111111111119, you can make the largest joltage possible by turning on the batteries labeled 8 and 9, producing 89 jolts.
    public long findTwoDigitsToMakeLargestNumber(int size) {
        var numbers = LoadTextFile();
        var result = numbers.stream().map(array -> {
            var queue = new FixedSizeQueue(size);
            for(var i = 0; i < array.length; i++) {
                queue.add(array[i]);
            }
            return queue.getResult();
        }).reduce(0L, Long::sum);
        return result;
    }
}
