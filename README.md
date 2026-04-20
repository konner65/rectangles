# Rectangles

A command-line tool that analyzes pairs of axis-aligned rectangles and
describes three relationships:

1. **Intersection** — the points where the rectangle boundaries meet.
2. **Containment** — whether one rectangle wholly contains the other.
3. **Adjacency** — whether the rectangles share a side, classified as
  *proper*, *sub-line*, or *partial*.

It also draws the pair as an ASCII picture so you can *see* the relationship
at a glance. The project is a Spring Boot / Spring Shell application written
in Java 17.

---

## Table of contents

- [Quick start](#quick-start)
- [Demo](#demo)
- [Analyzing your own rectangles](#analyzing-your-own-rectangles)
  - [Filter which analyses you want](#filter-which-analyses-you-want)
  - [Error handling](#error-handling)
- [CLI reference](#cli-reference)
  - [`analyze` — analyse a pair of rectangles](#analyze--analyse-a-pair-of-rectangles)
  - [`demo` — run pre-built scenarios](#demo--run-pre-built-scenarios)
  - [`help` — list commands and show per-command help](#help--list-commands-and-show-per-command-help)
- [Build & run](#build--run)
  - [Requirements](#requirements)
  - [Dependencies](#dependencies)
  - [Build](#build)
  - [Run](#run)
- [Definitions](#definitions)
- [Tests](#tests)
  - [Coverage report](#coverage-report)

---

## Quick start

**1. Build the self-contained jar**

```bash
./mvnw package
```

**2. Take the built-in tour — runs every scenario**

```bash
java -jar target/rectangles.jar demo
```

**3. Or analyze your own pair of rectangles**

```bash
java -jar target/rectangles.jar analyze --rectangles 0,0,10,10,5,5,15,15
```

If you run the jar with **no arguments**, it defaults to `demo`.

> Running `./mvnw package` will also run the full test suite. To skip tests,
> use `./mvnw -DskipTests package`. See [Build & run](#build--run) for more
> detail.

---

## Demo

The `demo` command walks through seven scenarios. Each scenario prints the analyzed rectangles, the intersection/containment/adjacency, and an ASCII picture of the rectangles.

Run the whole demo at once with `java -jar target/rectangles.jar demo`, or
pick a single scenario with `--name` (e.g. `demo --name proper-adjacency`).
Get the list any time with:

```bash
java -jar target/rectangles.jar demo --list
```

which prints the seven built-in scenarios:


| Name                 | Description                                                                         |
| -------------------- | ----------------------------------------------------------------------------------- |
| `disjoint`           | Rectangles are completely separate — no intersection, no containment, no adjacency. |
| `intersection`       | Partial overlap with two boundary crossing points. No containment.                  |
| `containment`        | Strict containment — B sits entirely inside A with no boundary contact.             |
| `sub-line-adjacency` | B's left side is wholly contained inside A's right side.                            |
| `proper-adjacency`   | Two rectangles share a full side (`A.right == B.left`).                             |
| `partial-adjacency`  | A's right side and B's left side share only a part of a side.                       |
| `corner-touch`       | Rectangles meet at a single corner — contact but no shared side.                    |


#### ASCII picture

Every scenario's picture is a perimeter-only drawing: Interiors are left blank so the two outlines stay easy to read. If one
rectangle is so small relative to the other that it would collapse to fewer
than two cells in either direction, it is drawn as a single letter marking
its location.

You can always turn the picture off for any command with `--draw false` (or
`-d false`) — for example when piping output into another tool.

### 1 — Disjoint

Two completely separate rectangles. No intersection, no containment, no adjacency.

```bash
java -jar target/rectangles.jar demo --name disjoint
```

```text
Scenario: disjoint
Rectangles are completely separate. No intersection no containment, and no adjacency.

Rectangle A: (0, 0) - (5, 5)  [width=5, height=5]
Rectangle B: (10, 10) - (15, 15)  [width=5, height=5]

Intersection: none — the rectangle boundaries do not cross.
Containment: none — neither rectangle wholly contains the other.
Adjacency: none — the rectangles do not share a side.

...................................
......................bbbbbbbbbbb..
......................b.........b..
......................b.........b..
......................b.........b..
......................b.........b..
......................bbbbbbbbbbb..
...................................
...................................
...................................
...................................
..aaaaaaaaaaa......................
..a.........a......................
..a.........a......................
..a.........a......................
..a.........a......................
..aaaaaaaaaaa......................
...................................
Legend: a = rect A boundary, b = rect B boundary, # = both, . = empty
```

### 2 — Intersection

The two rectangles overlap. Each boundary enters and exits the other, giving
two crossing points.

```bash
java -jar target/rectangles.jar demo --name intersection
```

```text
Scenario: intersection
Partial overlap with two boundary crossing points. No containment

Rectangle A: (0, 0) - (10, 10)  [width=10, height=10]
Rectangle B: (5, 5) - (15, 15)  [width=10, height=10]

Intersection: 2 points (5, 10), (10, 5)
Containment: none — neither rectangle wholly contains the other.
Adjacency: none — the rectangles do not share a side.

...................................
............bbbbbbbbbbbbbbbbbbbbb..
............b...................b..
............b...................b..
............b...................b..
............b...................b..
..aaaaaaaaaa#aaaaaaaaaa.........b..
..a.........b.........a.........b..
..a.........b.........a.........b..
..a.........b.........a.........b..
..a.........b.........a.........b..
..a.........bbbbbbbbbb#bbbbbbbbbb..
..a...................a............
..a...................a............
..a...................a............
..a...................a............
..aaaaaaaaaaaaaaaaaaaaa............
...................................
Legend: a = rect A boundary, b = rect B boundary, # = both, . = empty
```

Notice the two `#` cells — those are the reported intersection points,
`(5, 10)` and `(10, 5)`.

### 3 — Containment

B sits strictly inside A. The boundaries never touch, so the intersection is
empty and the two outlines don't share a single cell.

```bash
java -jar target/rectangles.jar demo --name containment
```

```text
Scenario: containment
Strict containment - B sits entirely inside A with no boundary contact.

Rectangle A: (0, 0) - (20, 20)  [width=20, height=20]
Rectangle B: (5, 5) - (15, 15)  [width=10, height=10]

Intersection: none — the rectangle boundaries do not cross.
Containment: A contains B — rectangle A wholly contains rectangle B.
Adjacency: none — the rectangles do not share a side.

.............................................
..aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa..
..a.......................................a..
..a.......................................a..
..a.......................................a..
..a.......................................a..
..a.........bbbbbbbbbbbbbbbbbbbbb.........a..
..a.........b...................b.........a..
..a.........b...................b.........a..
..a.........b...................b.........a..
..a.........b...................b.........a..
..a.........b...................b.........a..
..a.........b...................b.........a..
..a.........b...................b.........a..
..a.........b...................b.........a..
..a.........b...................b.........a..
..a.........bbbbbbbbbbbbbbbbbbbbb.........a..
..a.......................................a..
..a.......................................a..
..a.......................................a..
..a.......................................a..
..aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa..
.............................................
Legend: a = rect A boundary, b = rect B boundary, # = both, . = empty
```

### 4 —Adjacency (sub-line)

B's left side is wholly contained inside A's right side: one side sits
entirely on the other, but the other side extends further. The shared stretch
is a full side of *one* rectangle, not both.

```bash
java -jar target/rectangles.jar demo --name sub-line-adjacency
```

```text
Scenario: sub-line-adjacency
B's left side is wholly contained inside A's right side.

Rectangle A: (0, 0) - (10, 10)  [width=10, height=10]
Rectangle B: (10, 2) - (15, 7)  [width=5, height=5]

Intersection: 2 points (10, 2), (10, 7)
Containment: none — neither rectangle wholly contains the other.
Adjacency: sub-line — one side is wholly contained within a side of the other.

...................................
..aaaaaaaaaaaaaaaaaaaaa............
..a...................a............
..a...................a............
..a...................#bbbbbbbbbb..
..a...................#.........b..
..a...................#.........b..
..a...................#.........b..
..a...................#.........b..
..a...................#bbbbbbbbbb..
..a...................a............
..aaaaaaaaaaaaaaaaaaaaa............
...................................
Legend: a = rect A boundary, b = rect B boundary, # = both, . = empty
```

The run of `#` characters is the shared segment; the two endpoints,
`(10, 2)` and `(10, 7)`, are the reported intersection points.

### 5 — Adjacency (proper)

Two rectangles share a complete side. The full right edge of A is also the
full left edge of B.

```bash
java -jar target/rectangles.jar demo --name proper-adjacency
```

```text
Scenario: proper-adjacency
Two rectangles share a full side (A.right == B.left).

Rectangle A: (0, 0) - (10, 10)  [width=10, height=10]
Rectangle B: (10, 0) - (20, 10)  [width=10, height=10]

Intersection: 2 points (10, 0), (10, 10)
Containment: none — neither rectangle wholly contains the other.
Adjacency: proper — the rectangles share a complete side.

.............................................
..aaaaaaaaaaaaaaaaaaaa#bbbbbbbbbbbbbbbbbbbb..
..a...................#...................b..
..a...................#...................b..
..a...................#...................b..
..a...................#...................b..
..a...................#...................b..
..a...................#...................b..
..a...................#...................b..
..a...................#...................b..
..a...................#...................b..
..aaaaaaaaaaaaaaaaaaaa#bbbbbbbbbbbbbbbbbbbb..
.............................................
Legend: a = rect A boundary, b = rect B boundary, # = both, . = empty
```

Every cell along the shared edge is `#`.

### 6 — Adjacency (partial)

A's right side and B's left side overlap, but neither side is fully covered
by the other — they share only a part of a side.

```bash
java -jar target/rectangles.jar demo --name partial-adjacency
```

```text
Scenario: partial-adjacency
A's right side and B's left side share only a part of a side.

Rectangle A: (0, 0) - (10, 10)  [width=10, height=10]
Rectangle B: (10, 5) - (15, 15)  [width=5, height=10]

Intersection: 2 points (10, 5), (10, 10)
Containment: none — neither rectangle wholly contains the other.
Adjacency: partial — the rectangles share part of a side.

...................................
......................bbbbbbbbbbb..
......................b.........b..
......................b.........b..
......................b.........b..
......................b.........b..
..aaaaaaaaaaaaaaaaaaaa#.........b..
..a...................#.........b..
..a...................#.........b..
..a...................#.........b..
..a...................#.........b..
..a...................#bbbbbbbbbb..
..a...................a............
..a...................a............
..a...................a............
..a...................a............
..aaaaaaaaaaaaaaaaaaaaa............
...................................
Legend: a = rect A boundary, b = rect B boundary, # = both, . = empty
```

### 7 — corner touch

The rectangles meet at a single point. Touching a single corner is **not**
adjacency (adjacency requires a shared segment with non-zero length), but the
boundaries *do* still meet at that one point — so it's reported as a single
intersection point.

```bash
java -jar target/rectangles.jar demo --name corner-touch
```

```text
Scenario: corner-touch
Rectangles meet at a single corner — contact but no shared side.

Rectangle A: (0, 0) - (5, 5)  [width=5, height=5]
Rectangle B: (5, 5) - (10, 10)  [width=5, height=5]

Intersection: 1 point (5, 5)
Containment: none — neither rectangle wholly contains the other.
Adjacency: none — the rectangles do not share a side.

.........................
............bbbbbbbbbbb..
............b.........b..
............b.........b..
............b.........b..
............b.........b..
..aaaaaaaaaa#bbbbbbbbbb..
..a.........a............
..a.........a............
..a.........a............
..a.........a............
..aaaaaaaaaaa............
.........................
Legend: a = rect A boundary, b = rect B boundary, # = both, . = empty
```

---

## Analyzing your own rectangles

Use `analyze` to run the same checks on any pair of rectangles. Pass each
rectangle as two opposite corners (any two opposite corners will do — the
tool normalises them):

```bash
java -jar target/rectangles.jar analyze --rectangles 0,0,10,10,5,5,15,15 --draw false
```

```text
Rectangle A: (0, 0) - (10, 10)  [width=10, height=10]
Rectangle B: (5, 5) - (15, 15)  [width=10, height=10]

Intersection: 2 points (5, 10), (10, 5)
Containment: none — neither rectangle wholly contains the other.
Adjacency: none — the rectangles do not share a side.
```

### Filter which analyses you want

Use `--analysis` (`-a`) to run only some of the three checks:

```bash
java -jar target/rectangles.jar analyze -r 0,0,10,10,5,5,15,15 -a intersection,adjacency --draw false
```

```text
Rectangle A: (0, 0) - (10, 10)  [width=10, height=10]
Rectangle B: (5, 5) - (15, 15)  [width=10, height=10]

Intersection: 2 points (5, 10), (10, 5)
Adjacency: none — the rectangles do not share a side.
```

### Error handling

Bad input produces a friendly error on stdout, not a stack trace:

```bash
java -jar target/rectangles.jar analyze -r 0,0,0,5,1,1,2,2
```

```text
Error: Invalid Rectangle: width must be greater than 0 (got left=0, right=0 from Point[x=0, y=0] and Point[x=0, y=5]).
```

```bash
java -jar target/rectangles.jar analyze -r 0,0,1,1,2,2,3,3 --analysis bogus
```

```text
Error: Unknown analysis type 'bogus'. Valid values: [INTERSECTION, CONTAINMENT, ADJACENCY]
```

---

## CLI reference

### `analyze` — analyse a pair of rectangles

```bash
java -jar target/rectangles.jar analyze \
    --rectangles x1,y1,x2,y2,x3,y3,x4,y4 \
    [--analysis intersection,containment,adjacency] \
    [--draw true|false]
```


| Option         | Short | Required | Description                                                                                              |
| -------------- | ----- | -------- | -------------------------------------------------------------------------------------------------------- |
| `--rectangles` | `-r`  | yes      | Eight comma-separated integers giving two opposite corners of each rectangle: `x1,y1,x2,y2,x3,y3,x4,y4`. |
| `--analysis`   | `-a`  | no       | Comma-separated subset of `intersection`, `containment`, `adjacency`. Defaults to all three.             |
| `--draw`       | `-d`  | no       | Append an ASCII picture of the two rectangles. Defaults to `true`; pass `--draw false` to suppress.      |


The two corners may be given in any order — the rectangle is normalised so
`(x1,y1)` and `(x2,y2)` describe opposite corners. They must not share an x
or y coordinate, otherwise the rectangle is degenerate and the command
returns an error.

### `demo` — run pre-built scenarios

```bash
java -jar target/rectangles.jar demo [--name <scenario>] [--list] [--draw true|false]
```


| Option   | Short | Description                                                                                                     |
| -------- | ----- | --------------------------------------------------------------------------------------------------------------- |
| `--name` | `-n`  | Run only the named scenario (see `--list` for names).                                                           |
| `--list` | `-l`  | List all scenario names and descriptions, then exit.                                                            |
| `--draw` | `-d`  | Append an ASCII picture of each scenario's two rectangles. Defaults to `true`; pass `--draw false` to suppress. |


With no options, `demo` runs every scenario in turn. Available scenarios:
`disjoint`, `intersection`, `containment`, `sub-line-adjacency`,
`proper-adjacency`, `partial-adjacency`, `corner-touch`.

### `help` — list commands and show per-command help

With no arguments, `help` lists every available command grouped by category,
with the **Rectangle Analysis Commands** group first.

In one-shot mode (the default) the built-ins that only make sense inside a
REPL session — `clear`, `quit`/`exit`, and `script` — are hidden so the
output stays focused on what you can actually do. `help` and `version` are
kept because they're still useful when running a single command:

```bash
java -jar target/rectangles.jar help
```

```text
AVAILABLE COMMANDS

Rectangle Analysis Commands
	analyze: Analyze two rectangles for intersection, containment, and adjacency.
	demo: Run a built-in demo scenario (or all of them).
Built-In Commands
	help: Display help about available commands
	version: Show version info
```

In [interactive mode](#interactive-mode) all of the Spring Shell built-ins
are relevant, so they're listed in full:

```text
AVAILABLE COMMANDS

Rectangle Analysis Commands
	analyze: Analyze two rectangles for intersection, containment, and adjacency.
	demo: Run a built-in demo scenario (or all of them).
Built-In Commands
	clear: Clear the terminal screen
	help: Display help about available commands
	quit, exit: Exit the shell
	script: Execute commands from a script file
	version: Show version info
```

Pass a command name as an argument to print that command's full
NAME / SYNOPSIS / OPTIONS block — handy when you just want the flags for a
single command without scrolling through everything:

```bash
java -jar target/rectangles.jar help analyze
```

```bash
java -jar target/rectangles.jar help demo
```

---

## Build & run

### Requirements

- **Java 17 or newer** (verified on Java 17 and Java 25).
- The bundled **Maven wrapper** (`./mvnw`) — no separate Maven install
required.
- An internet connection on **first build only**, so Maven can download the
dependencies listed below into your local `~/.m2/repository`. No network
access is needed to run the jar afterwards.

### Dependencies

- **Parent POM:** `spring-boot-starter-parent`
- **Runtime:** `spring-shell-starter`
- **Test:** `spring-boot-starter-test`, `spring-shell-starter-test`
- **Build plugins:** `maven-surefire-plugin` (test runner),
`jacoco-maven-plugin` (HTML coverage report)

### Build

```bash
./mvnw package              # build + run tests
./mvnw -DskipTests package  # build, skip tests
```

Either command produces a single self-contained jar at:

```
target/rectangles.jar
```

The filename has **no version suffix** — it's just `rectangles.jar`, set via
`<finalName>` in the POM.

### Run

The application supports two modes: **one-shot** (the default, used in every
example in this README) and **interactive** (a Spring Shell REPL).

#### One-shot mode

Pass a command and its options as program arguments and the jar prints the
result and exits.

```bash
java -jar target/rectangles.jar <command> [options]
```

Two equivalent invocation styles are supported:

**1. `java -jar` (cross-platform — Mac, Linux, Windows):**

```bash
java -jar target/rectangles.jar analyze --rectangles 0,0,10,10,5,5,15,15
```

**2. Via Maven, without building a jar first:**

```bash
./mvnw -q spring-boot:run -Dspring-boot.run.arguments="analyze --rectangles 0,0,10,10,5,5,15,15"
```

Launching the jar with **no arguments** runs `demo` by default, which is the
fastest way to see the tool in action after a fresh build.

#### Interactive mode

To drop into the Spring Shell REPL instead, opt in with
`--spring.shell.interactive.enabled=true`:

```bash
java -jar target/rectangles.jar --spring.shell.interactive.enabled=true
```

You get a `shell:>` prompt where you can type the same commands as one-shot
mode, but **without** the `java -jar target/rectangles.jar` prefix:

```text
shell:>help
shell:>analyze --rectangles 0,0,10,10,5,5,15,15
shell:>demo --name proper-adjacency
shell:>exit
```

Interactive mode also gives you tab-completion, arrow-key history, and the
`quit` / `exit` commands. The same flag works via Maven too:

```bash
./mvnw -q spring-boot:run -Dspring-boot.run.arguments="--spring.shell.interactive.enabled=true"
```

---

## Definitions

The formal semantics implemented here:

- **Intersection points** are the points where rectangle *boundaries* meet.
  - Overlapping rectangles produce two boundary crossings.
  - Adjacent rectangles produce two points — the endpoints of the shared
  segment.
  - Two rectangles meeting at a single corner produce one intersection point.
  - Strict containment with no boundary contact produces an empty
  intersection.
- **Containment** considers a rectangle to contain another if every point of
the inner rectangle lies inside or on the boundary of the outer one. Two
identical rectangles are reported as `equal`, not as containing each other.
- **Adjacency** requires a shared edge segment of *non-zero* length. Two
rectangles meeting at a single corner are **not** adjacent.
  - **Proper** — the shared segment equals a full side of both rectangles.
  - **Sub-line** — the shared segment equals a full side of exactly one of
  the rectangles (i.e. one side is wholly contained within the other).
  - **Partial** — the shared segment is a strict subset of both sides.

---

## Tests

The project ships with a JUnit Jupiter / Mockito / AssertJ test suite (all
provided transitively by `spring-boot-starter-test`). Run it with:

```bash
./mvnw test
```

Run a single class, nested class, or method using Surefire's `-Dtest=` flag:

```bash
./mvnw test -Dtest=RectangleAnalysisServiceImplTest
./mvnw test -Dtest='RectangleAnalysisServiceImplTest$Intersection'
./mvnw test -Dtest='RectangleAnalysisServiceImplTest#analyzeWithSubsetSkipsOtherSections'
```

Surefire reports are written to `target/surefire-reports/`.

> On JDK 24+, Mockito's inline mock-maker needs the `byte-buddy` agent to be
> loaded up-front; the POM wires this in automatically so `./mvnw test` works
> out of the box on modern JDKs.

### Coverage report

Running `./mvnw test` also produces a **JaCoCo** HTML coverage report. Open it
in a browser:

```bash
open target/site/jacoco/index.html     # macOS
xdg-open target/site/jacoco/index.html # Linux
```

The landing page shows overall instruction / branch / line / method / class
coverage, and drills down package → class → source line, with covered lines
highlighted in green, partially-covered in yellow, and missed in red. JaCoCo
also emits machine-readable `jacoco.xml` and `jacoco.csv` in the same
directory for CI/reporting integrations.

