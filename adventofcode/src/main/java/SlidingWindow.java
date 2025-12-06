package main.java;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// We are given an input like ..@@.@@@@. if there are 4 or more rolls in the 8 squares adjacent
// then we are unable to pick up the roll here ..xx.xx@x. we can see an example
public class SlidingWindow {

    private static int EMPTY = 0;
    private static int ROLL = 1;
    private static int ROLL_TO_REMOVE = 2;

    private List<int[]> LoadTextFile() {
        try (InputStream in = RotationLockProblem.class.getResourceAsStream("/data/slidingrolls.txt")) {
            if (in == null) throw new FileNotFoundException("Resource not found");

            var content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            return Arrays.stream(content.split("\r\n")).map(s -> {
                return s.chars().map(i -> i == '@' ? ROLL : EMPTY).toArray();
            }).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // Answer is 1486
    public int solvePart1() {
        var input = LoadTextFile();
        return findNumberOfRolls(input, 1, 4);
    }

    // Answer is 9024
    public int solvePart2() {
        var input = LoadTextFile();
        var result = 0;
        var itr = 0;
        do {
            itr = findNumberOfRolls(input, 1, 4);
            result += itr;
            removeRolls(input);
        }while(itr > 0);
        return result;
    }

    /**
     * We keep a rolling number of the amount of rolls adjacent
     * we then add one on and as the window passes we subtract a roll off
     *
     * @param input_grid ..@@.@@@@. == [0,0,1,1,0,1,1,1,1,0] and there are multiple lines
     * @param windowSize How far to expand adjacently
     * @param rollLimit Part 1 says max amount of rolls is less than 4
     * @return the number of rolls that are acceptable
     */
    public int findNumberOfRolls(List<int[]> input_grid, int windowSize, int rollLimit) {
        var validRolls = 0;
        for(var row = 0; row < input_grid.size(); row++) {
            for(var col = 0; col < input_grid.get(row).length; col++) {
                if (input_grid.get(row)[col] == 1) {
                    var adjacentRolls = calculateAdjacentRollsAtPosition(input_grid, row, col, windowSize);
                    if (adjacentRolls - 1 < rollLimit) {

                        // Here for Part 2 if a roll is valid we mark the value for removal in another stage
                        input_grid.get(row)[col] = ROLL_TO_REMOVE;
                        validRolls++;
                    }
                }
            }
        }
        return validRolls;
    }

    private void removeRolls(List<int[]> input_grid) {
        for(var row = 0; row < input_grid.size(); row++) {
            for (var col = 0; col < input_grid.get(row).length; col++) {
                if (input_grid.get(row)[col] == ROLL_TO_REMOVE) {
                    input_grid.get(row)[col] = 0;
                }
            }
        }
    }

    public int calculateAdjacentRollsAtPosition(List<int[]> input_grid, int row, int col, int windowSize) {
        var startRow = row - windowSize;
        var startCol = col - windowSize;
        var result = 0;
        for(var itr_row = startRow; itr_row <= startRow + windowSize * 2; itr_row++) {
            if (itr_row < 0) {
                itr_row = -1;
                continue;
            }
            if (itr_row > input_grid.size() - 1) {
                break;
            }
            for (var itr_col = startCol; itr_col <= startCol + windowSize * 2; itr_col++) {
                if (itr_col < 0) {
                    itr_col = -1;
                    continue;
                }
                if (itr_col > input_grid.get(itr_row).length - 1) {
                    break;
                }
                result += input_grid.get(itr_row)[itr_col] != EMPTY ? 1 : 0;
            }
        }
        return result;
    }
}
