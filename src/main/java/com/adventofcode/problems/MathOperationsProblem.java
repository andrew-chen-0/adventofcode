package com.adventofcode.problems;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class MathOperationsProblem extends AdventOfCode {

    public MathOperationsProblem(String filename, boolean useExample) {
        super(filename, useExample);
    }

    class OperateNumber {

        ArrayList<Long[]> numbers;
        ArrayList<Character> operations;

        String[] lines;

        OperateNumber(ArrayList<Long[]> numbers, ArrayList<Character> operations, String[] lines) {
            this.numbers = numbers;
            this.operations = operations;
            var maxSize = 0;
            for(var line : lines) {
                var a = line.length();
                if (line.length() > maxSize) {
                    maxSize = line.length();
                }
            }
            for(var i = 0; i < lines.length; i++) {
                if (lines[i].length() < maxSize) {
                    lines[i] = lines[i] + " ".repeat(maxSize - lines[i].length());
                }
            }
            this.lines = lines;
        }

        public int size() {
            return numbers.get(0).length;
        }

        public Long performOperation(int index) throws Exception {
            var num_numbers = numbers.size();
            var operatingNumbers = new Long[num_numbers];
            for(var i = 0; i < num_numbers; i++) {
                operatingNumbers[i] = numbers.get(i)[index];
            }
            switch (operations.get(index)){
                case '*' :
                    return multiply(operatingNumbers);
                case '+':
                    return add(operatingNumbers);
            }
            throw new Exception("Shouldn't hit this code");
        }

        private Long multiply(Long[] arr) {
            return Arrays.stream(arr).reduce(1L, (a, b) -> a * b);
        }

        private Long multiply(ArrayList<Long> arr) {
            return arr.stream().reduce(1L, (a, b) -> a * b);
        }

        private Long add(Long[] arr) {
            return Arrays.stream(arr).reduce(0L, Long::sum);
        }
        private Long add(ArrayList<Long> arr) {
            return arr.stream().reduce(0L, Long::sum);
        }

    }

    boolean useExample = false;
    private OperateNumber LoadTextFile() {
        try (InputStream in = ReadFile()) {
            if (in == null) throw new FileNotFoundException("Resource not found");

            var content = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            var numbers = new ArrayList<Long[]>();
            var operations = new ArrayList<Character>();
            var lines = content.split("\r\n");
            for (var line : lines) {
                if (line.length() == 0) {
                    continue;
                }
                var s = Arrays.stream(line.split("\\s+")).filter(num -> num.length() > 0).toList();

                if (Character.isDigit(s.get(0).charAt(0))) {
                    var intArray = new Long[s.size()];
                    for (var i = 0; i < s.size(); i++) {
                        intArray[i] = Long.parseLong(s.get(i));
                    }
                    numbers.add(intArray);
                }else {
                    for (var str : s) {
                        operations.add(str.charAt(0));
                    }
                }
            }
            return new OperateNumber(numbers, operations, lines);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Long solvePart1() {
        var operateNumber = LoadTextFile();
        var res = 0L;
        for(var i = 0; i < operateNumber.size(); i++) {
            try {
                res += operateNumber.performOperation(i);
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return res;
    }

    public Long solvePart2() {
        var operateNumber = LoadTextFile();
        var numbersSize = operateNumber.lines.length - 1;

        var operatingNumbers = new ArrayList<Long>();

        var res = 0L;
        for(var i = operateNumber.lines[0].length() - 1; i >= 0; i--) {

            var charList = new char[numbersSize];
            for(var rows = 0; rows < numbersSize; rows++) {
                charList[rows] = operateNumber.lines[rows].charAt(i);
            }
            var num = constructNumber(charList);
            if (num != -1) {
                operatingNumbers.add(num);
            }
            switch (operateNumber.lines[numbersSize].charAt(i)) {
                case '*':
                    res += operateNumber.multiply(operatingNumbers);
                    operatingNumbers.clear();
                    break;
                case '+':
                    res += operateNumber.add(operatingNumbers);
                    operatingNumbers.clear();
                    break;
            }
        }
        return res;
    }

    public Long constructNumber(char[] numArr) {
        var res = 0L;
        boolean hasDigit = false;
        for(var ch : numArr) {
            if (Character.isDigit(ch)) {
                hasDigit = true;
                res = res * 10 + (ch - '0');
            }
        }
        if (!hasDigit) {
            return -1L;
        }
        return res;
    }
}
