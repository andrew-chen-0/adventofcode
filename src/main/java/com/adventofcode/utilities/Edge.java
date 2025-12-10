package com.adventofcode.utilities;

public class Edge {
    Node A;
    Node B;

    double distance;

    public Edge(Node A, Node B) {
        this.A = A;
        this.B = B;
        this.distance = Math.sqrt(Math.pow(A.getX() - B.getX(), 2) +
                Math.pow(A.getY() - B.getY(), 2) +
                Math.pow(A.getZ() - B.getZ(), 2));
    }

    public Node getA() {
        return A;
    }

    public Node getB() {
        return B;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int hashCode() {
        return (int)(A.getX() * B.getX() +
                A.getY() * B.getY() +
                A.getZ() * B.getZ());
    }
}