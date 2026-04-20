package org.konner.rectangles.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.konner.rectangles.exception.InvalidRectangleException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RectangleTest {

    @Nested
    class Construction {

        @Test
        void normalizesCornersRegardlessOfInputOrder() {
            Rectangle a = new Rectangle(new Point(0, 0), new Point(10, 5));
            Rectangle b = new Rectangle(new Point(10, 5), new Point(0, 0));
            Rectangle c = new Rectangle(new Point(10, 0), new Point(0, 5));
            Rectangle d = new Rectangle(new Point(0, 5), new Point(10, 0));

            for (Rectangle r : new Rectangle[]{a, b, c, d}) {
                assertThat(r.getLeft()).isZero();
                assertThat(r.getBottom()).isZero();
                assertThat(r.getRight()).isEqualTo(10);
                assertThat(r.getTop()).isEqualTo(5);
            }
        }

        @Test
        void factoryOfBuildsSameRectangleAsConstructor() {
            assertThat(Rectangle.of(0, 0, 5, 5))
                    .isEqualTo(new Rectangle(new Point(0, 0), new Point(5, 5)));
        }

        @Test
        void supportsNegativeCoordinates() {
            Rectangle r = Rectangle.of(-3, -7, 2, 4);
            assertThat(r.getLeft()).isEqualTo(-3);
            assertThat(r.getBottom()).isEqualTo(-7);
            assertThat(r.getRight()).isEqualTo(2);
            assertThat(r.getTop()).isEqualTo(4);
        }
    }

    @Nested
    class Validation {

        @Test
        void rejectsNullFirstPoint() {
            assertThatThrownBy(() -> new Rectangle(null, new Point(1, 1)))
                    .isInstanceOf(InvalidRectangleException.class)
                    .hasMessageContaining("points cannot be null");
        }

        @Test
        void rejectsNullSecondPoint() {
            assertThatThrownBy(() -> new Rectangle(new Point(0, 0), null))
                    .isInstanceOf(InvalidRectangleException.class)
                    .hasMessageContaining("points cannot be null");
        }

        @Test
        void rejectsZeroWidth() {
            assertThatThrownBy(() -> Rectangle.of(3, 0, 3, 5))
                    .isInstanceOf(InvalidRectangleException.class)
                    .hasMessageContaining("width must be greater than 0");
        }

        @Test
        void rejectsZeroHeight() {
            assertThatThrownBy(() -> Rectangle.of(0, 4, 5, 4))
                    .isInstanceOf(InvalidRectangleException.class)
                    .hasMessageContaining("height must be greater than 0");
        }

        @Test
        void rejectsIdenticalPoints() {
            assertThatThrownBy(() -> Rectangle.of(2, 2, 2, 2))
                    .isInstanceOf(InvalidRectangleException.class);
        }
    }

    @Nested
    class Contains {

        private final Rectangle outer = Rectangle.of(0, 0, 10, 10);

        @Test
        void containsItself() {
            assertThat(outer.contains(outer)).isTrue();
        }

        @Test
        void containsStrictlyInnerRectangle() {
            assertThat(outer.contains(Rectangle.of(2, 2, 8, 8))).isTrue();
        }

        @Test
        void containsRectangleSharingEdge() {
            assertThat(outer.contains(Rectangle.of(0, 0, 5, 5))).isTrue();
        }

        @Test
        void doesNotContainOverlappingRectangle() {
            assertThat(outer.contains(Rectangle.of(5, 5, 15, 15))).isFalse();
        }

        @Test
        void doesNotContainDisjointRectangle() {
            assertThat(outer.contains(Rectangle.of(20, 20, 30, 30))).isFalse();
        }
    }

    @Nested
    class EqualsAndHashCode {

        @Test
        void equalRectanglesHaveSameHashCode() {
            Rectangle a = Rectangle.of(0, 0, 4, 4);
            Rectangle b = Rectangle.of(4, 4, 0, 0);
            assertThat(a).isEqualTo(b);
            assertThat(a).hasSameHashCodeAs(b);
        }

        @Test
        void differentRectanglesAreNotEqual() {
            assertThat(Rectangle.of(0, 0, 4, 4)).isNotEqualTo(Rectangle.of(0, 0, 5, 5));
        }

        @Test
        void notEqualToOtherTypes() {
            assertThat(Rectangle.of(0, 0, 4, 4)).isNotEqualTo("rectangle");
            assertThat(Rectangle.of(0, 0, 4, 4)).isNotEqualTo(null);
        }
    }

    @Test
    void toStringIncludesCorners() {
        assertThat(Rectangle.of(1, 2, 3, 4))
                .hasToString("Rectangle[(1,2)-(3,4)]");
    }
}
