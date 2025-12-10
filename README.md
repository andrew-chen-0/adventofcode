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

| Day | Part 1 | Part 2 | Part 1 Answer | Part 2 Answer   |
|-----|--------|--------|---------------|-----------------|
| 01  | ⭐      | ⭐⭐     | 1411          | 6634            |
| 02  | ⭐      | ⭐⭐     | 24747430309   | 30962646823     |
| 03  | ⭐      | ⭐⭐     | 17405         | 171990312704598 |
| 04  | ⭐      | ⭐⭐     | 1486          | 9024            |
| 05  | ⭐      | ⭐⭐     | 770           | 357674099117260 |
| 06  | ⭐      | ⭐⭐     | 6100348226985 | 12377473011151  |
| 07  | ⭐      | ⭐⭐     | 1658          | 53916299384254  |
| 08  | ⭐      | ⭐⭐     | 123234        | 9259958565      |
| 09  | ⭐      | ⭐⭐     | 4749672288    | 1479665889      |
| 10  |        |        |
| 11  |        |        |
| 12  |        |        |

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

**Note:** in your snippet, `useExample` is parsed from `args[1]` instead of `args[2]`. If that’s not deliberate, change:

```java
var useExample = args.length > 2 ? Boolean.parseBoolean(args[1]) : false;
```

to:

```java
var useExample = args.length > 2 ? Boolean.parseBoolean(args[2]) : false;
```

---

If your actual main class name isn’t `Main`, tell me what it is and I’ll swap it in.


---

## Credits

Advent of Code is created and maintained by **Eric Wastl**, with support from sponsors, beta testers, and the community.  
If you enjoy it, consider supporting the project via AoC++.

---

## License

MIT, unless a specific day notes otherwise.  
Feel free to borrow ideas, but please don’t redistribute AoC puzzle text or inputs.

Happy puzzling! 🎅







