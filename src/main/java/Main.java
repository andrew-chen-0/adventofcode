package main.java;

import main.java.problems.*;

import java.util.Map;
import java.util.function.Function;

public class Main {

    static String useDefaultIfNull(String name, String defaultName) {
        return name != null ? name : defaultName;
    }

    static Map<Integer, AdventOfCode> AOC_DAY_TO_PROBLEM(String filename, boolean useExample) {
        return Map.of(
                1, new RotationLockProblem(useDefaultIfNull(filename, "rotationlock.txt"), useExample),
                2, new RepeatingNumbersProblem(useDefaultIfNull(filename,"repeatingnumbers.txt"), useExample),
                3, new SlidingTwoMaximumNumbers(useDefaultIfNull(filename, "slidingtoptwonumbers.txt"), useExample),
                4, new SurroundingPositionCheck(useDefaultIfNull(filename, "slidingrolls.txt"), useExample),
                5, new RangesProblem(useDefaultIfNull(filename, "ranges.txt"), useExample),
                6, new MathOperationsProblem(useDefaultIfNull(filename, "mathproblem.txt"), useExample),
                7, new BeamSplit(useDefaultIfNull(filename, "beamsplit.txt"), useExample),
                8, new ShortestConnections(useDefaultIfNull(filename, "shortestconnection.txt"), useExample),
                9, new LargestRectangle(useDefaultIfNull(filename, "largestrectangle.txt"), useExample),
                10, new JoltageConfigurationProblem(useDefaultIfNull(filename, "dfs.txt"), useExample)

        );
    }

    static Number timeFunction(Function<Void, Number> function) {
        long start = System.nanoTime();
        var result = function.apply(null);
        long end = System.nanoTime();
        long durationNs = end - start;
        double durationMs = (durationNs / 1_000_000.0);
        System.out.println("Duration:\t" + durationMs + "ms");
        return result;
    }

    public static void main(String[] args) throws Exception {
        var day = args.length > 0 ? Integer.parseInt(args[0]) : 1;
        var filename = args.length > 1 ? args[1] : null;
        var useExample = args.length > 2 && Boolean.parseBoolean(args[2]);

        day = 10;
        useExample = false;
        var problem = AOC_DAY_TO_PROBLEM(filename, useExample).get(day);

        System.out.println("Part1:\t" + timeFunction((t) -> problem.solvePart1()));
        System.out.println();
        System.out.println("Part2:\t" + timeFunction((t) -> problem.solvePart2()));
    }
}