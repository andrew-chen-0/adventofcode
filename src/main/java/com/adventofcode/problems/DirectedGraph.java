package com.adventofcode.problems;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;


// TODO invert the tree and keep track of the number of possibilities summing the up backwards
// If we have already seen a node we save the value of the path for future reference dynamic programming
public class DirectedGraph extends AdventOfCode {
    class Graph {
        HashMap<String, GraphNode> allNodes = new HashMap<>();
        HashMap<String, Long> nodeToPath = new HashMap<>();

        public void addNode(GraphNode node) {
            allNodes.put(node.name, node);
        }

        public void buildGraph() {
            for (var node: allNodes.values()) {
                node.setOutputNodes(node.outputs.stream().map(s -> allNodes.get(s)).toList());
            }
        }

        public long findPaths(String startName, String endName) {
            var startNode = allNodes.get(startName);
            var result = iteratePaths(startNode, endName);
            nodeToPath.clear();
            return result;
        }

        private long iteratePaths(GraphNode currentNode, String endName) {
            if (Objects.equals(currentNode.name, endName)) {
                return 1L;
            }

            if (nodeToPath.containsKey(currentNode.name)) {
                return nodeToPath.get(currentNode.name);
            }

            var pathCount = currentNode.outputNodes.stream().map(node -> iteratePaths(node, endName)).reduce(0L, Long::sum);
            nodeToPath.put(currentNode.name, pathCount);
            return pathCount;
        }
    }

    class GraphNode {

        String name;

        List<String> outputs;
        List<GraphNode> outputNodes;

        GraphNode(String name, List<String> outputs) {
            this.name = name;
            this.outputs = outputs;
        }

        public void setOutputNodes(List<GraphNode> node) {
            this.outputNodes = node;
        }



    }

    public DirectedGraph(String filename, boolean useExample) {
        super(filename, useExample);
    }

    private Graph LoadTextFile() {
        try (InputStream in = ReadFile()) {
            if (in == null) throw new FileNotFoundException("Resource not found");

            var content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            var graph = new Graph();

            Arrays.stream(content.split("\r\n")).forEach(s -> {
                var str = s.split(": ");
                var outputs = Arrays.stream(str[1].split(" ")).toList();
                graph.addNode(new GraphNode(str[0], outputs));
            });
            graph.addNode(new GraphNode("out", new ArrayList<>()));
            graph.buildGraph();
            return graph;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Number solvePart1() {
        var graph = LoadTextFile();
        return graph.findPaths("you", "out");
    }

    @Override
    public Number solvePart2() {
        var graph = LoadTextFile();
        var toDAC = graph.findPaths("svr", "dac");
        var toFFT = graph.findPaths("svr", "fft");
        var DACtoFFT = graph.findPaths("dac", "fft");
        var FFTtoDAC = graph.findPaths("fft", "dac");
        var DACtoOut = graph.findPaths("dac", "out");
        var FFTtoOut = graph.findPaths("fft", "out");
        var dacFirst = toDAC * DACtoFFT * FFTtoOut;
        var fftFirst = toFFT * FFTtoDAC * DACtoOut;
        return dacFirst + fftFirst;
    }
}
