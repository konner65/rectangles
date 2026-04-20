package org.konner.rectangles.formatter;

import org.konner.rectangles.model.Adjacency;
import org.konner.rectangles.model.Containment;
import org.konner.rectangles.model.Point;
import org.konner.rectangles.model.Rectangle;
import org.konner.rectangles.model.RectangleAnalysisResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RectangleAnalysisFormatterImpl implements RectangleAnalysisFormatter {

    @Override
    public String format(RectangleAnalysisResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rectangle A: ").append(describe(result.r1())).append('\n');
        sb.append("Rectangle B: ").append(describe(result.r2())).append('\n');

        if (result.intersection() != null) {
            sb.append('\n').append(formatIntersection(result.intersection()));
        }
        if (result.containment() != null) {
            sb.append('\n').append(formatContainment(result.containment()));
        }
        if (result.adjacency() != null) {
            sb.append('\n').append(formatAdjacency(result.adjacency()));
        }
        return sb.toString();
    }

    private static String describe(Rectangle r) {
        return String.format("(%d, %d) - (%d, %d)  [width=%d, height=%d]",
                r.getLeft(), r.getBottom(), r.getRight(), r.getTop(),
                r.getRight() - r.getLeft(), r.getTop() - r.getBottom());
    }

    private static String formatIntersection(List<Point> points) {
        StringBuilder sb = new StringBuilder("Intersection: ");
        if (points.isEmpty()) {
            sb.append("none — the rectangle boundaries do not cross.");
            return sb.toString();
        }
        sb.append(points.size()).append(points.size() == 1 ? " point" : " points");
        sb.append(' ');
        sb.append(points.stream()
                .map(p -> "(" + p.x() + ", " + p.y() + ")")
                .collect(Collectors.joining(", ")));
        return sb.toString();
    }

    private static String formatContainment(Containment c) {
        return switch (c) {
            case NONE          -> "Containment: none — neither rectangle wholly contains the other.";
            case A_CONTAINS_B  -> "Containment: A contains B — rectangle A wholly contains rectangle B.";
            case B_CONTAINS_A  -> "Containment: B contains A — rectangle B wholly contains rectangle A.";
            case EQUAL         -> "Containment: equal — the rectangles occupy the same region.";
        };
    }

    private static String formatAdjacency(Adjacency a) {
        return switch (a) {
            case NONE     -> "Adjacency: none — the rectangles do not share a side.";
            case PROPER   -> "Adjacency: proper — the rectangles share a complete side.";
            case SUB_LINE -> "Adjacency: sub-line — one side is wholly contained within a side of the other.";
            case PARTIAL  -> "Adjacency: partial — the rectangles share part of a side.";
        };
    }
}
