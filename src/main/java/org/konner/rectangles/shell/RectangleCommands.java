package org.konner.rectangles.shell;

import org.konner.rectangles.demo.DemoScenarios;
import org.konner.rectangles.demo.DemoScenarios.Scenario;
import org.konner.rectangles.exception.InvalidRectangleException;
import org.konner.rectangles.formatter.RectangleAnalysisFormatter;
import org.konner.rectangles.formatter.RectangleDrawer;
import org.konner.rectangles.model.AnalysisType;
import org.konner.rectangles.model.Rectangle;
import org.konner.rectangles.model.RectangleAnalysisResult;
import org.konner.rectangles.service.RectangleAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RectangleCommands {

    private static final String GROUP = "Rectangle Analysis Commands";

    @Autowired
    private RectangleAnalysisService analyzer;

    @Autowired
    private RectangleAnalysisFormatter formatter;

    @Autowired
    private RectangleDrawer drawer;

    @Autowired
    private DemoScenarios scenarios;

    @Command(
            name = "analyze",
            group = GROUP,
            description = "Analyze two rectangles for intersection, containment, and adjacency.",
            help = """
                   Analyzes two axis-aligned rectangles and reports:
                     • Intersection: any points where their boundaries meet,
                     • Containment:  whether one rectangle wholly contains the other,
                     • Adjacency:    whether they share a side (proper / sub-line / partial).

                   Each rectangle is specified by two opposite corners, so --rectangles
                   takes eight comma-separated integers in this order:

                       x1,y1,x2,y2,x3,y3,x4,y4

                   where (x1,y1)-(x2,y2) is rectangle A and (x3,y3)-(x4,y4) is rectangle B.

                   The --analysis option restricts which analyses are run. It accepts a
                   comma-separated list of: intersection, containment, adjacency. When
                   omitted all three analyses are run.

                   The --draw option controls whether a small ASCII picture of the two
                   rectangles is appended to the output. It is on by default; pass
                   --draw false to suppress it.

                   Examples:
                       analyze --rectangles 0,0,10,10,5,5,15,15
                       analyze -r 0,0,10,10,5,5,15,15 --analysis intersection,adjacency
                       analyze -r 0,0,10,10,5,5,15,15 --draw false
                   """
    )
    public String analyze(
            @Option(
                    longName = "rectangles",
                    shortName = 'r',
                    required = true,
                    description = "Eight comma-separated integers: x1,y1,x2,y2,x3,y3,x4,y4 "
                            + "(two opposite corners of each of the two rectangles)."
            ) String rectangles,
            @Option(
                    longName = "analysis",
                    shortName = 'a',
                    description = "Comma-separated list of analyses to run: "
                            + "intersection, containment, adjacency. Defaults to all three."
            ) String analysis,
            @Option(
                    longName = "draw",
                    shortName = 'd',
                    defaultValue = "true",
                    description = "Append an ASCII picture of the two rectangles. "
                            + "Defaults to true; pass --draw false to suppress."
            ) Boolean draw
    ) {
        int[] coords;
        try {
            coords = parseCoords(rectangles);
        } catch (IllegalArgumentException ex) {
            return "Error: " + ex.getMessage();
        }

        Rectangle a, b;
        try {
            a = Rectangle.of(coords[0], coords[1], coords[2], coords[3]);
            b = Rectangle.of(coords[4], coords[5], coords[6], coords[7]);
        } catch (InvalidRectangleException ex) {
            return "Error: " + ex.getMessage();
        }

        Set<AnalysisType> types;
        try {
            types = AnalysisType.parse(analysis);
        } catch (IllegalArgumentException ex) {
            return "Error: " + ex.getMessage();
        }

        RectangleAnalysisResult result = analyzer.analyze(a, b, types);
        String body = formatter.format(result);
        return shouldDraw(draw) ? body + "\n\n" + drawer.draw(a, b) : body;
    }

    @Command(
            name = "demo",
            group = GROUP,
            description = "Run a built-in demo scenario (or all of them).",
            help = """
                   Runs one or all of the pre-built demonstration scenarios. Each
                   scenario is a carefully chosen pair of rectangles that exhibits
                   a specific geometric relationship.

                   Invocations:
                       demo                              — run every scenario
                       demo --name intersection          — run a single scenario
                       demo --list                       — list scenario names
                       demo --draw false                 — suppress the ASCII pictures

                   Available scenarios:
                       disjoint                 no contact and no overlap
                       intersection             two boundary crossing points
                       containment              strict containment, no intersection
                       sub-line-adjacency       one side fully contained on another
                       proper-adjacency         full side sharing
                       partial-adjacency        shorter shared segment than both sides
                       corner-touch             contact at one corner only
                   """
    )
    public String demo(
            @Option(
                    longName = "name",
                    shortName = 'n',
                    description = "Run only the scenario with this name. Omit to run all scenarios."
            ) String name,
            @Option(
                    longName = "list",
                    shortName = 'l',
                    description = "List available scenario names and exit without running them."
            ) boolean list,
            @Option(
                    longName = "draw",
                    shortName = 'd',
                    defaultValue = "true",
                    description = "Append an ASCII picture of each scenario's two rectangles. "
                            + "Defaults to true; pass --draw false to suppress."
            ) Boolean draw
    ) {
        boolean drawPictures = shouldDraw(draw);
        if (list) {
            StringBuilder sb = new StringBuilder("Available scenarios:\n");
            scenarios.all().values().forEach(s ->
                    sb.append("  ").append(pad(s.name(), 22)).append(s.description()).append('\n'));
            return sb.toString().stripTrailing();
        }

        if (name != null && !name.isBlank()) {
            try {
                return renderScenario(scenarios.get(name.trim()), drawPictures);
            } catch (IllegalArgumentException ex) {
                return "Error: " + ex.getMessage();
            }
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Scenario s : scenarios.all().values()) {
            if (!first) sb.append('\n').append("-".repeat(72)).append("\n\n");
            sb.append(renderScenario(s, drawPictures));
            first = false;
        }
        return sb.toString();
    }

    private String renderScenario(Scenario s, boolean draw) {
        StringBuilder sb = new StringBuilder();
        sb.append("Scenario: ").append(s.name()).append('\n');
        sb.append(s.description()).append("\n\n");
        sb.append(formatter.format(analyzer.analyze(s.a(), s.b())));
        if (draw) {
            sb.append("\n\n").append(drawer.draw(s.a(), s.b()));
        }
        return sb.toString();
    }

    private static int[] parseCoords(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("--rectangles is required.");
        }
        String[] tokens = raw.split(",");
        if (tokens.length != 8) {
            throw new IllegalArgumentException(
                    "Expected 8 comma-separated integers (x1,y1,x2,y2,x3,y3,x4,y4) but got "
                            + tokens.length + ".");
        }
        int[] out = new int[8];
        for (int i = 0; i < 8; i++) {
            String t = tokens[i].trim();
            try {
                out[i] = Integer.parseInt(t);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(
                        "Could not parse '" + t + "' as an integer (position " + (i + 1) + ").");
            }
        }
        return out;
    }

    private static String pad(String s, int width) {
        return s.length() >= width ? s + " " : s + " ".repeat(width - s.length());
    }

    // Spring Shell only honors @Option(defaultValue=...) for boxed types, so
    // `draw` is declared as Boolean. A null here means Spring Shell somehow
    // skipped the default — fall back to true to preserve the contract.
    private static boolean shouldDraw(Boolean draw) {
        return draw == null || draw;
    }
}
