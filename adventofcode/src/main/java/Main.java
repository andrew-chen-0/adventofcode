package main.java;

public class Main {
    public static void main(String[] args) throws Exception {
        var problem = new RepeatingNumbersProblem();
//        problem.findTotalOfOddDigitRepeatingNumbers(998, 1010);
        problem.findTotalOfOddDigitRepeatingNumbersSlow(2, 17);
        problem.findTotalOfRepeatingNumbersInFile();
    }
}