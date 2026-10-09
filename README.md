# Advent of Code 2025 🎄

Solutions for **[Advent of Code 2025](https://adventofcode.com/2025)**, written in **Java**. 
AoC is an Advent-calendar-style set of programming puzzles released daily in December, each day with two parts that get progressively trickier.

This repo is me working through the 2025 event for fun and practice. Expect a mix of clean solutions, experiments, and the occasional refactor after the fact.

---

## Event details

- **Puzzles:** 12 days, two parts per day  
- **Release time:** 3:30pm 🕞  ACST
- **Language:** Java

---


## Progress

| Day | Part 1 | Part 2 |
|-----|--------|--------|
| 01  | ⭐      | ⭐⭐     |
| 02  | ⭐      | ⭐⭐     |
| 03  | ⭐      | ⭐⭐     |
| 04  | ⭐      | ⭐⭐     |
| 05  | ⭐      | ⭐⭐     |
| 06  | ⭐      | ⭐⭐     |
| 07  | ⭐      | ⭐⭐     |
| 08  | ⭐      | ⭐⭐     |
| 09  | ⭐      | ⭐⭐     |
| 10  | ⭐      | ⭐⭐     |
| 11  | ⭐      | ⭐⭐     |
| 12  | ⭐      | ⭐⭐     |s

---


## Running the Solutions

The program accepts up to three command-line arguments:

1. **`day`** (optional) — which Advent of Code day to run.

    * If omitted, it defaults to **day 9**.

2. **`filename`** (optional) — name or path of the input file.

    * **Input files must be placed under `resources/data/`.**
    * You can pass either:

        * just the filename (e.g., `input9.txt`), or
        * a relative path from the project root (e.g., `resources/data/input9.txt`).
    * If omitted, the program will use the default input configured in `AOC_DAY_TO_PROBLEM(...)`.

3. **`useExample`** (optional) — whether to run with example input.

    * If omitted, it defaults to `false`.

### Compile

```bash
javac -d out src/**/*.java
```

### Run

**Run default (day 9, default input):**

```bash
java -cp out Main
```

**Run a specific day:**

```bash
java -cp out Main 5
```

**Run a specific day with a custom input file**
(file must be in `resources/data/`):

```bash
java -cp out Main 5 input5.txt
```

**Run a specific day with custom input + example mode:**

```bash
java -cp out Main 5 input5.txt true
```

### Input Folder Layout

Place your inputs like this:

```
resources/
  data/
    input1.txt
    input2.txt
    ...
    input9.txt
```

### Output

The program prints timed results for both parts:

```
Part1:  <answer>
Part2:  <answer>
```

---

## Credits

Advent of Code is created and maintained by **Eric Wastl**, with support from sponsors, beta testers, and the community.  
If you enjoy it, consider supporting the project via AoC++.

---

## License

MIT, unless a specific day notes otherwise.  
Feel free to borrow ideas, but please don’t redistribute AoC puzzle text or inputs.

Happy puzzling! 🎅







