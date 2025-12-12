package com.adventofcode.problems;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FittingShapesInRectangle extends AdventOfCode{

    class Shape {
        int id;
        List<int[]> shapeMatrix = new ArrayList<>();
        
        // Get the height of the shape
        public int getHeight() {
            return shapeMatrix.size();
        }
        
        // Get the width of the shape
        public int getWidth() {
            return shapeMatrix.isEmpty() ? 0 : shapeMatrix.get(0).length;
        }
        
        // Rotate the shape 90 degrees clockwise
        public Shape rotate90() {
            Shape rotated = new Shape();
            rotated.id = this.id;
            
            int oldHeight = getHeight();
            int oldWidth = getWidth();
            
            // New dimensions: width becomes height, height becomes width
            for (int x = 0; x < oldWidth; x++) {
                int[] newRow = new int[oldHeight];
                for (int y = 0; y < oldHeight; y++) {
                    // (x, y) -> (y, width - 1 - x)
                    newRow[y] = shapeMatrix.get(oldHeight - 1 - y)[x];
                }
                rotated.shapeMatrix.add(newRow);
            }
            
            return rotated;
        }
        
        // Get all unique rotations (0°, 90°, 180°, 270°)
        public List<Shape> getAllRotations() {
            List<Shape> rotations = new ArrayList<>();
            Map<String, Shape> uniqueRotations = new HashMap<>();
            
            Shape current = this;
            for (int i = 0; i < 4; i++) {
                String key = getShapeSignature(current);
                if (!uniqueRotations.containsKey(key)) {
                    uniqueRotations.put(key, current);
                    rotations.add(current);
                }
                current = current.rotate90();
            }
            
            return rotations;
        }
        
        // Generate a signature for shape to detect duplicates
        private String getShapeSignature(Shape shape) {
            StringBuilder sig = new StringBuilder();
            for (int[] row : shape.shapeMatrix) {
                for (int cell : row) {
                    sig.append(cell);
                }
                sig.append("|");
            }
            return sig.toString();
        }
    }
    
    class RectangleProblem {
        int width;
        int height;
        int[] shapeCount; // Array where index represents shape ID and value is the count
    }
    
    class ShapePlacement {
        Shape shape;        // Which rotation of the shape
        int topLeftX;       // X position in the rectangle
        int topLeftY;       // Y position in the rectangle
        private List<int[]> cachedCells; // Cache occupied cells
        
        ShapePlacement(Shape shape, int x, int y) {
            this.shape = shape;
            this.topLeftX = x;
            this.topLeftY = y;
            this.cachedCells = null;
        }
        
        // Check if this placement fits within the rectangle bounds
        public boolean fitsInBounds(int rectWidth, int rectHeight) {
            return topLeftX >= 0 && topLeftY >= 0 &&
                   (topLeftX + shape.getWidth()) <= rectWidth &&
                   (topLeftY + shape.getHeight()) <= rectHeight;
        }
        
        // Get all cells occupied by this placement (cached)
        public List<int[]> getOccupiedCells() {
            if (cachedCells != null) {
                return cachedCells;
            }
            
            cachedCells = new ArrayList<>();
            for (int y = 0; y < shape.getHeight(); y++) {
                for (int x = 0; x < shape.getWidth(); x++) {
                    if (shape.shapeMatrix.get(y)[x] == 1) {
                        cachedCells.add(new int[]{topLeftX + x, topLeftY + y});
                    }
                }
            }
            return cachedCells;
        }
    }
    
    class ParsedData {
        ArrayList<Shape> shapes;
        ArrayList<RectangleProblem> problems;
    }

    private ParsedData LoadTextFile() {
        try (InputStream in = ReadFile()) {
            if (in == null) throw new FileNotFoundException("Resource not found");

            var content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            var lines = content.split("\r\n");
            
            var shapes = new ArrayList<Shape>();
            var problems = new ArrayList<RectangleProblem>();
            
            Shape currentShape = null;
            
            for (var line : lines) {
                line = line.trim();
                
                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }
                
                // Check if this is a shape ID line (e.g., "0:", "1:", etc.)
                if (line.matches("\\d+:")) {
                    // Save previous shape if it exists
                    if (currentShape != null && !currentShape.shapeMatrix.isEmpty()) {
                        shapes.add(currentShape);
                    }
                    
                    // Start a new shape
                    currentShape = new Shape();
                    currentShape.id = Integer.parseInt(line.substring(0, line.length() - 1));
                }
                // Check if this is a shape row (contains # or .)
                else if (line.contains("#") || line.contains(".")) {
                    if (currentShape != null) {
                        // Convert the line to an int array: # -> 1, . -> 0
                        int[] row = new int[line.length()];
                        for (int i = 0; i < line.length(); i++) {
                            row[i] = line.charAt(i) == '#' ? 1 : 0;
                        }
                        currentShape.shapeMatrix.add(row);
                    }
                }
                // Check if this is a problem line (e.g., "4x4: 0 0 0 0 2 0")
                else if (line.matches("\\d+x\\d+:.*")) {
                    RectangleProblem problem = new RectangleProblem();
                    
                    // Split by colon to separate dimensions from counts
                    String[] parts = line.split(":");
                    String dimensionPart = parts[0].trim();
                    String countsPart = parts[1].trim();
                    
                    // Parse dimensions (e.g., "4x4" or "12x5")
                    String[] dimensions = dimensionPart.split("x");
                    problem.width = Integer.parseInt(dimensions[0]);
                    problem.height = Integer.parseInt(dimensions[1]);
                    
                    // Parse shape counts
                    String[] countStrings = countsPart.split("\\s+");
                    problem.shapeCount = new int[countStrings.length];
                    for (int i = 0; i < countStrings.length; i++) {
                        problem.shapeCount[i] = Integer.parseInt(countStrings[i]);
                    }
                    
                    problems.add(problem);
                }
            }
            
            // Don't forget to add the last shape
            if (currentShape != null && !currentShape.shapeMatrix.isEmpty()) {
                shapes.add(currentShape);
            }
            
            ParsedData result = new ParsedData();
            result.shapes = shapes;
            result.problems = problems;
            return result;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FittingShapesInRectangle(String filename, boolean useExample) {
        super(filename, useExample);
    }
    
    private Map<String, Boolean> loadResultsCache() {
        Map<String, Boolean> cache = new HashMap<>();
        String cacheFile = "fitshapes_cache.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(cacheFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    cache.put(parts[0], Boolean.parseBoolean(parts[1]));
                }
            }
            System.out.println("Loaded " + cache.size() + " cached results from " + cacheFile);
        } catch (IOException e) {
            System.out.println("No cache file found, starting fresh");
        }

//        return cache;
        return new HashMap<>();
    }
    
    private synchronized void saveResultToCache(String problemKey, boolean feasible) {
//        String cacheFile = "fitshapes_cache.txt";
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(cacheFile, true))) {
//            writer.write(problemKey + "," + feasible);
//            writer.newLine();
//        } catch (IOException e) {
//            System.err.println("Warning: Could not save result to cache: " + e.getMessage());
//        }
    }
    
    private String getProblemKey(int problemIndex, RectangleProblem problem) {
        StringBuilder key = new StringBuilder();
        key.append(problemIndex).append("_")
           .append(problem.width).append("x").append(problem.height).append("_");
        for (int i = 0; i < problem.shapeCount.length; i++) {
            if (i > 0) key.append("-");
            key.append(problem.shapeCount[i]);
        }
        return key.toString();
    }

    @Override
    public Number solvePart1() {
        var data = LoadTextFile();
        var shapes = data.shapes;
        var problems = data.problems;
        
        Map<String, Boolean> cache = loadResultsCache();
        
        System.out.println("Total problems to solve: " + problems.size());
        System.out.println("Total shapes loaded: " + shapes.size());
        
        // Count cached results
        int cachedCount = 0;
        long cachedFeasible = 0;
        for (int i = 0; i < problems.size(); i++) {
            String key = getProblemKey(i, problems.get(i));
            if (cache.containsKey(key)) {
                cachedCount++;
                if (cache.get(key)) {
                    cachedFeasible++;
                }
            }
        }
        
        if (cachedCount > 0) {
            System.out.println("Already solved: " + cachedCount + " (skipping)");
        }
        
        // Multi-threading setup
        int numThreads = Math.min(Runtime.getRuntime().availableProcessors(), 2); // Limit to 4 to avoid memory issues
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        System.out.println("Using " + numThreads + " threads\n");
        
        final int finalCachedCount = cachedCount;
        AtomicInteger completed = new AtomicInteger(cachedCount);
        AtomicLong feasibleCount = new AtomicLong(cachedFeasible);
        AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
        
        List<Future<Boolean>> futures = new ArrayList<>();
        
        for (int i = 0; i < problems.size(); i++) {
            final int problemIndex = i;
            final RectangleProblem problem = problems.get(i);
            final String problemKey = getProblemKey(i, problem);
            
            // Check cache
            if (cache.containsKey(problemKey)) {
                System.out.printf("[%d/%d] Problem %d (%dx%d): %s (cached)%n", 
                    problemIndex + 1, problems.size(), problemIndex, 
                    problem.width, problem.height,
                    cache.get(problemKey) ? "FEASIBLE" : "INFEASIBLE");
                continue;
            }
            
            Future<Boolean> future = executor.submit(() -> {
                long taskStart = System.currentTimeMillis();
                
                System.out.printf("[%d/%d] Problem %d (%dx%d): Starting...%n", 
                    problemIndex + 1, problems.size(), problemIndex, 
                    problem.width, problem.height);
                
                boolean result = canFitAllShapes(shapes, problem);
                long duration = System.currentTimeMillis() - taskStart;
                
                int done = completed.incrementAndGet();
                if (result) {
                    feasibleCount.incrementAndGet();
                }
                
                // Save to cache
                saveResultToCache(problemKey, result);
                
                // Calculate ETA
                double avgTime = (System.currentTimeMillis() - startTime.get()) / (double) (done - finalCachedCount);
                int remaining = problems.size() - done;
                long etaSeconds = (long) (avgTime * remaining / 1000);
                String etaStr = "";
                if (remaining > 0) {
                    long etaMinutes = etaSeconds / 60;
                    etaSeconds = etaSeconds % 60;
                    if (etaMinutes > 0) {
                        etaStr = String.format(" - ETA: %dm %ds", etaMinutes, etaSeconds);
                    } else {
                        etaStr = String.format(" - ETA: %ds", etaSeconds);
                    }
                }
                
                System.out.printf("[%d/%d] Problem %d (%dx%d): %s (%.2fs)%s%n", 
                    done, problems.size(), problemIndex, 
                    problem.width, problem.height,
                    result ? "FEASIBLE" : "INFEASIBLE",
                    duration / 1000.0,
                    etaStr);
                
                return result;
            });
            
            futures.add(future);
        }
        
        // Wait for all to complete
        try {
            for (Future<Boolean> future : futures) {
                future.get();
            }
        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        
        long totalTime = System.currentTimeMillis() - startTime.get();
        
        System.out.println("\n=== Summary ===");
        System.out.println("Total feasible: " + feasibleCount.get() + "/" + problems.size());
        if (finalCachedCount > 0) {
            System.out.println("Cached results used: " + finalCachedCount);
        }
        System.out.printf("Total time: %.2fs%n", totalTime / 1000.0);
        
        return feasibleCount.get();
    }

    @Override
    public Number solvePart2() {
        return null;
    }
    
    // Generate all possible placements for a shape in a rectangle
    private List<ShapePlacement> generateAllPlacements(Shape shape, int rectWidth, int rectHeight) {
        List<ShapePlacement> placements = new ArrayList<>();
        
        // For each rotation
        for (Shape rotated : shape.getAllRotations()) {
            // For each possible position
            for (int y = 0; y <= rectHeight - rotated.getHeight(); y++) {
                for (int x = 0; x <= rectWidth - rotated.getWidth(); x++) {
                    ShapePlacement placement = new ShapePlacement(rotated, x, y);
                    if (placement.fitsInBounds(rectWidth, rectHeight)) {
                        placements.add(placement);
                    }
                }
            }
        }
        
        return placements;
    }

    public boolean canFitAllShapes(ArrayList<Shape> shapes, RectangleProblem problem) {
        Loader.loadNativeLibraries();
        
        // Early feasibility check: total shape area vs rectangle area
        int totalShapeArea = 0;
        for (Shape shape : shapes) {
            if (shape.id < problem.shapeCount.length && problem.shapeCount[shape.id] > 0) {
                int shapeArea = 0;
                for (int[] row : shape.shapeMatrix) {
                    for (int cell : row) {
                        shapeArea += cell;
                    }
                }
                totalShapeArea += shapeArea * problem.shapeCount[shape.id];
            }
        }
        
        int rectangleArea = problem.width * problem.height;
        if (totalShapeArea > rectangleArea) {
            return false; // Impossible - shapes don't fit
        }
        
        // Check if all presents can fit in their own 3x3 grid cells
        int gridCols = problem.width / 3;
        int gridRows = problem.height / 3;
        int totalGridCells = gridCols * gridRows;
        
        // Count total presents needed
        int totalPresents = 0;
        for (int count : problem.shapeCount) {
            totalPresents += count;
        }
        
        // If grid cells is less than number of presents, impossible
        if (totalGridCells >= totalPresents) {
            return true; // Not enough 3x3 grid cells for all presents
        }
        
        // Use SCIP (efficient ILP solver)
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            System.err.println("Could not create SCIP solver");
            return false;
        }

        // Optimization: Set aggressive solver parameters
        solver.setTimeLimit(60000); // 60 seconds

        // For each shape instance, create binary variables for each possible placement
        List<PlacementVariable> allPlacements = new ArrayList<>();

        for (Shape shape : shapes) {
            if (shape.id >= problem.shapeCount.length || problem.shapeCount[shape.id] == 0) {
                continue;
            }

            List<ShapePlacement> validPlacements = generateAllPlacements(shape, problem.width, problem.height);

            // For each required instance of this shape
            for (int instance = 0; instance < problem.shapeCount[shape.id]; instance++) {
                for (int p = 0; p < validPlacements.size(); p++) {
                    ShapePlacement placement = validPlacements.get(p);
                    MPVariable var = solver.makeIntVar(0, 1,
                        "s" + shape.id + "_i" + instance + "_p" + p);
                    allPlacements.add(new PlacementVariable(shape.id, instance, p, placement, var));
                }
            }
        }

        // Constraint: Each shape instance must use exactly one placement
        Map<String, List<MPVariable>> instanceVars = new HashMap<>();
        for (PlacementVariable pv : allPlacements) {
            String key = pv.shapeId + "_" + pv.instance;
            instanceVars.computeIfAbsent(key, k -> new ArrayList<>()).add(pv.variable);
        }

        for (Map.Entry<String, List<MPVariable>> entry : instanceVars.entrySet()) {
            MPConstraint constraint = solver.makeConstraint(1, 1, "instance_" + entry.getKey());
            for (MPVariable var : entry.getValue()) {
                constraint.setCoefficient(var, 1);
            }
        }

        // Constraint: Each cell can be covered by at most one shape
        // Build cell coverage map efficiently
        Map<String, List<MPVariable>> cellCoverage = new HashMap<>();
        for (PlacementVariable pv : allPlacements) {
            List<int[]> cells = pv.placement.getOccupiedCells();
            for (int[] cell : cells) {
                String cellKey = cell[0] + "_" + cell[1];
                cellCoverage.computeIfAbsent(cellKey, k -> new ArrayList<>()).add(pv.variable);
            }
        }

        // Only create constraints for cells that are actually covered
        for (Map.Entry<String, List<MPVariable>> entry : cellCoverage.entrySet()) {
            if (entry.getValue().size() > 1) { // Only add constraint if more than one placement can cover it
                MPConstraint constraint = solver.makeConstraint(0, 1, "cell_" + entry.getKey());
                for (MPVariable var : entry.getValue()) {
                    constraint.setCoefficient(var, 1);
                }
            }
        }

        // Solve (feasibility problem - no objective needed)
        MPSolver.ResultStatus status = solver.solve();

        return status == MPSolver.ResultStatus.OPTIMAL || status == MPSolver.ResultStatus.FEASIBLE;
    }
    
    // Helper class to track placement variables
    class PlacementVariable {
        int shapeId;
        int instance;
        int placementIndex;
        ShapePlacement placement;
        MPVariable variable;
        
        PlacementVariable(int shapeId, int instance, int placementIndex, ShapePlacement placement, MPVariable variable) {
            this.shapeId = shapeId;
            this.instance = instance;
            this.placementIndex = placementIndex;
            this.placement = placement;
            this.variable = variable;
        }
    }
}
