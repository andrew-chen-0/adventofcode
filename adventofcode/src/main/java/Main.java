package main.java;

public class Main {
    public static void main(String[] args) throws Exception {
        var problem = new RepeatingNumbersProblem();
        problem.findTotalOfRepeatingNumbersFast(95, 115);
        problem.findTotalOfRepeatingNumbersInFile();
    }
}