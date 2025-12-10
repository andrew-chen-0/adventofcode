package main.java.utilities;

import java.util.List;

public class Node {
    List<Long> coordinates;

    public Node(List<Long> coordinates) {
        this.coordinates = coordinates;
    }

    public Long getX() {
        return this.coordinates.get(0);
    }

    public Long getY() {
        return this.coordinates.get(1);
    }

    public Long getZ() {
        return this.coordinates.get(2);
    }

    public List<Long> getCoordinates() {
        return this.coordinates;
    }
}
