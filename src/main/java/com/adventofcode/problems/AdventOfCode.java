package com.adventofcode.problems;

import java.io.InputStream;

public abstract class AdventOfCode {
    String filename;
    public AdventOfCode(String filename, boolean useExample) {
        var nameAndExtension = filename.split("\\.");
        assert nameAndExtension.length == 2;
        this.filename = "/data/" + nameAndExtension[0] + (useExample ? "example." : ".") + nameAndExtension[1];
    }

    protected InputStream ReadFile() {
        return AdventOfCode.class.getResourceAsStream(filename);
    }
    public abstract Number solvePart1();
    public abstract Number solvePart2();
}
