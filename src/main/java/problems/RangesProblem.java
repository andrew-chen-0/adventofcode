package main.java.problems;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

// We have input like 11-24, 2-4, 5-6 and we get given numbers that we want to check fit into some range
// If the ranges are overlapping like 11-24, 15-30 we want to extend the range to become a single one 11-30
public class RangesProblem extends AdventOfCode {

    public RangesProblem(String filename, boolean useExample) {
        super(filename, useExample);
    }

    // Ranges are inclusive
    class Range {
        long min;
        long max;

        Range(long min, long max) {
            this.min = min;
            this.max = max;
        }

        long GetSizeOfRange() {
            return max - min + 1;
        }

        boolean IsInRange(long num) {
            return num >= min && num <= max;
        }

        boolean TryCombineRange(Range range) {
            boolean result = false;
            if (IsInRange(range.min)) {
                max = Math.max(range.max, max);
                result = true;
            }

            if (IsInRange(range.max)) {
                min = Math.min(range.min, min);
                result = true;
            }

            // Or if the other range overlaps our range
            if (range.IsInRange(min) && range.IsInRange(max)) {
                max = range.max;
                min = range.min;
                result = true;
            }
            return result;
        }
    }

    class LoadedFileData {
        public ArrayList<Range> Ranges;
        public ArrayList<Long>  InputsToCheck;
        LoadedFileData(ArrayList<Range>ranges, ArrayList<Long> inputs) {
            Ranges = ranges;
            InputsToCheck = inputs;
        }
    }

    boolean useExample = false;
    private LoadedFileData LoadTextFile() {
        try (InputStream in = ReadFile()) {
            if (in == null) throw new FileNotFoundException("Resource not found");

            var content = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            var rangeList = new ArrayList<Range>();
            var inputsToCheck = new ArrayList<Long>();
            var lines = content.split("\r\n");
            for (var line : lines) {
                if (line.length() == 0) {
                    continue;
                }
                var s = line.split("-");
                if (s.length == 2) {
                    AddRangeFromInputFile(rangeList, s[0], s[1]);
                } else {
                    inputsToCheck.add(Long.parseLong(line));
                }
            }
            return new LoadedFileData(rangeList, inputsToCheck);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void AddRangeFromInputFile(ArrayList<Range> rangeList, String minValue, String maxValue) {
        var range = new Range(Long.parseLong(minValue), Long.parseLong(maxValue));

        // Check if ranges can be merged with any existing ranges
        // Also loop and merge any new ranges Ex 10-14, 16-18, 12-20
        // 10-14 and 16-18 cannot merge but 12-20 bridges them
        // 10-20 we then merge 16-18 into this.
        boolean didCombine = false;
        do {
            didCombine = false;
            for (var r : rangeList) {
                if (r.TryCombineRange(range)) {
                    rangeList.remove(r);
                    range = r;
                    didCombine = true;
                    break;
                }
            }
        }while(didCombine);
        if (!didCombine) {
            rangeList.add(range);
        }
    }

    // Answer is 770
    // Here we load all the ranges and check to see how many IDs fall in those ranges
    public Integer solvePart1() {
        var data = LoadTextFile();

        var validCount = 0;
        for(var ID : data.InputsToCheck) {
            for (var range : data.Ranges) {
                if (range.IsInRange(ID)) {
                    validCount++;
                    break;
                }
            }
        }
        return validCount;
    }


    // They want to know all numbers within the ranges
    // Ex. 11 - 15 would be 11, 12, 13, 14, 15 which means there are 5 numbers
    public Long solvePart2() {
        var data = LoadTextFile();
        long result = 0;
        for(var range : data.Ranges) {
            result += range.GetSizeOfRange();
        }
        return result;
    }


}
