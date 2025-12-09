package main.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class LargestRectangle {

    class Rectangle {
        Node A;
        Node B;
        Node OptionalNode;

        double area;

        public Rectangle(Node A, Node B) {
            this.A = A;
            this.B = B;
            this.area = (long)(Math.abs(A.coordinates.get(0) - B.coordinates.get(0)) + 1) * (long)(Math.abs(A.coordinates.get(1) - B.coordinates.get(1)) + 1);
        }

        public Rectangle(Node A, Node B, double area) {
            this.A = A;
            this.B = B;
            this.area = area;
        }

        @Override
        public int hashCode() {
            return (int)(A.coordinates.get(0) * B.coordinates.get(0) +
                    A.coordinates.get(1) * B.coordinates.get(1));
        }
        public boolean withinDimension(int dimension, Node node) {
            var min = Math.min(this.A.coordinates.get(dimension), this.B.coordinates.get(dimension));
            var max = Math.max(this.A.coordinates.get(dimension), this.B.coordinates.get(dimension));
            var pos = node.coordinates.get(dimension);
            return min <= pos && max >= pos;
        }

        public boolean withinRectangle(Node node) {
            return withinDimension(0, node) && withinDimension(1, node);
        }

        public ArrayList<Node> getSquares() {
            var minX = Math.min(this.A.coordinates.get(0), this.B.coordinates.get(0));
            var maxX = Math.max(this.A.coordinates.get(0), this.B.coordinates.get(0));
            var minY = Math.min(this.A.coordinates.get(1), this.B.coordinates.get(1));
            var maxY = Math.max(this.A.coordinates.get(1), this.B.coordinates.get(1));

            var arr = new ArrayList<Node>();
            for (var i = minX; i <= maxX; i++) {
                for (var j = minY; j <= maxY; j++) {
                    arr.add(new Node(List.of(i, j)));
                }
            }
            return arr;
        }

        public ArrayList<Node> getAllCorners() {
            var minX = Math.min(this.A.coordinates.get(0), this.B.coordinates.get(0));
            var maxX = Math.max(this.A.coordinates.get(0), this.B.coordinates.get(0));
            var minY = Math.min(this.A.coordinates.get(1), this.B.coordinates.get(1));
            var maxY = Math.max(this.A.coordinates.get(1), this.B.coordinates.get(1));

            var arr = new ArrayList<Node>();
            arr.add(new Node(List.of(minX, minY)));
            arr.add(new Node(List.of(minX, maxY)));
            arr.add(new Node(List.of(maxX, minY)));
            arr.add(new Node(List.of(maxX, maxY)));
            return arr;
        }

        public boolean equalsDimension(int dimension, Node node) {
            var pos = node.coordinates.get(dimension);
            return this.A.coordinates.get(dimension) == pos || this.B.coordinates.get(dimension) == pos;
        }

        public boolean trySetOptionalNode(Node optionalNode) {
            if (this.withinDimension(0, optionalNode) && this.withinDimension(1, optionalNode)) {
                this.OptionalNode = optionalNode;
                return true;
            }
            return false;
        }



        public Rectangle tryExpandRectangle(Node newNode) {
            if ((this.A.coordinates.get(0) == newNode.coordinates.get(0) && withinDimension(1, newNode)) ||
                    (this.A.coordinates.get(1) == newNode.coordinates.get(1) && withinDimension(0, newNode))) {
                var tempRect = this.B.createRectangle(newNode);
                if (tempRect.area > area) {
                    this.A = newNode;
                    area = tempRect.area;
                    return tempRect;
                }
            } else if ((this.B.coordinates.get(0) == newNode.coordinates.get(0) && withinDimension(1, newNode)) ||
                    (this.B.coordinates.get(1) == newNode.coordinates.get(1) && withinDimension(0, newNode))) {
                var tempRect = this.A.createRectangle(newNode);
                if (tempRect.area > area) {
                    this.B = newNode;
                    area = tempRect.area;
                    return tempRect;
                }
            }
            if (OptionalNode != null) {
                var tempRect = this.OptionalNode.createRectangle(newNode);
                if (tempRect.area > area && (equalsDimension(0, newNode) || equalsDimension(1, newNode))) {
                    this.A = OptionalNode;
                    this.B = newNode;
                    return tempRect;
                }
            }

            return this;
        }

        public Rectangle tryExpandOptionalNode(Node newNode) {
            if (OptionalNode != null) {
                var tempRect = this.OptionalNode.createRectangle(newNode);
                if (tempRect.area > area && (equalsDimension(0, newNode) || equalsDimension(1, newNode))) {
                    return tempRect;
                }
            }
            return null;
        }

        public List<Rectangle> getPossibleRectangles(Node newNode) {
            var result = new ArrayList<Rectangle>();

            result.add(this.A.createRectangle(newNode));
            result.add(this.B.createRectangle(newNode));
            if (OptionalNode != null) {
                result.add(this.OptionalNode.createRectangle(newNode));
            }
            return result;
        }
    }

    class Node {
        List<Long> coordinates;

        public Node(List<Long> coordinates) {
            this.coordinates = coordinates;
        }


        public Rectangle createRectangle(Node node) {
            var distance = (long)(Math.abs(coordinates.get(0) - node.coordinates.get(0)) + 1) * (long)(Math.abs(coordinates.get(1) - node.coordinates.get(1)) + 1);
            return new Rectangle(this, node, distance);
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
                    var edge = node.createRectangle(comparisonNode);
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

        var sortedGraph = graph.stream().sorted((rectangle1, rectangle2) -> rectangle1.area < rectangle2.area ? -1 : 1).toList();
        return (long)sortedGraph.get(sortedGraph.size() - 1).area;
    }

    public List<Long> subtractPositions(List<Long> pos1, List<Long> pos2) {
        return List.of(pos2.get(0) - pos1.get(0), pos2.get(1) - pos1.get(1));
    }

    public Long direction(List<Long> pos1, List<Long> pos2, List<Long> pos3) {
        var v1 = subtractPositions(pos1, pos2);
        var v2 = subtractPositions(pos2, pos3);
        var direction = v1.get(0) * v2.get(1) - v1.get(1) * v2.get(0);
        if (direction == 0) {
            return 0L;
        }
        return direction / Math.abs(direction);
    }


    public String coordinatesToHashCode(Long x, Long y) {
        return (x + "," + y);
    }

    // Strong 64-bit mixing function (MurmurHash3 finalizer)
    private static long mix64(long z) {
        z ^= (z >>> 33);
        z *= 0xff51afd7ed558ccdL;
        z ^= (z >>> 33);
        z *= 0xc4ceb9fe1a85ec53L;
        z ^= (z >>> 33);
        return z;
    }

    // Hash a single int vec2
    public static long hashVec2(int x, int y) {
        long h = 0x9E3779B97F4A7C15L; // golden ratio seed
        h ^= mix64(x);
        h = mix64(h);
        h ^= mix64(y);
        h = mix64(h);
        return h;
    }

    // Order-independent hash of two int vec2
    public static long hashUnordered(int ax, int ay, int bx, int by) {
        long ha = hashVec2(ax, ay);
        long hb = hashVec2(bx, by);

        long lo = Math.min(ha, hb);
        long hi = Math.max(ha, hb);

        long h = lo;
        h ^= hi + 0x9E3779B97F4A7C15L + (h << 6) + (h >>> 2); // boost::hash_combine style
        return mix64(h);
    }

    public void test() {
        var point1 = List.of(0L,0L);
        var increment = 1;
        var direction = 1;
        var outsideIncrement = increment * direction * -1;
        var dimension = 1;
        var x = dimension == 0 ? increment : outsideIncrement;
        var y = dimension == 0 ? outsideIncrement : increment;

        var straight = coordinatesToHashCode(point1.get(0) + x, point1.get(1));
        var right = coordinatesToHashCode(point1.get(0), point1.get(1) + y);
    }

    public Long solvePart2() {
        var data = LoadTextFile();
        var perimeter = new HashSet<String>();
        var direction = 0L;

        var point0 = data.get(data.size() - 1);
        var point1 = data.get(0);
        for (var i = 1; i < data.size() + 1; i++) {
            var j = i % data.size();
            var point2 = data.get(j);
            direction += direction(point0, point1, point2);
            point0 = point1;
            point1 = point2;
        }

        direction = direction / Math.abs(direction);

        var newDirection = 0L;
        point0 = data.get(data.size() - 1);
        point1 = data.get(0);
        for (var i = 1; i < data.size() + 1; i++) {
            var j = i % data.size();
            var point2 = data.get(j);
            var dimension = point2.get(0) - point1.get(0) == 0 ? 1 : 0;

            var increment = (point2.get(dimension) - point1.get(dimension)) / Math.abs((point2.get(dimension) - point1.get(dimension)));
            var outsideIncrement = increment * direction * -1;
            for (var a = point1.get(dimension); a != point2.get(dimension) + increment; a += increment) {
                // Increments the perimeter by 1 in the outside direction
                if (dimension == 0) {
                    perimeter.add(coordinatesToHashCode(a, point1.get(1) + outsideIncrement));
                } else {
                    perimeter.add(coordinatesToHashCode(point1.get(0) + outsideIncrement * -1, a));
                }
            }

            // On the inside of an L bend
            newDirection = direction(point0, point1, point2);
            if (newDirection != direction) {
                if (dimension == 0) {
                    var y = (increment > 0 ? increment : -increment) * direction;
                    var xDir = coordinatesToHashCode(point1.get(0) + increment, point1.get(1));
                    var yDir = coordinatesToHashCode(point1.get(0), point1.get(1) + y);
                    perimeter.remove(xDir);
                    perimeter.remove(yDir);
                }else {
                    var x = (increment > 0 ? -increment : increment) * direction;
                    var xDir = coordinatesToHashCode(point1.get(0) + x, point1.get(1));
                    var yDir = coordinatesToHashCode(point1.get(0), point1.get(1) + increment);
                    perimeter.remove(xDir);
                    perimeter.remove(yDir);
                }
            }
            point0 = point1;
            point1 = point2;
        }

        var largestArea = 0L;
        var seenPoints =  new HashSet<Long>();
        var count = 0.0;
        double durationSeconds = 0;
        for(var point: data) {
            count++;
            if (count % 5 == 0) {
                System.out.println("Completion:\t" + (count / data.size() * 100) + "%");
                System.out.println("Time to completion:\t" + (durationSeconds * (data.size() - count)) + "s");
            }
            long start = System.nanoTime();
            for(var point2: data) {
                if (seenPoints.contains(hashUnordered(point.get(0).intValue(), point.get(1).intValue(), point2.get(0).intValue(), point2.get(1).intValue()))) {
                    continue;
                }
                var tempRect = new Rectangle(new Node(point), new Node(point2));
                if (tempRect.area <= largestArea) {
                    continue;
                }
                var directionX = point2.get(0) - point.get(0) == 0 ? 1 : (point2.get(0) - point.get(0)) / Math.abs((point2.get(0) - point.get(0)));
                var directionY = point2.get(1) - point.get(1) == 0 ? 1 : (point2.get(1) - point.get(1)) / Math.abs((point2.get(1) - point.get(1)));
                var hitPerimeter = false;
                for (var x = point.get(0); x != point2.get(0) + directionX && !hitPerimeter; x += directionX) {
                    hitPerimeter = hitPerimeter || perimeter.contains(coordinatesToHashCode(x, point.get(1)));
                    hitPerimeter = hitPerimeter || perimeter.contains(coordinatesToHashCode(x, point2.get(1)));
                }

                for (var y = point.get(1); y != point2.get(1) + directionY && !hitPerimeter; y += directionY) {
                    hitPerimeter = hitPerimeter || perimeter.contains(coordinatesToHashCode(point.get(0), y));
                    hitPerimeter = hitPerimeter || perimeter.contains(coordinatesToHashCode(point2.get(0), y));
                }
                if (!hitPerimeter) {
                    largestArea = (long)tempRect.area;
                }

                seenPoints.add(hashUnordered(point.get(0).intValue(), point.get(1).intValue(), point2.get(0).intValue(), point2.get(1).intValue()));
            }
            long end = System.nanoTime();
            long durationNs = end - start;
            durationSeconds = (durationNs / 1_000_000.0 / 1000);

        }



        return largestArea;
    }


}
