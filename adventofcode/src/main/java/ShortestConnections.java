package main.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ShortestConnections {

    class Edge {
        Node A;
        Node B;

        double distance;

        public Edge(Node A, Node B) {
            this.A = A;
            this.B = B;
        }

        public Edge(Node A, Node B, double distance) {
            this.A = A;
            this.B = B;
            this.distance = distance;
        }

        @Override
        public int hashCode() {
            return A.coordinates.get(0) * B.coordinates.get(0) +
                    A.coordinates.get(1) * B.coordinates.get(1) +
                    A.coordinates.get(2) * B.coordinates.get(2);
        }
    }

    class Node {
        List<Integer> coordinates;

        public Node(List<Integer> coordinates) {
            this.coordinates = coordinates;
        }


        public Edge createEdge(Node node) {
            var distance = Math.sqrt(Math.pow(coordinates.get(0) - node.coordinates.get(0), 2) +
                    Math.pow(coordinates.get(1) - node.coordinates.get(1), 2) +
                    Math.pow(coordinates.get(2) - node.coordinates.get(2), 2));

            return new Edge(this, node, distance);
        }
    }

    boolean useExample = false;
    private ArrayList<List<Integer>> LoadTextFile() {
        try (InputStream in = RotationLockProblem.class.getResourceAsStream("/data/shortestconnection" + (useExample ? "example" : "") + ".txt")) {
            if (in == null) throw new FileNotFoundException("Resource not found");

            var content = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            var rangeList = new ArrayList<RangesProblem.Range>();
            var inputsToCheck = new ArrayList<Long>();
            var lines = content.split("\r\n");
            var fullMap = new ArrayList<List<Integer>>();

            for (var line : lines) {
                var coordinates = line.split(",");
                fullMap.add(Arrays.stream(coordinates).map(s -> Integer.parseInt(s)).toList());
            }
            return fullMap;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private HashSet<Edge> ConstructTree(ArrayList<List<Integer>> coordinates) {
        var nodes = coordinates.stream().map(c -> new Node(c)).toList();
        var graph = new HashSet<Edge>();
        var hashCodeCheck = new HashSet<Integer>();

        for(var node : nodes) {
            for(var comparisonNode : nodes) {
                if (node == comparisonNode) {
                    continue;
                }
                var tempEdge = new Edge(node, comparisonNode);

                // Here we use the hash code to check if the Edge already exists in the map regardless of direction A->B or B->A
                if (!hashCodeCheck.contains(tempEdge.hashCode())) {
                    var edge = node.createEdge(comparisonNode);
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

        var sortedGraph = graph.stream().sorted((edge1, edge2) -> edge1.distance < edge2.distance ? -1 : 1).toList();
        for(var i = 0; i < sortedGraph.size() && i < numConnections; i++) {
            var edge = sortedGraph.get(i);
            var nodeA = edge.A;
            var nodeB = edge.B;

            // There are three cases we want to address
            // 1. The nodes are new and have not been added to the map
            // 2. The nodes have both been seen
            //    a. We need to check if they exist in the same tree if they do it causes a loop
            //    b. If they are not in the same tree then we need to merge the two seperate trees together
            // 3. One of the nodes is seen and the other is in a tree. We just append the detached node to the tree
            if (!seenNodes.contains(nodeA) && !seenNodes.contains(nodeB)) {
                var newTree = new HashSet<Node>();
                newTree.add(nodeA);
                newTree.add(nodeB);
                trees.add(newTree);
            } else if (seenNodes.contains(nodeA) && seenNodes.contains(nodeB)) {
                var treesContainingNodes = new ArrayList<HashSet<Node>>();
                for(var tree : trees) {
                    if (tree.contains(nodeA) && tree.contains(nodeB)) {
                        break;
                    } else if (tree.contains(nodeA) || tree.contains(nodeB)) {
                        treesContainingNodes.add(tree);
                    }

                    if (treesContainingNodes.size() == 2) {
                        break;
                    }
                }
                // Merge the trees
                if (treesContainingNodes.size() == 2) {
                    trees.remove(treesContainingNodes.get(1));
                    treesContainingNodes.get(0).addAll(treesContainingNodes.get(1));
                }
            } else {
                for(var tree : trees) {
                    if (tree.contains(nodeA) || tree.contains(nodeB)) {
                        tree.add(nodeA);
                        tree.add(nodeB);
                        break;
                    }
                }
            }

            seenNodes.add(nodeA);
            seenNodes.add(nodeB);
        }

        trees.sort((set1, set2) -> set1.size() == set2.size() ? 0 : set1.size() > set2.size() ? -1 : 1);


        return (long)trees.get(0).size() * (long)trees.get(1).size() * (long)trees.get(2).size();
    }

    // Part 2 is the same as Part 1 we however instead of just 1000 shortest connections
    // we allow the tree to run to completion and use the last edge to be added to the tree to do some math with the nodes.
    public Long solvePart2() {
        var data = LoadTextFile();
        var graph = ConstructTree(data);
        HashSet<Node> seenNodes = new HashSet<>();
        List<HashSet<Node>> trees = new ArrayList<>();



        var sortedGraph = graph.stream().sorted((edge1, edge2) -> edge1.distance < edge2.distance ? -1 : 1).toList();
        var lastEdge = sortedGraph.get(0);

        for(var i = 0; i < sortedGraph.size(); i++) {
            var edge = sortedGraph.get(i);
            var nodeA = edge.A;
            var nodeB = edge.B;

            // If the nodes are not found in the tree we start making a new connection
            if (!seenNodes.contains(nodeA) && !seenNodes.contains(nodeB)) {
                var newTree = new HashSet<Node>();
                newTree.add(nodeA);
                newTree.add(nodeB);
                trees.add(newTree);
            } else if (seenNodes.contains(nodeA) && seenNodes.contains(nodeB)) {
                var treesContainingNodes = new ArrayList<HashSet<Node>>();
                for(var tree : trees) {
                    if (tree.contains(nodeA) && tree.contains(nodeB)) {
                        break;
                    } else if (tree.contains(nodeA) || tree.contains(nodeB)) {
                        treesContainingNodes.add(tree);
                    }

                    if (treesContainingNodes.size() == 2) {
                        break;
                    }
                }
                // Merge the trees
                if (treesContainingNodes.size() == 2) {
                    trees.remove(treesContainingNodes.get(1));
                    treesContainingNodes.get(0).addAll(treesContainingNodes.get(1));
                    lastEdge = edge;
                }
            } else {
                for(var tree : trees) {
                    if (tree.contains(nodeA) || tree.contains(nodeB)) {
                        tree.add(nodeA);
                        tree.add(nodeB);
                        lastEdge = edge;
                        break;
                    }
                }
            }

            seenNodes.add(nodeA);
            seenNodes.add(nodeB);
        }

        return (long)((long)lastEdge.A.coordinates.get(0) * (long)lastEdge.B.coordinates.get(0));
    }


}
