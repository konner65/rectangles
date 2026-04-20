package org.konner.rectangles.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.konner.rectangles.model.AnalysisType.ADJACENCY;
import static org.konner.rectangles.model.AnalysisType.CONTAINMENT;
import static org.konner.rectangles.model.AnalysisType.INTERSECTION;

class AnalysisTypeTest {

    @Test
    void allReturnsEveryEnumValue() {
        assertThat(AnalysisType.all())
                .containsExactlyInAnyOrder(INTERSECTION, CONTAINMENT, ADJACENCY);
    }

    @Test
    void parseNullReturnsAll() {
        assertThat(AnalysisType.parse(null)).isEqualTo(AnalysisType.all());
    }

    @Test
    void parseEmptyReturnsAll() {
        assertThat(AnalysisType.parse("")).isEqualTo(AnalysisType.all());
    }

    @Test
    void parseBlankReturnsAll() {
        assertThat(AnalysisType.parse("   ")).isEqualTo(AnalysisType.all());
    }

    @Test
    void parseOnlyCommasReturnsAll() {
        assertThat(AnalysisType.parse(",,,")).isEqualTo(AnalysisType.all());
    }

    @Test
    void parseSingleValue() {
        assertThat(AnalysisType.parse("intersection"))
                .containsExactly(INTERSECTION);
    }

    @Test
    void parseMultipleValues() {
        assertThat(AnalysisType.parse("intersection,adjacency"))
                .containsExactlyInAnyOrder(INTERSECTION, ADJACENCY);
    }

    @Test
    void parseIsCaseInsensitive() {
        assertThat(AnalysisType.parse("Intersection,CONTAINMENT,adjacency"))
                .containsExactlyInAnyOrder(INTERSECTION, CONTAINMENT, ADJACENCY);
    }

    @Test
    void parseTolerantToWhitespace() {
        assertThat(AnalysisType.parse("  intersection ,  adjacency  "))
                .containsExactlyInAnyOrder(INTERSECTION, ADJACENCY);
    }

    @Test
    void parseTolerantToEmptyTokens() {
        assertThat(AnalysisType.parse("intersection,,adjacency"))
                .containsExactlyInAnyOrder(INTERSECTION, ADJACENCY);
    }

    @Test
    void parseRejectsUnknownToken() {
        assertThatThrownBy(() -> AnalysisType.parse("intersection,bogus"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown analysis type 'bogus'");
    }
}
