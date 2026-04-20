package org.konner.rectangles.demo;

import org.konner.rectangles.model.Rectangle;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DemoScenarios {
    public record Scenario(String name, String description, Rectangle a, Rectangle b) { }

    private final Map<String, Scenario> scenarios = new LinkedHashMap<>();

    public DemoScenarios() {
        register(new Scenario(
                "disjoint",
                "Rectangles are completely separate. No intersection, no containment, and no adjacency.",
                Rectangle.of(0, 0, 5, 5),
                Rectangle.of(10, 10, 15, 15)));

        register(new Scenario(
                "intersection",
                "Partial overlap with two boundary crossing points. No containment",
                Rectangle.of(0, 0, 10, 10),
                Rectangle.of(5, 5, 15, 15)));

        register(new Scenario(
                "containment",
                "Strict containment - B sits entirely inside A with no boundary contact.",
                Rectangle.of(0, 0, 20, 20),
                Rectangle.of(5, 5, 15, 15)));

        register(new Scenario(
                "sub-line-adjacency",
                "B's left side is wholly contained inside A's right side.",
                Rectangle.of(0, 0, 10, 10),
                Rectangle.of(10, 2, 15, 7)));

        register(new Scenario(
                "proper-adjacency",
                "Two rectangles share a full side (A.right == B.left).",
                Rectangle.of(0, 0, 10, 10),
                Rectangle.of(10, 0, 20, 10)));

        register(new Scenario(
                "partial-adjacency",
                "A's right side and B's left side share only a part of a side.",
                Rectangle.of(0, 0, 10, 10),
                Rectangle.of(10, 5, 15, 15)));

        register(new Scenario(
                "corner-touch",
                "Rectangles meet at a single corner — contact but no shared side.",
                Rectangle.of(0, 0, 5, 5),
                Rectangle.of(5, 5, 10, 10)));

    }

    private void register(Scenario s) {
        scenarios.put(s.name(), s);
    }

    public Map<String, Scenario> all() {
        return scenarios;
    }

    public Scenario get(String name) {
        Scenario s = scenarios.get(name);
        if (s == null) {
            throw new IllegalArgumentException(
                    "Unknown scenario '" + name + "'. Available scenarios: " + scenarios.keySet());
        }
        return s;
    }
}
