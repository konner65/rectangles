package org.konner.rectangles.shell;

import org.konner.rectangles.analyzer.RectangleAnalyzer;
import org.konner.rectangles.demo.DemoScenarios;
import org.konner.rectangles.formatter.RectangleAnalysisFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

@Component
public class RectangleCommands {

    @Autowired
    private RectangleAnalyzer analyzer;

    @Autowired
    private RectangleAnalysisFormatter formatter;

    @Autowired
    private DemoScenarios scenarios;

    @Command(
            name = "analyze",
            description = "Analyze two rectangles for intersection, containment, and adjacency.",
            help = """
                   Analyzes two axis-aligned rectangles and reports:
                     • any points where their boundaries meet,
                     • whether one rectangle wholly contains the other,
                     • whether they share a side (proper / sub-line / partial).

                   Each rectangle is specified by its bottom-left and top-right corner,
                   so --rectangles takes eight comma-separated numbers in this order:

                       x1,y1,x2,y2,x3,y3,x4,y4

                   where (x1,y1)-(x2,y2) is rectangle A and (x3,y3)-(x4,y4) is rectangle B.

                   Example:
                       analyze --rectangles 0,0,10,10,5,5,15,15
                   """
    )
    public String analyze(
            @Option(
                    longName = "rectangles",
                    shortName = 'r',
                    required = true,
                    description = "Eight comma-separated numbers: x1,y1,x2,y2,x3,y3,x4,y4 "
                            + "(bottom-left and top-right of each of the two rectangles)."
            ) String rectangles
    ) {
        //todo
        return "";
    }

    @Command(
            name = "demo",
            description = "Run a built-in demo scenario (or all of them).",
            help = """
                   Runs one or all of the pre-built demonstration scenarios. Each
                   scenario is a carefully chosen pair of rectangles that exhibits
                   a specific geometric relationship.

                   Invocations:
                       demo                              — run every scenario
                       demo --name intersection          — run a single scenario
                       demo --list                       — list scenario names

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
            ) boolean list
    ) {
        //todo
        return "";
    }

}
