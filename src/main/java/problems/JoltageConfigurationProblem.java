package main.java.problems;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

/**
 * Joltage Configuration Problem - Advent of Code Day 10
 * 
 * Problem Overview:
 * Configure machines by pressing buttons to set joltage counters to specific target values.
 * Each button increments certain counters by 1 when pressed. Find the minimum total button
 * presses needed to configure all machines.
 * 
 * Part 1: Light Switch Puzzle
 * Uses Depth-First Search (DFS) to find the minimum button presses needed to toggle lights
 * from all-off to a target configuration. Each button toggles specific lights on/off.
 * 
 * Part 2: Joltage Counter Configuration (Main Challenge)
 * I tried a DFS search but it took too long.
 * I realized that this is a matrix optimization problem where Ax=C where A is the matrix formed by
 * the binary vectors of the light switches and C is the resulting joltage vector.
 * We also have to add the constraints on x that all numbers are integers and are positive.
 * Also we want to minimize x.
 */
public class JoltageConfigurationProblem extends AdventOfCode{

    private static final boolean DEBUG = false;

    class Button {
        List<Integer> lightSwitches;
        List<Integer> lightSwitchVector;
        int numLights;
        
        Button(int[] switches, int numLights) {
            this.numLights = numLights;
            lightSwitches = Arrays.stream(switches)
                    .boxed()
                    .collect(Collectors.toList());
            lightSwitchVector = getLightSwitchVector();
        }

        public int getSize() {
            return lightSwitches.size();
        }

        List<Boolean> toggleLights(List<Boolean> currentLights) {
            var cloneLights = new ArrayList<Boolean>(currentLights);
            for (var switch1 : lightSwitches) {
                cloneLights.set(switch1, !cloneLights.get(switch1));
            }
            return cloneLights;
        }

        List<Integer> getLightSwitchVector() {
            var a = new ArrayList<Integer>();
            for(var i = 0; i < numLights; i++) {
                a.add(lightSwitches.contains(i) ? 1 : 0);
            }
            return a;
        }
    }

    class Machine {
        int lowestDepth = Integer.MAX_VALUE;
        List<Boolean> lights;
        List<Integer> joltage;
        List<Button> buttons;

        public Machine(List<Boolean> lights, List<int[]> buttons, int[] joltage) {
            this.lights = lights;
            int numLights = lights.size();
            this.buttons = new ArrayList<Button>(buttons.stream().map(b -> new Button(b, numLights)).sorted(Comparator.comparingInt(Button::getSize)).toList());
            this.joltage = Arrays.stream(joltage).boxed().toList();
            Collections.reverse(this.buttons);
        }

        private int BooleanArrayToIntHashCode(List<Boolean> lights) {
            var res = 0;
            for(var l : lights) {
                res = res * 10 + (l ? 1 : 0);
            }
            return res;
        }

        private boolean allLightsMatch(List<Boolean> lights) {
            for(var i = 0; i < lights.size(); i++) {
                if (lights.get(i) != this.lights.get(i)) {
                    return false;
                }
            }
            return true;
        }

        public int startSimulation() {
            var startingLightsOff = new ArrayList<Boolean>();
            lights.stream().forEach(s -> startingLightsOff.add(false));
            toggleLights(startingLightsOff, new HashSet<Integer>(), 0);
            return lowestDepth;
        }

        private int toggleLights(List<Boolean> inputLights, HashSet<Integer> seenLights, int depth)  {
            if (depth >= lowestDepth) {
                return lowestDepth;
            }

            if (allLightsMatch(inputLights)) {
                lowestDepth = depth;
                return depth;
            }

            for(var button : buttons) {
                var newLightState = button.toggleLights(inputLights);
                if (seenLights.contains(BooleanArrayToIntHashCode(newLightState))) {
                    return depth;
                }
                var cloneSeenLights = new HashSet<Integer>(seenLights);
                cloneSeenLights.add(BooleanArrayToIntHashCode(newLightState));
                toggleLights(newLightState, cloneSeenLights, depth + 1);
            }
            return depth;
        }
    }

    public JoltageConfigurationProblem(String filename, boolean useExample) {
        super(filename, useExample);
    }

    private ArrayList<Machine> LoadTextFile() {
        try (InputStream in = ReadFile()) {
            if (in == null) throw new FileNotFoundException("Resource not found");

            var content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            var lines = content.split("\r\n");
            var machines = new ArrayList<Machine>();

            for (var s : lines) {
                List<Boolean> lights = extractSingleGroup(s, "\\[(.*?)\\]").chars().mapToObj(c -> c == '#').toList();
                List<int[]> buttons = extractMultiIntGroups(s, "\\(([^)]*)\\)");
                int[] joltages = extractSingleIntGroup(s, "\\{([^}]*)\\}");
                machines.add(new Machine(lights, buttons, joltages));
            }
            return machines;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Number solvePart1() {
        var data = LoadTextFile();
        var steps = data.stream().map(Machine::startSimulation).toList();
        return steps.stream().reduce(0, Integer::sum);
    }

    @Override
    public Number solvePart2() {
        var data = LoadTextFile();
        
        long totalPresses = 0;
        for (var machine : data) {
            totalPresses += findMinimumButtonPresses(machine);
        }
        
        return totalPresses;
    }

    /**
     * Solves the Integer Linear Programming problem:
     * Minimize: sum of all button presses (x[0] + x[1] + ... + x[n])
     * Subject to: Ax = C (each counter reaches its target joltage)
     *            x[i] >= 0 and x[i] is integer for all i
     * 
     * Where A is the matrix of button vectors, C is the target joltage vector,
     * and x is the number of presses for each button.
     */
    private long findMinimumButtonPresses(Machine machine) {
        int numButtons = machine.buttons.size();
        int numCounters = machine.joltage.size();
        
        try {
            // Create the optimization model
            ExpressionsBasedModel model = new ExpressionsBasedModel();
            
            // Create variables for each button (x[0], x[1], ..., x[n])
            // Each variable is a non-negative integer
            Variable[] buttonVars = new Variable[numButtons];
            for (int i = 0; i < numButtons; i++) {
                buttonVars[i] = model.addVariable("button_" + i)
                    .lower(0)  // Non-negative
                    .integer(true);  // Integer constraint
            }
            
            // Create constraints: For each counter, sum of (button presses × button effect) = target joltage
            // Ax = C  where A[counter][button] = 1 if button affects that counter, 0 otherwise
            for (int counter = 0; counter < numCounters; counter++) {
                Expression constraint = model.addExpression("counter_" + counter);
                constraint.level(machine.joltage.get(counter));  // Set target value
                
                // Add each button's contribution to this counter
                for (int button = 0; button < numButtons; button++) {
                    int effect = machine.buttons.get(button).lightSwitchVector.get(counter);
                    if (effect > 0) {
                        constraint.set(buttonVars[button], effect);
                    }
                }
            }
            
            // Objective: Minimize total button presses (x[0] + x[1] + ... + x[n])
            Expression objective = model.addExpression("total_presses").weight(1);
            for (Variable var : buttonVars) {
                objective.set(var, 1);
            }
            
            // Solve the ILP
            Optimisation.Result result = model.minimise();
            
            if (result.getState().isOptimal()) {
                long totalPresses = Math.round(result.getValue());
                
                if (DEBUG) {
                    System.out.println("Optimal integer solution found (total: " + totalPresses + " presses):");
                    for (int i = 0; i < numButtons; i++) {
                        long presses = Math.round(result.get(i).doubleValue());
                        if (presses > 0) {
                            System.out.println("  Button " + i + ": " + presses);
                        }
                    }
                }
                
                return totalPresses;
            } else {
                if (DEBUG) {
                    System.out.println("No optimal solution found. State: " + result.getState());
                }
                return 0;
            }
            
        } catch (Exception e) {
            System.err.println("ILP solver error for machine: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /** Returns the first regex capture group match, or null if none. */
    static String extractSingleGroup(String s, String regex) {
        Matcher m = Pattern.compile(regex).matcher(s);
        return m.find() ? m.group(1) : null;
    }

    /** Extracts all matches of regex, splitting each captured group by comma into ints. */
    static List<int[]> extractMultiIntGroups(String s, String regex) {
        Matcher m = Pattern.compile(regex).matcher(s);
        List<int[]> out = new ArrayList<>();
        while (m.find()) {
            out.add(parseInts(m.group(1)));
        }
        return out;
    }

    /** Extracts a single match of regex into ints. */
    static int[] extractSingleIntGroup(String s, String regex) {
        Matcher m = Pattern.compile(regex).matcher(s);
        return m.find() ? parseInts(m.group(1)) : new int[0];
    }

    /** Parses "1,3" or "3" or " 0, 2 " into int[]. */
    static int[] parseInts(String body) {
        body = body.trim();
        if (body.isEmpty()) return new int[0];
        String[] parts = body.split("\\s*,\\s*");
        int[] arr = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = Integer.parseInt(parts[i]);
        }
        return arr;
    }
}
