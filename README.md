# Rectangles

A small command-line tool that analyses pairs of axis-aligned rectangles and
reports:

1. **Intersection** — the points at which the rectangle boundaries cross.
2. **Containment** — whether one rectangle wholly contains the other.
3. **Adjacency** — whether the rectangles share a side, classified as
   *proper*, *sub-line*, or *partial*.

The project is a Spring Boot / Spring Shell application written in Java 17+.

---

## Requirements

- Java 17 or newer (the build was verified on Java 17 and Java 25)
- The bundled Maven wrapper (`./mvnw`) — no separate Maven install required

## Build

From the project root:

```bash
./mvnw -DskipTests package
```

The build produces an executable jar at:

```
target/rectangles-0.0.1-SNAPSHOT.jar
```

## Run

The application is a one-shot CLI: pass a command and its options as program
arguments and it prints the result and exits.

```bash
java -jar target/rectangles-0.0.1-SNAPSHOT.jar <command> [options]
```

You can also use the Spring Boot Maven plugin if you prefer not to build a jar:

```bash
./mvnw -q spring-boot:run -Dspring-boot.run.arguments="analyze --rectangles 0,0,10,10,5,5,15,15"
```

If you launch the jar with no arguments it runs `demo` by default.

---

## Commands

### `analyze` — analyse a pair of rectangles

```bash
java -jar target/rectangles-0.0.1-SNAPSHOT.jar analyze \
    --rectangles x1,y1,x2,y2,x3,y3,x4,y4 \
    [--analysis intersection,containment,adjacency]
```

| Option         | Short | Required | Description                                                                                              |
|----------------|:-----:|:--------:|----------------------------------------------------------------------------------------------------------|
| `--rectangles` | `-r`  |   yes    | Eight comma-separated integers giving two opposite corners of each rectangle: `x1,y1,x2,y2,x3,y3,x4,y4`. |
| `--analysis`   | `-a`  |    no    | Comma-separated subset of `intersection`, `containment`, `adjacency`. Defaults to all three.             |

The two corners may be supplied in any order — the rectangle is normalised so
that `(x1,y1)` and `(x2,y2)` describe opposite corners. They must not share an
x or y coordinate, otherwise the rectangle is degenerate and the command
returns an error.

### `demo` — run pre-built scenarios

```bash
java -jar target/rectangles-0.0.1-SNAPSHOT.jar demo [--name <scenario>] [--list]
```

| Option   | Short | Description                                              |
|----------|:-----:|----------------------------------------------------------|
| `--name` | `-n`  | Run only the named scenario (see `--list` for names).    |
| `--list` | `-l`  | List all scenario names and descriptions, then exit.     |

With no options, `demo` runs every scenario in turn.

Available scenarios: `disjoint`, `intersection`, `containment`,
`sub-line-adjacency`, `proper-adjacency`, `partial-adjacency`, `corner-touch`.

### `help` — built-in help

Spring Shell provides a `help` command that lists every command and shows the
detailed help text for a given one:

```bash
java -jar target/rectangles-0.0.1-SNAPSHOT.jar help
java -jar target/rectangles-0.0.1-SNAPSHOT.jar help analyze
```

---

## Example input / output

### Two overlapping rectangles

```bash
$ java -jar target/rectangles-0.0.1-SNAPSHOT.jar analyze --rectangles 0,0,10,10,5,5,15,15
Rectangle A: (0, 0) - (10, 10)  [width=10, height=10]
Rectangle B: (5, 5) - (15, 15)  [width=10, height=10]

Intersection: 2 points (5, 10), (10, 5)
Containment: none — neither rectangle wholly contains the other.
Adjacency: none — the rectangles do not share a side.
```

### Containment

```bash
$ java -jar target/rectangles-0.0.1-SNAPSHOT.jar analyze -r 0,0,20,20,5,5,15,15
Rectangle A: (0, 0) - (20, 20)  [width=20, height=20]
Rectangle B: (5, 5) - (15, 15)  [width=10, height=10]

Intersection: none — the rectangle boundaries do not cross.
Containment: A contains B — rectangle A wholly contains rectangle B.
Adjacency: none — the rectangles do not share a side.
```

### Proper adjacency (full shared side)

```bash
$ java -jar target/rectangles-0.0.1-SNAPSHOT.jar analyze -r 0,0,10,10,10,0,20,10
Rectangle A: (0, 0) - (10, 10)  [width=10, height=10]
Rectangle B: (10, 0) - (20, 10)  [width=10, height=10]

Intersection: 2 points (10, 0), (10, 10)
Containment: none — neither rectangle wholly contains the other.
Adjacency: proper — the rectangles share a complete side.
```

### Sub-line adjacency (one side fully contained on another)

```bash
$ java -jar target/rectangles-0.0.1-SNAPSHOT.jar analyze -r 0,0,10,10,10,2,15,7
Rectangle A: (0, 0) - (10, 10)  [width=10, height=10]
Rectangle B: (10, 2) - (15, 7)  [width=5, height=5]

Intersection: 2 points (10, 2), (10, 7)
Containment: none — neither rectangle wholly contains the other.
Adjacency: sub-line — one side is wholly contained within a side of the other.
```

### Partial adjacency

```bash
$ java -jar target/rectangles-0.0.1-SNAPSHOT.jar analyze -r 0,0,10,10,10,5,15,15
Rectangle A: (0, 0) - (10, 10)  [width=10, height=10]
Rectangle B: (10, 5) - (15, 15)  [width=5, height=10]

Intersection: 2 points (10, 10), (10, 5)
Containment: none — neither rectangle wholly contains the other.
Adjacency: partial — the rectangles share part of a side.
```

### Filtering analyses

You can ask for only some of the three analyses with `--analysis`:

```bash
$ java -jar target/rectangles-0.0.1-SNAPSHOT.jar analyze -r 0,0,10,10,5,5,15,15 -a intersection,adjacency
Rectangle A: (0, 0) - (10, 10)  [width=10, height=10]
Rectangle B: (5, 5) - (15, 15)  [width=10, height=10]

Intersection: 2 points (5, 10), (10, 5)
Adjacency: none — the rectangles do not share a side.
```

### Listing demo scenarios

```bash
$ java -jar target/rectangles-0.0.1-SNAPSHOT.jar demo --list
Available scenarios:
  disjoint              Rectangles are completely separate. No intersection no containment, and no adjacency.
  intersection          Partial overlap with two boundary crossing points. No containment
  containment           Strict containment - B sits entirely inside A with no boundary contact.
  sub-line-adjacency    B's left side is wholly contained inside A's right side.
  proper-adjacency      Two rectangles share a full side (A.right == B.left).
  partial-adjacency     A's right side and B's left side share only a part of a side.
  corner-touch          Rectangles meet at a single corner — contact but no shared side.
```

### Running a single demo scenario

```bash
$ java -jar target/rectangles-0.0.1-SNAPSHOT.jar demo --name corner-touch
Scenario: corner-touch
Rectangles meet at a single corner — contact but no shared side.

Rectangle A: (0, 0) - (5, 5)  [width=5, height=5]
Rectangle B: (5, 5) - (10, 10)  [width=5, height=5]

Intersection: 1 point (5, 5)
Containment: none — neither rectangle wholly contains the other.
Adjacency: none — the rectangles do not share a side.
```

### Error handling

Bad input is reported on stdout instead of throwing a stack trace:

```bash
$ java -jar target/rectangles-0.0.1-SNAPSHOT.jar analyze -r 0,0,0,5,1,1,2,2
Error: Invalid Rectangle: the 2 points cannot share the same x or y coordinate (width and height must be greater than 0). Got Point[x=0, y=0] and Point[x=0, y=5]

$ java -jar target/rectangles-0.0.1-SNAPSHOT.jar analyze -r 0,0,1,1,2,2,3,3 --analysis bogus
Error: Unknown analysis type 'bogus'. Valid values: [INTERSECTION, CONTAINMENT, ADJACENCY]
```

---

## Definitions

A few notes on the semantics implemented here:

- **Intersection points** are the points where rectangle *boundaries* cross.
  For two rectangles that overlap, there are typically two such points (where
  one rectangle's edge enters and exits the other). For two rectangles that
  share a stretch of edge, the reported intersection points are the two
  endpoints of the shared stretch (those are the points at which boundary
  segments actually cross). A pure containment with no boundary contact
  produces an empty intersection.
- **Containment** considers a rectangle to contain another if every point of
  the inner rectangle lies inside or on the boundary of the outer one. Two
  identical rectangles are reported as `equal`, not as containing each other.
- **Adjacency** requires a shared edge segment of non-zero length. Two
  rectangles meeting at a single corner are *not* adjacent.
  - **Proper** — the shared segment equals a full side of both rectangles.
  - **Sub-line** — the shared segment equals a full side of exactly one of
    the rectangles (i.e. one side is wholly contained within the other).
  - **Partial** — the shared segment is a strict subset of both sides.

---

## Project layout

```
src/main/java/org/konner/rectangles
├── RectanglesApplication.java          Spring Boot entry point
├── shell/RectangleCommands.java        Spring Shell command definitions
├── service/
│   ├── RectangleAnalysisService.java   Service interface
│   └── RectangleAnalysisServiceImpl.java
├── formatter/
│   ├── RectangleAnalysisFormatter.java         Output interface
│   └── TextRectangleAnalysisFormatter.java     Plain-text implementation
├── demo/DemoScenarios.java             Built-in demo scenarios for `demo`
├── model/
│   ├── Rectangle.java
│   ├── Point.java
│   ├── Containment.java
│   ├── Adjacency.java
│   ├── AnalysisType.java
│   └── RectangleAnalysisResult.java
└── exception/InvalidRectangleException.java
```
