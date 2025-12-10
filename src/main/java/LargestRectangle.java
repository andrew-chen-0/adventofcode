package main.java;

import main.java.utilities.Edge;
import main.java.utilities.Node;
import main.java.utilities.Rectangle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LargestRectangle {

    // A class to define an outside perimeter of the shape efficiently
    class OutsidePerimeter {
        HashMap<Long, TreeSet<Long>> rowToColValues = new HashMap<>();
        HashMap<Long, TreeSet<Long>> colToRowValues = new HashMap<>();

        // This takes all the values 1 block outside the perimeter to construct a perimeter of values we can use to check
        // when a testing rectangles perimeter hits these values we know it has gone out of the defined shape.
        // Limitations:
        // - The shape defined by the perimeter must be one single shape the path of the shape cannot cross over
        // - The perimeter + 1 does not include corner block this is not neccessary because we know that since we are making
        //   rectangles the perimeters are guaranteed to be orthogonal to any red squares which are also used to define the path.
        public OutsidePerimeter(List<Node> data, int direction) {
            var newDirection = 0L;
            var point0 = data.get(data.size() - 1).getCoordinates();
            var point1 = data.get(0).getCoordinates();
            for (var i = 1; i < data.size() + 1; i++) {
                var j = i % data.size();
                var point2 = data.get(j).getCoordinates();
                var dimension = point2.get(0) - point1.get(0) == 0 ? 1 : 0;

                var increment = (point2.get(dimension) - point1.get(dimension)) / Math.abs((point2.get(dimension) - point1.get(dimension)));
                var outsideIncrement = increment * direction * -1;
                for (var a = point1.get(dimension); a != point2.get(dimension) + increment; a += increment) {
                    // Increments the perimeter by 1 in the outside direction
                    if (dimension == 0) {
                        addValue(a, point1.get(1) + outsideIncrement);
                    } else {
                        addValue(point1.get(0) + outsideIncrement * -1, a);
                    }
                }

                // On the inside of an L bend
                newDirection = getVectorScalarProductFromPoints(new Node(point0), new Node(point1), new Node(point2));
                if (newDirection != direction) {
                    if (dimension == 0) {
                        var y = (increment > 0 ? increment : -increment) * direction;
                        removeValue(point1.get(0) + increment, point1.get(1));
                        removeValue(point1.get(0), point1.get(1) + y);
                    }else {
                        var x = (increment > 0 ? -increment : increment) * direction;
                        removeValue(point1.get(0) + x, point1.get(1));
                        removeValue(point1.get(0), point1.get(1) + increment);
                    }
                }
                point0 = point1;
                point1 = point2;
            }
        }

        public void addValue(long x, long y) {
            if (rowToColValues.containsKey(x)) {
                rowToColValues.get(x).add(y);
            } else {
                var newHashSetCols = new TreeSet<Long>();
                newHashSetCols.add(y);
                rowToColValues.put(x, newHashSetCols);
            }

            if (colToRowValues.containsKey(y)) {
                colToRowValues.get(y).add(x);
            } else {
                var newHashSetRows = new TreeSet<Long>();
                newHashSetRows.add(x);
                colToRowValues.put(y, newHashSetRows);
            }
        }

        public void removeValue(long x, long y) {
            if (rowToColValues.containsKey(x)) {
                rowToColValues.get(x).remove(y);
                if (rowToColValues.get(x).size() == 0) {
                    rowToColValues.remove(x);
                }
            }

            if (rowToColValues.containsKey(y)) {
                colToRowValues.get(y).remove(x);
                if (colToRowValues.get(y).size() == 0) {
                    colToRowValues.remove(y);
                }
            }
        }

        private boolean withinRange(long min, long max, long num) {
            return min <= num && max >= min;
        }

        private boolean collidesWithLine(boolean isXDimension, long index, long min, long max) {
            // Gets a sorted list of indexes
            var dimensionalSet = isXDimension ? rowToColValues : colToRowValues;
            if (!dimensionalSet.containsKey(index)) {
                return false;
            }

            var rowsOrCols = dimensionalSet.get(index);
            for(var idx : rowsOrCols) {
                // Escape early since the iterations are sorted
                if (idx > max) {
                    break;
                }
                if (withinRange(min, max, idx)) {
                    return true;
                }
            }
            return false;
        }

        public boolean collidesWithPerimeter(Rectangle rectangle) {
            var nodeA = rectangle.getA();
            var nodeB = rectangle.getB();
            var minX = Math.min(nodeA.getX(), nodeB.getX());
            var maxX = Math.max(nodeA.getX(), nodeB.getX());
            var minY = Math.min(nodeA.getY(), nodeB.getY());
            var maxY = Math.max(nodeA.getY(), nodeB.getY());

            // Test the four lines that make the perimeter of the rectangle to see if
            // we have any outer perimeter collisions
            var result = collidesWithLine(true, minX, minY, maxY) ||
                    collidesWithLine(true, maxX, minY, maxY) ||
                    collidesWithLine(false, minY, minX, maxX) ||
                    collidesWithLine(false, maxY, minX, maxX);
            return result;
        }
    }

    boolean useExample = false;
    private ArrayList<List<Long>> LoadTextFile() {
        try (InputStream in = RotationLockProblem.class.getResourceAsStream("/data/largestrectangle" + (useExample ? "example" : "") + ".txt")) {
            if (in == null) throw new FileNotFoundException("Resource not found");

            var content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            var lines = content.split("\r\n");
            var fullMap = new ArrayList<List<Long>>();

            for (var line : lines) {
                var coordinates = line.split(",");
//                var reverseCoordinates = new String[] { coordinates[1], coordinates[0] };
                fullMap.add(Arrays.stream(coordinates).map(s -> Long.parseLong(s)).toList());
            }
            return fullMap;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private HashSet<Rectangle> ConstructTree(ArrayList<List<Long>> coordinates) {
        var nodes = coordinates.stream().map(c -> new Node(c)).toList();
        var graph = new HashSet<Rectangle>();
        var hashCodeCheck = new HashSet<Integer>();

        for(var node : nodes) {
            for(var comparisonNode : nodes) {
                if (node == comparisonNode) {
                    continue;
                }
                var tempEdge = new Rectangle(node, comparisonNode);

                // Here we use the hash code to check if the Edge already exists in the map regardless of direction A->B or B->A
                if (!hashCodeCheck.contains(tempEdge.hashCode())) {
                    var edge = new Rectangle(node, comparisonNode);
                    graph.add(edge);
                    hashCodeCheck.add(edge.hashCode());
                }
            }
        }
        return graph;

    }

    // In this solution we want to find the distances between all of the nodes
    // We then sort based on the distances and construct the trees according to the nodes within them
    public Long solvePart1() {
        var data = LoadTextFile();
        var graph = ConstructTree(data);
        var numConnections = useExample ? 10 : 1000;
        HashSet<Node> seenNodes = new HashSet<>();
        List<HashSet<Node>> trees = new ArrayList<>();

        var sortedGraph = graph.stream().sorted(Comparator.comparingDouble(Rectangle::getArea)).toList();
        return (long)sortedGraph.get(sortedGraph.size() - 1).getArea();
    }

    // Convert our positional coordinates to a vector from point1 to point2
    public List<Long> getVectorFromPositions(Node pos1, Node pos2) {
        return List.of(pos2.getX()- pos1.getX(), pos2.getY() - pos1.getY());
    }

    // When given 3 points in order we convert them into 2 vectors
    // By computing the scalar product of the two vectors we can determine which direction the second vector is
    // relative the first.
    // If the value is positive it means the second vector is to the right of the first which indicates a right turn/bend
    // If the value is negative it means the second vector is to the left indicating a left turn/bend
    public Long getVectorScalarProductFromPoints(Node pos1, Node pos2, Node pos3) {
        var v1 = getVectorFromPositions(pos1, pos2);
        var v2 = getVectorFromPositions(pos2, pos3);
        var direction = v1.get(0) * v2.get(1) - v1.get(1) * v2.get(0);
        if (direction == 0) {
            return 0L;
        }
        return direction / Math.abs(direction);
    }

    // This find the direction of the path which we know to be a loop
    // if it is +1 it means the path rotates clockwise otherwise -1 is anticlockwise
    public int getDirectionOfPath(List<Node> data) {
        var point0 = data.get(data.size() - 1);
        var point1 = data.get(0);
        var direction = 0;
        for (var i = 1; i < data.size() + 1; i++) {
            var j = i % data.size();
            var point2 = data.get(j);
            direction += getVectorScalarProductFromPoints(point0, point1, point2);
            point0 = point1;
            point1 = point2;
        }

        // if -1 the path turns to the left if 1 the path turns to the right making a loop
        direction = direction / Math.abs(direction);
        return direction;
    }


    // Answer: 1479665889
    public Long solvePart2() {
        var data = LoadTextFile().stream().map(c -> new Node(c)).toList();
        var direction = getDirectionOfPath(data);
        var outsidePerimeter = new OutsidePerimeter(data, direction);

        var largestArea = 0L;
        var seenPoints =  new HashSet<Long>();
        long start = System.nanoTime();
        for(var point: data) {
            for(var point2: data) {
                var coordinatesHashCode = CoordinatesHashing.hashUnordered(point.getX().intValue(), point.getY().intValue(), point2.getX().intValue(), point2.getY().intValue());
                if (seenPoints.contains(coordinatesHashCode)) {
                    continue;
                }
                var tempRect = new Rectangle(point, point2);
                if (tempRect.getArea() <= largestArea) {
                    continue;
                }

                var hitPerimeter = outsidePerimeter.collidesWithPerimeter(tempRect);
                if (!hitPerimeter) {
                    largestArea = (long)tempRect.getArea();
                }

                seenPoints.add(coordinatesHashCode);
            }


        }
        long end = System.nanoTime();
        long durationNs = end - start;
        double durationMs = (durationNs / 1_000_000.0);
        System.out.println("Duration:\t" + durationMs + "ms");


        return largestArea;
    }


}
