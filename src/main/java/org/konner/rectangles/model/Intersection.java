package org.konner.rectangles.model;

import java.util.List;

public record Intersection(boolean intersects, List<Point> intersectionPoints) {}
