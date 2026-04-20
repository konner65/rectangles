package org.konner.rectangles.service;

import org.konner.rectangles.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.konner.rectangles.model.AnalysisType.ADJACENCY;
import static org.konner.rectangles.model.AnalysisType.CONTAINMENT;
import static org.konner.rectangles.model.AnalysisType.INTERSECTION;

import static org.konner.rectangles.model.Containment.A_CONTAINS_B;
import static org.konner.rectangles.model.Containment.B_CONTAINS_A;
import static org.konner.rectangles.model.Containment.EQUAL;

@Component
public class RectangleAnalysisServiceImpl implements RectangleAnalysisService {

    @Override
    public List<Point> intersection(Rectangle r1, Rectangle r2) {
        Set<Point> points = new LinkedHashSet<>();

        for (int[] h : horizontalSides(r1)) {
            for (int[] v : verticalSides(r2)) {
                addCrossing(points, h, v);
            }
        }
        for (int[] h : horizontalSides(r2)) {
            for (int[] v : verticalSides(r1)) {
                addCrossing(points, h, v);
            }
        }

        return new ArrayList<>(points);
    }

    @Override
    public Containment containment(Rectangle r1, Rectangle r2) {
        if (r1.equals(r2)) {
            return EQUAL;
        }
        if (r1.contains(r2)) {
            return A_CONTAINS_B;
        }
        if (r2.contains(r1)) {
            return B_CONTAINS_A;
        }
        return Containment.NONE;
    }

    @Override
    public Adjacency adjacency(Rectangle r1, Rectangle r2) {
        // Vertical edge sharing (a vertical line is shared). Compare y-ranges.
        if (r1.getRight() == r2.getLeft()) {
            return classify(r1.getBottom(), r1.getTop(), r2.getBottom(), r2.getTop());
        }
        if (r1.getLeft() == r2.getRight()) {
            return classify(r1.getBottom(), r1.getTop(), r2.getBottom(), r2.getTop());
        }
        // Horizontal edge sharing (a horizontal line is shared). Compare x-ranges.
        if (r1.getTop() == r2.getBottom()) {
            return classify(r1.getLeft(), r1.getRight(), r2.getLeft(), r2.getRight());
        }
        if (r1.getBottom() == r2.getTop()) {
            return classify(r1.getLeft(), r1.getRight(), r2.getLeft(), r2.getRight());
        }
        return Adjacency.NONE;
    }

    @Override
    public RectangleAnalysisResult analyze(Rectangle r1, Rectangle r2) {
        return analyze(r1, r2, AnalysisType.all());
    }

    @Override
    public RectangleAnalysisResult analyze(Rectangle r1, Rectangle r2, Set<AnalysisType> types) {
        Set<AnalysisType> selected = (types == null || types.isEmpty()) ? AnalysisType.all() : types;
        return new RectangleAnalysisResult(
                r1, r2,
                selected.contains(INTERSECTION) ? intersection(r1, r2) : null,
                selected.contains(CONTAINMENT)  ? containment(r1, r2)  : null,
                selected.contains(ADJACENCY)    ? adjacency(r1, r2)    : null);
    }

    private static Adjacency classify(int aLo, int aHi, int bLo, int bHi) {
        int lo = Math.max(aLo, bLo);
        int hi = Math.min(aHi, bHi);
        if (hi <= lo) {
            return Adjacency.NONE;
        }
        boolean coversA = lo == aLo && hi == aHi;
        boolean coversB = lo == bLo && hi == bHi;
        if (coversA && coversB) return Adjacency.PROPER;
        if (coversA || coversB) return Adjacency.SUB_LINE;
        return Adjacency.PARTIAL;
    }

    private static int[][] horizontalSides(Rectangle r) {
        return new int[][]{
                {r.getBottom(), r.getLeft(), r.getRight()},
                {r.getTop(),    r.getLeft(), r.getRight()}
        };
    }

    private static int[][] verticalSides(Rectangle r) {
        return new int[][]{
                {r.getLeft(),  r.getBottom(), r.getTop()},
                {r.getRight(), r.getBottom(), r.getTop()}
        };
    }

    private static void addCrossing(Set<Point> out, int[] horizontal, int[] vertical) {
        int y = horizontal[0], hxStart = horizontal[1], hxEnd = horizontal[2];
        int x = vertical[0],   vyStart = vertical[1],   vyEnd = vertical[2];
        if (x >= hxStart && x <= hxEnd && y >= vyStart && y <= vyEnd) {
            out.add(new Point(x, y));
        }
    }
}
