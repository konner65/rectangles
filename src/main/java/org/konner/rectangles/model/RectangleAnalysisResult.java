package org.konner.rectangles.model;

import java.util.List;

public record RectangleAnalysisResult(
        Rectangle rectangleA,
        Rectangle rectangleB,
        List<Point> intersectionPoints,
        Containment containment,
        Adjacency adjacency
) {
    public boolean intersects() {
        return !intersectionPoints.isEmpty();
    }

    public boolean isAdjacent() {
        return adjacency != Adjacency.NONE;
    }

    public boolean isContained() {
        return containment != Containment.NONE;
    }
}
