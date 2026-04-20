package org.konner.rectangles.shell;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.konner.rectangles.demo.DemoScenarios;
import org.konner.rectangles.demo.DemoScenarios.Scenario;
import org.konner.rectangles.formatter.RectangleAnalysisFormatter;
import org.konner.rectangles.formatter.RectangleDrawer;
import org.konner.rectangles.model.Adjacency;
import org.konner.rectangles.model.AnalysisType;
import org.konner.rectangles.model.Containment;
import org.konner.rectangles.model.Rectangle;
import org.konner.rectangles.model.RectangleAnalysisResult;
import org.konner.rectangles.service.RectangleAnalysisService;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RectangleCommandsTest {

    @Mock
    private RectangleAnalysisService analyzer;

    @Mock
    private RectangleAnalysisFormatter formatter;

    @Mock
    private RectangleDrawer drawer;

    @Mock
    private DemoScenarios scenarios;

    @InjectMocks
    private RectangleCommands commands;

    // ---------- analyze ----------

    @Nested
    class AnalyzeCommand {

        private RectangleAnalysisResult fakeResult;

        @BeforeEach
        void stubAnalyzerAndFormatter() {
            // We don't care about the precise contents — only that the command
            // wires the service result through the formatter.
            fakeResult = new RectangleAnalysisResult(
                    Rectangle.of(0, 0, 1, 1),
                    Rectangle.of(0, 0, 1, 1),
                    List.of(),
                    Containment.NONE,
                    Adjacency.NONE);
        }

        @Test
        void parsesCoordinatesAndDelegatesToServiceAndFormatter() {
            when(analyzer.analyze(any(), any(), any())).thenReturn(fakeResult);
            when(formatter.format(fakeResult)).thenReturn("FORMATTED");

            String out = commands.analyze("0,0,10,10,5,5,15,15", null, false);

            assertThat(out).isEqualTo("FORMATTED");

            ArgumentCaptor<Rectangle> a = ArgumentCaptor.forClass(Rectangle.class);
            ArgumentCaptor<Rectangle> b = ArgumentCaptor.forClass(Rectangle.class);
            @SuppressWarnings("unchecked")
            ArgumentCaptor<Set<AnalysisType>> types = ArgumentCaptor.forClass(Set.class);
            verify(analyzer).analyze(a.capture(), b.capture(), types.capture());

            assertThat(a.getValue()).isEqualTo(Rectangle.of(0, 0, 10, 10));
            assertThat(b.getValue()).isEqualTo(Rectangle.of(5, 5, 15, 15));
            assertThat(types.getValue()).isEqualTo(AnalysisType.all());
            verifyNoInteractions(drawer);
        }

        @Test
        void analysisOptionRestrictsRequestedTypes() {
            when(analyzer.analyze(any(), any(), any())).thenReturn(fakeResult);
            when(formatter.format(any())).thenReturn("FORMATTED");

            commands.analyze("0,0,10,10,5,5,15,15", "intersection,adjacency", false);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<Set<AnalysisType>> types = ArgumentCaptor.forClass(Set.class);
            verify(analyzer).analyze(any(), any(), types.capture());
            assertThat(types.getValue())
                    .containsExactlyInAnyOrder(AnalysisType.INTERSECTION, AnalysisType.ADJACENCY);
        }

        @Test
        void toleratesWhitespaceAroundCoordinates() {
            when(analyzer.analyze(any(), any(), any())).thenReturn(fakeResult);
            when(formatter.format(any())).thenReturn("FORMATTED");

            commands.analyze(" 0 , 0 , 10 , 10 , 5 , 5 , 15 , 15 ", null, false);

            verify(analyzer).analyze(eq(Rectangle.of(0, 0, 10, 10)),
                                     eq(Rectangle.of(5, 5, 15, 15)),
                                     any());
        }

        @Test
        void drawingAppendsPictureFromDrawerWhenEnabled() {
            when(analyzer.analyze(any(), any(), any())).thenReturn(fakeResult);
            when(formatter.format(any())).thenReturn("FORMATTED");
            when(drawer.draw(any(), any())).thenReturn("PICTURE");

            String out = commands.analyze("0,0,10,10,5,5,15,15", null, true);

            assertThat(out).isEqualTo("FORMATTED\n\nPICTURE");
            verify(drawer).draw(Rectangle.of(0, 0, 10, 10), Rectangle.of(5, 5, 15, 15));
        }

        @Test
        void drawingIsSkippedWhenDisabled() {
            when(analyzer.analyze(any(), any(), any())).thenReturn(fakeResult);
            when(formatter.format(any())).thenReturn("FORMATTED");

            String out = commands.analyze("0,0,10,10,5,5,15,15", null, false);

            assertThat(out).isEqualTo("FORMATTED");
            verifyNoInteractions(drawer);
        }

        @Test
        void rejectsBlankRectanglesArgument() {
            String out = commands.analyze("   ", null, true);
            assertThat(out).startsWith("Error:").contains("required");
            verifyNoInteractions(analyzer, formatter, drawer);
        }

        @Test
        void rejectsWrongNumberOfCoordinates() {
            String out = commands.analyze("0,0,1,1", null, true);
            assertThat(out).startsWith("Error:").contains("8 comma-separated integers");
            verifyNoInteractions(analyzer, formatter, drawer);
        }

        @Test
        void rejectsNonIntegerCoordinate() {
            String out = commands.analyze("0,0,10,ten,5,5,15,15", null, true);
            assertThat(out).startsWith("Error:").contains("ten");
            verifyNoInteractions(analyzer, formatter, drawer);
        }

        @Test
        void rejectsDegenerateRectangle() {
            // Zero-width rectangle in A.
            String out = commands.analyze("3,0,3,5,1,1,2,2", null, true);
            assertThat(out).startsWith("Error:").contains("width must be greater than 0");
            verifyNoInteractions(analyzer, formatter, drawer);
        }

        @Test
        void rejectsUnknownAnalysisType() {
            String out = commands.analyze("0,0,10,10,5,5,15,15", "intersection,bogus", true);
            assertThat(out).startsWith("Error:").contains("Unknown analysis type 'bogus'");
            verifyNoInteractions(analyzer, formatter, drawer);
        }
    }

    // ---------- demo ----------

    @Nested
    class DemoCommand {

        private final Scenario disjoint = new Scenario(
                "disjoint", "two disjoint rectangles",
                Rectangle.of(0, 0, 5, 5), Rectangle.of(10, 10, 15, 15));

        private final Scenario overlap = new Scenario(
                "overlap", "two overlapping rectangles",
                Rectangle.of(0, 0, 10, 10), Rectangle.of(5, 5, 15, 15));

        private Map<String, Scenario> registry() {
            Map<String, Scenario> map = new LinkedHashMap<>();
            map.put(disjoint.name(), disjoint);
            map.put(overlap.name(), overlap);
            return map;
        }

        @Test
        void listOptionPrintsScenarioNamesAndDescriptionsWithoutRunningThem() {
            when(scenarios.all()).thenReturn(registry());

            String out = commands.demo(null, true, true);

            assertThat(out)
                    .startsWith("Available scenarios:")
                    .contains("disjoint")
                    .contains("two disjoint rectangles")
                    .contains("overlap")
                    .contains("two overlapping rectangles");
            verifyNoInteractions(analyzer, formatter, drawer);
        }

        @Test
        void namedScenarioRunsOnlyThatScenario() {
            when(scenarios.get("overlap")).thenReturn(overlap);
            when(analyzer.analyze(overlap.a(), overlap.b()))
                    .thenReturn(stubResult(overlap));
            when(formatter.format(any())).thenReturn("OVERLAP-FORMATTED");

            String out = commands.demo("overlap", false, false);

            assertThat(out)
                    .contains("Scenario: overlap")
                    .contains("two overlapping rectangles")
                    .contains("OVERLAP-FORMATTED");
            verify(analyzer).analyze(overlap.a(), overlap.b());
            verify(scenarios, never()).all();
            verifyNoInteractions(drawer);
        }

        @Test
        void namedScenarioTrimsWhitespace() {
            when(scenarios.get("overlap")).thenReturn(overlap);
            when(analyzer.analyze(any(), any())).thenReturn(stubResult(overlap));
            when(formatter.format(any())).thenReturn("OK");

            commands.demo("  overlap  ", false, false);

            verify(scenarios).get("overlap");
        }

        @Test
        void namedScenarioReturnsErrorWhenNameUnknown() {
            when(scenarios.get("nope"))
                    .thenThrow(new IllegalArgumentException("Unknown scenario 'nope'"));

            String out = commands.demo("nope", false, true);

            assertThat(out).startsWith("Error:").contains("nope");
            verifyNoInteractions(analyzer, formatter, drawer);
        }

        @Test
        void noArgumentsRunsEveryScenarioAndJoinsThemWithSeparators() {
            when(scenarios.all()).thenReturn(registry());
            when(analyzer.analyze(any(), any()))
                    .thenReturn(stubResult(disjoint))
                    .thenReturn(stubResult(overlap));
            when(formatter.format(any()))
                    .thenReturn("BODY-1")
                    .thenReturn("BODY-2");

            String out = commands.demo(null, false, false);

            assertThat(out)
                    .contains("Scenario: disjoint")
                    .contains("BODY-1")
                    .contains("Scenario: overlap")
                    .contains("BODY-2")
                    .contains("-".repeat(72));
            verify(analyzer).analyze(disjoint.a(), disjoint.b());
            verify(analyzer).analyze(overlap.a(), overlap.b());
            verifyNoInteractions(drawer);
        }

        @Test
        void blankNameFallsThroughToAllScenarios() {
            when(scenarios.all()).thenReturn(registry());
            when(analyzer.analyze(any(), any())).thenReturn(stubResult(disjoint));
            when(formatter.format(any())).thenReturn("BODY");

            String out = commands.demo("   ", false, false);

            assertThat(out)
                    .contains("Scenario: disjoint")
                    .contains("Scenario: overlap");
        }

        @Test
        void drawingAppendsPictureForNamedScenarioWhenEnabled() {
            when(scenarios.get("overlap")).thenReturn(overlap);
            when(analyzer.analyze(overlap.a(), overlap.b())).thenReturn(stubResult(overlap));
            when(formatter.format(any())).thenReturn("OVERLAP-FORMATTED");
            when(drawer.draw(overlap.a(), overlap.b())).thenReturn("OVERLAP-PICTURE");

            String out = commands.demo("overlap", false, true);

            assertThat(out)
                    .contains("Scenario: overlap")
                    .contains("OVERLAP-FORMATTED")
                    .endsWith("OVERLAP-PICTURE");
            verify(drawer).draw(overlap.a(), overlap.b());
        }

        @Test
        void drawingAppendsPicturesForEveryScenarioWhenEnabled() {
            when(scenarios.all()).thenReturn(registry());
            when(analyzer.analyze(any(), any()))
                    .thenReturn(stubResult(disjoint))
                    .thenReturn(stubResult(overlap));
            when(formatter.format(any()))
                    .thenReturn("BODY-1")
                    .thenReturn("BODY-2");
            when(drawer.draw(disjoint.a(), disjoint.b())).thenReturn("PIC-1");
            when(drawer.draw(overlap.a(), overlap.b())).thenReturn("PIC-2");

            String out = commands.demo(null, false, true);

            assertThat(out)
                    .contains("BODY-1")
                    .contains("PIC-1")
                    .contains("BODY-2")
                    .contains("PIC-2");
            verify(drawer).draw(disjoint.a(), disjoint.b());
            verify(drawer).draw(overlap.a(), overlap.b());
        }

        private RectangleAnalysisResult stubResult(Scenario s) {
            return new RectangleAnalysisResult(
                    s.a(), s.b(), List.of(), Containment.NONE, Adjacency.NONE);
        }
    }
}
