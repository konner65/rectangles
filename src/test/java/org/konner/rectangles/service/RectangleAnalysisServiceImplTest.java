package org.konner.rectangles.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.konner.rectangles.model.Adjacency;
import org.konner.rectangles.model.AnalysisType;
import org.konner.rectangles.model.Containment;
import org.konner.rectangles.model.Point;
import org.konner.rectangles.model.Rectangle;
import org.konner.rectangles.model.RectangleAnalysisResult;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

class RectangleAnalysisServiceImplTest {

    private final RectangleAnalysisServiceImpl service = new RectangleAnalysisServiceImpl();

    // ---------- intersection ----------

    @Nested
    class Intersection {

        @Test
        void disjointRectanglesHaveNoIntersection() {
            assertThat(service.intersection(
                    Rectangle.of(0, 0, 5, 5),
                    Rectangle.of(10, 10, 15, 15))
            ).isEmpty();
        }

        @Test
        void strictlyContainedRectangleHasNoBoundaryIntersection() {
            assertThat(service.intersection(
                    Rectangle.of(0, 0, 20, 20),
                    Rectangle.of(5, 5, 15, 15))
            ).isEmpty();
        }

        @Test
        void overlappingRectanglesHaveTwoCrossings() {
            assertThat(service.intersection(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(5, 5, 15, 15))
            ).containsExactlyInAnyOrder(new Point(5, 10), new Point(10, 5));
        }

        @Test
        void cornerTouchProducesOneIntersection() {
            assertThat(service.intersection(
                    Rectangle.of(0, 0, 5, 5),
                    Rectangle.of(5, 5, 10, 10))
            ).containsExactly(new Point(5, 5));
        }

        @Test
        void equalRectanglesIntersectAtAllFourCorners() {
            assertThat(service.intersection(
                    Rectangle.of(0, 0, 5, 5),
                    Rectangle.of(0, 0, 5, 5))
            ).containsExactlyInAnyOrder(
                    new Point(0, 0),
                    new Point(0, 5),
                    new Point(5, 0),
                    new Point(5, 5));
        }

        @Test
        void properlyAdjacentRectanglesShareTwoEndpoints() {
            assertThat(service.intersection(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(10, 0, 20, 10))
            ).containsExactlyInAnyOrder(new Point(10, 0), new Point(10, 10));
        }

        @Test
        void subLineAdjacencyReportsContainedSegmentEndpoints() {
            assertThat(service.intersection(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(10, 2, 15, 7))
            ).containsExactlyInAnyOrder(new Point(10, 2), new Point(10, 7));
        }

        @Test
        void partialAdjacencyReportsOverlapSegmentEndpoints() {
            assertThat(service.intersection(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(10, 5, 15, 15))
            ).containsExactlyInAnyOrder(new Point(10, 5), new Point(10, 10));
        }

        @Test
        void containedRectangleSharingCornerStillProducesBoundaryPoints() {
            // Inner rect shares the bottom-left corner of the outer one.
            assertThat(service.intersection(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(0, 0, 5, 5))
            ).containsExactlyInAnyOrder(
                    new Point(0, 0),
                    new Point(0, 5),
                    new Point(5, 0));
        }

        @Test
        void resultIsCommutative() {
            Rectangle a = Rectangle.of(0, 0, 10, 10);
            Rectangle b = Rectangle.of(5, 5, 15, 15);
            assertThat(service.intersection(a, b))
                    .containsExactlyInAnyOrderElementsOf(service.intersection(b, a));
        }
    }

    // ---------- containment ----------

    @Nested
    class ContainmentTests {

        @Test
        void disjointRectanglesAreNotInContainment() {
            assertThat(service.containment(
                    Rectangle.of(0, 0, 5, 5),
                    Rectangle.of(10, 10, 15, 15))
            ).isEqualTo(Containment.NONE);
        }

        @Test
        void aContainsB() {
            assertThat(service.containment(
                    Rectangle.of(0, 0, 20, 20),
                    Rectangle.of(5, 5, 15, 15))
            ).isEqualTo(Containment.A_CONTAINS_B);
        }

        @Test
        void bContainsA() {
            assertThat(service.containment(
                    Rectangle.of(5, 5, 15, 15),
                    Rectangle.of(0, 0, 20, 20))
            ).isEqualTo(Containment.B_CONTAINS_A);
        }

        @Test
        void equalRectanglesReportEqualNotContainment() {
            assertThat(service.containment(
                    Rectangle.of(0, 0, 5, 5),
                    Rectangle.of(0, 0, 5, 5))
            ).isEqualTo(Containment.EQUAL);
        }

        @Test
        void containmentAllowsBoundaryContact() {
            assertThat(service.containment(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(0, 0, 5, 5))
            ).isEqualTo(Containment.A_CONTAINS_B);
        }

        @Test
        void overlappingButNeitherContainingIsNone() {
            assertThat(service.containment(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(5, 5, 15, 15))
            ).isEqualTo(Containment.NONE);
        }
    }

    // ---------- adjacency ----------

    @Nested
    class AdjacencyTests {

        @Test
        void disjointIsNone() {
            assertThat(service.adjacency(
                    Rectangle.of(0, 0, 5, 5),
                    Rectangle.of(10, 10, 15, 15))
            ).isEqualTo(Adjacency.NONE);
        }

        @Test
        void overlappingIsNone() {
            assertThat(service.adjacency(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(5, 5, 15, 15))
            ).isEqualTo(Adjacency.NONE);
        }

        @Test
        void cornerTouchIsNotAdjacent() {
            assertThat(service.adjacency(
                    Rectangle.of(0, 0, 5, 5),
                    Rectangle.of(5, 5, 10, 10))
            ).isEqualTo(Adjacency.NONE);
        }

        @Test
        void properAdjacencyOnRightLeft() {
            assertThat(service.adjacency(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(10, 0, 20, 10))
            ).isEqualTo(Adjacency.PROPER);
        }

        @Test
        void properAdjacencyOnLeftRight() {
            assertThat(service.adjacency(
                    Rectangle.of(10, 0, 20, 10),
                    Rectangle.of(0, 0, 10, 10))
            ).isEqualTo(Adjacency.PROPER);
        }

        @Test
        void properAdjacencyOnTopBottom() {
            assertThat(service.adjacency(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(0, 10, 10, 20))
            ).isEqualTo(Adjacency.PROPER);
        }

        @Test
        void properAdjacencyOnBottomTop() {
            assertThat(service.adjacency(
                    Rectangle.of(0, 10, 10, 20),
                    Rectangle.of(0, 0, 10, 10))
            ).isEqualTo(Adjacency.PROPER);
        }

        @Test
        void subLineAdjacencyWhenOneSideIsContainedInTheOther() {
            assertThat(service.adjacency(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(10, 2, 15, 7))
            ).isEqualTo(Adjacency.SUB_LINE);
        }

        @Test
        void subLineAdjacencyWhenLargerSideIsTheSecondRectangle() {
            assertThat(service.adjacency(
                    Rectangle.of(10, 2, 15, 7),
                    Rectangle.of(0, 0, 10, 10))
            ).isEqualTo(Adjacency.SUB_LINE);
        }

        @Test
        void partialAdjacencyWhenSidesOverlapButNeitherIsContained() {
            assertThat(service.adjacency(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(10, 5, 15, 15))
            ).isEqualTo(Adjacency.PARTIAL);
        }

        @Test
        void noAdjacencyWhenEdgesAreOnSameLineButDoNotOverlap() {
            // Both touch x=10 but their y-ranges don't overlap at all.
            assertThat(service.adjacency(
                    Rectangle.of(0, 0, 10, 5),
                    Rectangle.of(10, 8, 15, 12))
            ).isEqualTo(Adjacency.NONE);
        }
    }

    // ---------- analyze ----------

    @Nested
    class Analyze {

        @Test
        void analyzeWithoutTypesRunsAllThree() {
            RectangleAnalysisResult result = service.analyze(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(5, 5, 15, 15));

            assertThat(result.r1()).isEqualTo(Rectangle.of(0, 0, 10, 10));
            assertThat(result.r2()).isEqualTo(Rectangle.of(5, 5, 15, 15));
            assertThat(result.intersection()).isNotNull();
            assertThat(result.containment()).isEqualTo(Containment.NONE);
            assertThat(result.adjacency()).isEqualTo(Adjacency.NONE);
        }

        @Test
        void analyzeWithSubsetSkipsOtherSections() {
            RectangleAnalysisResult result = service.analyze(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(5, 5, 15, 15),
                    EnumSet.of(AnalysisType.INTERSECTION));

            assertThat(result.intersection()).isNotEmpty();
            assertThat(result.containment()).isNull();
            assertThat(result.adjacency()).isNull();
        }

        @Test
        void analyzeWithNullTypesDefaultsToAll() {
            RectangleAnalysisResult result = service.analyze(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(5, 5, 15, 15),
                    null);

            assertThat(result.intersection()).isNotNull();
            assertThat(result.containment()).isNotNull();
            assertThat(result.adjacency()).isNotNull();
        }

        @Test
        void analyzeWithEmptyTypesDefaultsToAll() {
            RectangleAnalysisResult result = service.analyze(
                    Rectangle.of(0, 0, 10, 10),
                    Rectangle.of(5, 5, 15, 15),
                    EnumSet.noneOf(AnalysisType.class));

            assertThat(result.intersection()).isNotNull();
            assertThat(result.containment()).isNotNull();
            assertThat(result.adjacency()).isNotNull();
        }
    }
}
