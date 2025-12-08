package main.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BeamSplit {
    class BeamTimelines {
        long count = 1L;
        int rayPosition;

        BeamTimelines(long count, int rayPosition) {
            this.count = count;
            this.rayPosition = rayPosition;
        }

        public BeamTimelines clone(int newRayPosition) {
            return new BeamTimelines(count, newRayPosition);
        }
    }
    boolean useExample = false;
    private ArrayList<ArrayList<Integer>> LoadTextFile() {
        try (InputStream in = RotationLockProblem.class.getResourceAsStream("/data/beamsplit" + (useExample ? "example" : "") + ".txt")) {
            if (in == null) throw new FileNotFoundException("Resource not found");

            var content = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            var rangeList = new ArrayList<RangesProblem.Range>();
            var inputsToCheck = new ArrayList<Long>();
            var lines = content.split("\r\n");
            var fullMap = new ArrayList<ArrayList<Integer>>();
            for (var line : lines) {
                var splitters = new ArrayList<Integer>();
                for(var i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == 'S' || line.charAt(i) == '^') {
                        splitters.add(i);
                    }
                }
                fullMap.add(splitters);
            }
            return fullMap;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    int beamSplits = 0;
    public ArrayList<Integer> hitSplitter(ArrayList<Integer> rayPositions, ArrayList<Integer> splitterPositions) {
        var newRayPositions = new ArrayList<Integer>(rayPositions);
        for(var splitter : splitterPositions) {
            if (rayPositions.contains(splitter)) {
                beamSplits++;
                newRayPositions.add(splitter - 1);
                newRayPositions.add(splitter + 1);
                newRayPositions.remove(splitter);
            }
        }
        return newRayPositions;
    }

    public ArrayList<BeamTimelines> hitSplitterMultipleRays(ArrayList<BeamTimelines> rayPositions, ArrayList<Integer> splitterPositions) {
        var newRayPositions = new ArrayList<BeamTimelines>();
        for(var ray : rayPositions) {
            if (splitterPositions.contains(ray.rayPosition)) {
                newRayPositions.add(ray.clone(ray.rayPosition - 1));
                newRayPositions.add(ray.clone(ray.rayPosition + 1));
                continue;
            }
            newRayPositions.add(ray);
        }
        return newRayPositions;
    }

    public ArrayList<BeamTimelines> flattenDuplicates(ArrayList<BeamTimelines> beamTimelines) {
        var newRayPositions = new ArrayList<BeamTimelines>();
        var rayToBeam = new HashMap<Integer, BeamTimelines>();
        for(var beam : beamTimelines) {
            if (rayToBeam.containsKey(beam.rayPosition)) {
                rayToBeam.get(beam.rayPosition).count += beam.count;
            } else {
                newRayPositions.add(beam);
                rayToBeam.put(beam.rayPosition, beam);
            }
        }
        return newRayPositions;
    }

    // Answer is 1658
    public int solvePart1() {
        var map = LoadTextFile();
        var rayPositions = new ArrayList<Integer>();

        rayPositions.add(map.get(0).get(0)); // find the position of the S
        for(var i = 1; i < map.size(); i++) {
            rayPositions = hitSplitter(rayPositions, map.get(i));
        }
        return beamSplits;
    }

    // Answer is 53916299384254
    public long solvePart2() {
        var map = LoadTextFile();
        var rayPositions = new ArrayList<BeamTimelines>();

        rayPositions.add(new BeamTimelines(1, map.get(0).get(0))); // find the position of the S
        for(var i = 1; i < map.size(); i++) {
            rayPositions = flattenDuplicates(rayPositions);
            rayPositions = hitSplitterMultipleRays(rayPositions, map.get(i));
        }

        return rayPositions.stream().map(s -> s.count).reduce(0L, Long::sum);
    }





}
