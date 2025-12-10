package main.java;

import main.java.utilities.Edge;
import main.java.utilities.Node;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ShortestConnections {

    boolean useExample = false;
    private ArrayList<List<Long>> LoadTextFile() {
        try (InputStream in = RotationLockProblem.class.getResourceAsStream("/data/shortestconnection" + (useExample ? "example" : "") + ".txt")) {
            if (in == null) throw new FileNotFoundException("Resource not found");

            var content = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            var rangeList = new ArrayList<RangesProblem.Range>();
            var inputsToCheck = new ArrayList<Long>();
            var lines = content.split("\r\n");
            var fullMap = new ArrayList<List<Long>>();

            for (var line : lines) {
                var coordinates = line.split(",");
                fullMap.add(Arrays.stream(coordinates).map(Long::parseLong).toList());
            }
            return fullMap;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private HashSet<Edge> ConstructTree(ArrayList<List<Long>> coordinates) {
        var nodes = coordinates.stream().map(Node::new).toList();
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
                    var edge = new Edge(node, comparisonNode);
                    graph.add(edge);
                    hashCodeCheck.add(edge.hashCode());
                }
            }
        }
        return graph;

    }

    // In this solution we want to find the distances between all of the nodes
    // We then sort based on the distances and construct the trees according to the nodes within them
    // Answer: 123234
    public Long solvePart1() {
        var data = LoadTextFile();
        var graph = ConstructTree(data);
        var numConnections = useExample ? 10 : 1000;
        HashSet<Node> seenNodes = new HashSet<>();
        List<HashSet<Node>> trees = new ArrayList<>();

        var sortedGraph = graph.stream().sorted(Comparator.comparingDouble(Edge::getDistance)).toList();
        for(var i = 0; i < sortedGraph.size() && i < numConnections; i++) {
            var edge = sortedGraph.get(i);
            var nodeA = edge.getA();
            var nodeB = edge.getB();

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

        trees.sort((set1, set2) -> Integer.compare(set2.size(), set1.size()));


        return (long)trees.get(0).size() * (long)trees.get(1).size() * (long)trees.get(2).size();
    }

    // Part 2 is the same as Part 1 we however instead of just 1000 shortest connections
    // we allow the tree to run to completion and use the last edge to be added to the tree to do some math with the nodes.
    // Answer: 9259958565
    public Long solvePart2() {
        var data = LoadTextFile();
        var graph = ConstructTree(data);
        HashSet<Node> seenNodes = new HashSet<>();
        List<HashSet<Node>> trees = new ArrayList<>();



        var sortedGraph = graph.stream().sorted(Comparator.comparingDouble(Edge::getDistance)).toList();
        var lastEdge = sortedGraph.get(0);

        for(var i = 0; i < sortedGraph.size(); i++) {
            var edge = sortedGraph.get(i);
            var nodeA = edge.getA();
            var nodeB = edge.getB();

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

        return (long)((long)lastEdge.getA().getX() * (long)lastEdge.getB().getX());
    }


}
