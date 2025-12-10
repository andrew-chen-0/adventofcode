package main.java.utilities;

import java.util.ArrayList;
import java.util.List;

public class Rectangle {
    Node A;
    Node B;
    Node OptionalNode;

    double area;

    public Rectangle(Node A, Node B) {
        this.A = A;
        this.B = B;
        this.area = (long)(Math.abs(A.getCoordinates().get(0) - B.getCoordinates().get(0)) + 1) * (long)(Math.abs(A.getCoordinates().get(1) - B.getCoordinates().get(1)) + 1);
    }

    public Rectangle(Node A, Node B, double area) {
        this.A = A;
        this.B = B;
        this.area = area;
    }

    public double getArea() {
        return area;
    }

    public Node getA() {
        return A;
    }

    public Node getB() {
        return B;
    }

    @Override
    public int hashCode() {
        return (int)(A.getCoordinates().get(0) * B.getCoordinates().get(0) +
                A.getCoordinates().get(1) * B.getCoordinates().get(1));
    }
    public boolean withinDimension(int dimension, Node node) {
        var min = Math.min(this.A.getCoordinates().get(dimension), this.B.getCoordinates().get(dimension));
        var max = Math.max(this.A.getCoordinates().get(dimension), this.B.getCoordinates().get(dimension));
        var pos = node.getCoordinates().get(dimension);
        return min <= pos && max >= pos;
    }

    public boolean withinRectangle(Node node) {
        return withinDimension(0, node) && withinDimension(1, node);
    }

    public ArrayList<Node> getSquares() {
        var minX = Math.min(this.A.getCoordinates().get(0), this.B.getCoordinates().get(0));
        var maxX = Math.max(this.A.getCoordinates().get(0), this.B.getCoordinates().get(0));
        var minY = Math.min(this.A.getCoordinates().get(1), this.B.getCoordinates().get(1));
        var maxY = Math.max(this.A.getCoordinates().get(1), this.B.getCoordinates().get(1));

        var arr = new ArrayList<Node>();
        for (var i = minX; i <= maxX; i++) {
            for (var j = minY; j <= maxY; j++) {
                arr.add(new Node(List.of(i, j)));
            }
        }
        return arr;
    }

    public ArrayList<Node> getAllCorners() {
        var minX = Math.min(this.A.getCoordinates().get(0), this.B.getCoordinates().get(0));
        var maxX = Math.max(this.A.getCoordinates().get(0), this.B.getCoordinates().get(0));
        var minY = Math.min(this.A.getCoordinates().get(1), this.B.getCoordinates().get(1));
        var maxY = Math.max(this.A.getCoordinates().get(1), this.B.getCoordinates().get(1));

        var arr = new ArrayList<Node>();
        arr.add(new Node(List.of(minX, minY)));
        arr.add(new Node(List.of(minX, maxY)));
        arr.add(new Node(List.of(maxX, minY)));
        arr.add(new Node(List.of(maxX, maxY)));
        return arr;
    }

    public boolean equalsDimension(int dimension, Node node) {
        var pos = node.getCoordinates().get(dimension);
        return this.A.getCoordinates().get(dimension) == pos || this.B.getCoordinates().get(dimension) == pos;
    }
}