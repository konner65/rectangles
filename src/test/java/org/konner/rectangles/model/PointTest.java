package org.konner.rectangles.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PointTest {

    @Test
    void exposesXAndYAccessors() {
        Point p = new Point(3, -5);
        assertThat(p.x()).isEqualTo(3);
        assertThat(p.y()).isEqualTo(-5);
    }

    @Test
    void recordsAreValueEqual() {
        assertThat(new Point(1, 2)).isEqualTo(new Point(1, 2));
        assertThat(new Point(1, 2)).hasSameHashCodeAs(new Point(1, 2));
        assertThat(new Point(1, 2)).isNotEqualTo(new Point(2, 1));
    }
}
