package org.konner.rectangles.model;

import java.util.List;

public record RectangleAnalysisResult(
        Rectangle r1,
        Rectangle r2,
        List<Point> intersection,
        Containment containment,
        Adjacency adjacency
) {}
