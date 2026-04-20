package org.konner.rectangles.formatter;

import org.junit.jupiter.api.Test;
import org.konner.rectangles.model.Adjacency;
import org.konner.rectangles.model.Containment;
import org.konner.rectangles.model.Point;
import org.konner.rectangles.model.Rectangle;
import org.konner.rectangles.model.RectangleAnalysisResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RectangleAnalysisFormatterImplTest {

    private final RectangleAnalysisFormatterImpl formatter = new RectangleAnalysisFormatterImpl();

    private static final Rectangle A = Rectangle.of(0, 0, 10, 10);
    private static final Rectangle B = Rectangle.of(5, 5, 15, 15);

    @Test
    void rendersRectangleHeadersWithDimensions() {
        String out = formatter.format(new RectangleAnalysisResult(A, B, List.of(), Containment.NONE, Adjacency.NONE));
        assertThat(out)
                .contains("Rectangle A: (0, 0) - (10, 10)  [width=10, height=10]")
                .contains("Rectangle B: (5, 5) - (15, 15)  [width=10, height=10]");
    }

    @Test
    void emptyIntersectionListRendersAsNone() {
        String out = formatter.format(new RectangleAnalysisResult(A, B, List.of(), null, null));
        assertThat(out).contains("Intersection: none");
    }

    @Test
    void singleIntersectionPointIsSingular() {
        String out = formatter.format(new RectangleAnalysisResult(A, B, List.of(new Point(5, 5)), null, null));
        assertThat(out).contains("Intersection: 1 point (5, 5)");
    }

    @Test
    void multipleIntersectionPointsArePlural() {
        String out = formatter.format(new RectangleAnalysisResult(
                A, B, List.of(new Point(5, 10), new Point(10, 5)), null, null));
        assertThat(out).contains("Intersection: 2 points (5, 10), (10, 5)");
    }

    @Test
    void rendersEachContainmentEnumValue() {
        assertThat(format(null, Containment.NONE, null)).contains("Containment: none");
        assertThat(format(null, Containment.A_CONTAINS_B, null)).contains("Containment: A contains B");
        assertThat(format(null, Containment.B_CONTAINS_A, null)).contains("Containment: B contains A");
        assertThat(format(null, Containment.EQUAL, null)).contains("Containment: equal");
    }

    @Test
    void rendersEachAdjacencyEnumValue() {
        assertThat(format(null, null, Adjacency.NONE)).contains("Adjacency: none");
        assertThat(format(null, null, Adjacency.PROPER)).contains("Adjacency: proper");
        assertThat(format(null, null, Adjacency.SUB_LINE)).contains("Adjacency: sub-line");
        assertThat(format(null, null, Adjacency.PARTIAL)).contains("Adjacency: partial");
    }

    @Test
    void omitsIntersectionSectionWhenNull() {
        String out = format(null, Containment.NONE, Adjacency.NONE);
        assertThat(out).doesNotContain("Intersection:");
        assertThat(out).contains("Containment:").contains("Adjacency:");
    }

    @Test
    void omitsContainmentSectionWhenNull() {
        String out = format(List.of(), null, Adjacency.NONE);
        assertThat(out).doesNotContain("Containment:");
        assertThat(out).contains("Intersection:").contains("Adjacency:");
    }

    @Test
    void omitsAdjacencySectionWhenNull() {
        String out = format(List.of(), Containment.NONE, null);
        assertThat(out).doesNotContain("Adjacency:");
        assertThat(out).contains("Intersection:").contains("Containment:");
    }

    @Test
    void omitsAllAnalysisSectionsWhenAllNull() {
        String out = format(null, null, null);
        assertThat(out)
                .doesNotContain("Intersection:")
                .doesNotContain("Containment:")
                .doesNotContain("Adjacency:")
                .contains("Rectangle A:")
                .contains("Rectangle B:");
    }

    private String format(List<Point> intersection, Containment containment, Adjacency adjacency) {
        return formatter.format(new RectangleAnalysisResult(A, B, intersection, containment, adjacency));
    }
}
