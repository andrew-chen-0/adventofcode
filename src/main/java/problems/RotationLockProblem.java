package main.java.problems;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * We have a rotating lock with numbers from 0-99
 * We can define moves L19 or R25 where the letter represents the direction
 * and the number represents the number of steps.
 * The lock starts at position 50
 *
 * For the first part we want to count number of times the lock stops at position 0
 * For the second part we want to count the number of times the position crosses 0
 * Note that given it is a rotation lock if it passes 99 it wraps back to 0
 */
public class RotationLockProblem extends AdventOfCode {

    private static int POSITION = 50;

    public RotationLockProblem(String filename, boolean useExample) {
        super(filename, useExample);
    }

    private int[] LoadTextFile() {
        try (InputStream in = ReadFile()) {
            if (in == null) throw new FileNotFoundException("Resource not found");

            var content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            var actions = content.split("\n");
            var moves = new int[actions.length];
            for (int i = 0; i < actions.length; i++) {
                int sign = actions[i].charAt(0) == 'R' ? 1 : -1;
                moves[i] = sign * Integer.parseInt(actions[i].substring(1).trim());
            }
            return moves;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Answer is 1141
    public int GetNumberOfTimesLockStopsAt0() {
        var moves = LoadTextFile();
        var position = POSITION;
        var count = 0;
        for(var move : moves) {
            position += move;
            if (position % 100 == 0) {
                count++;
            }
        }
        return count;
    }

    // Answer is 6634
    public int GetTotalNumberOfTimes0isPassed() {
        var moves = LoadTextFile();
        var position = POSITION;
        var count = 0;
        for(var move : moves) {
            var remainder = move % 100;
            var rotations = Math.abs(move / 100);
            count += rotations;
            var oldPosition = position;
            position += remainder;
            if (position < 0) {
                position += 100;
                if (oldPosition != 0) {
                    count++;
                }
            } else if (position > 99) {
                position -= 100;
                count++;
            } else if (position == 0) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Number solvePart1() {
        return GetNumberOfTimesLockStopsAt0();
    }

    @Override
    public Number solvePart2() {
        return GetTotalNumberOfTimes0isPassed();
    }
}
