package org.konner.rectangles.model;

import java.util.List;

public record RectangleAnalysisResult(
        Rectangle rectangleA,
        Rectangle rectangleB,
        Intersection intersection,
        Containment containment,
        Adjacency adjacency
) {}
