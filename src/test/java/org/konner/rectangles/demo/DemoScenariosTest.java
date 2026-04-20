package org.konner.rectangles.demo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DemoScenariosTest {

    private final DemoScenarios scenarios = new DemoScenarios();

    @Test
    void registersAllExpectedScenariosInOrder() {
        assertThat(scenarios.all().keySet())
                .containsExactly(
                        "disjoint",
                        "intersection",
                        "containment",
                        "sub-line-adjacency",
                        "proper-adjacency",
                        "partial-adjacency",
                        "corner-touch");
    }

    @Test
    void everyScenarioHasNonNullFields() {
        for (DemoScenarios.Scenario s : scenarios.all().values()) {
            assertThat(s.name()).isNotBlank();
            assertThat(s.description()).isNotBlank();
            assertThat(s.a()).isNotNull();
            assertThat(s.b()).isNotNull();
        }
    }

    @Test
    void getReturnsTheNamedScenario() {
        DemoScenarios.Scenario s = scenarios.get("intersection");
        assertThat(s.name()).isEqualTo("intersection");
    }

    @Test
    void getThrowsForUnknownScenarioWithHelpfulMessage() {
        assertThatThrownBy(() -> scenarios.get("does-not-exist"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does-not-exist")
                .hasMessageContaining("Available scenarios");
    }
}
