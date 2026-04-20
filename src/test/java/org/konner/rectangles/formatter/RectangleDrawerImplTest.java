package org.konner.rectangles.formatter;

import org.junit.jupiter.api.Test;
import org.konner.rectangles.model.Rectangle;

import static org.assertj.core.api.Assertions.assertThat;

class RectangleDrawerImplTest {

    private static final String LEGEND =
            "Legend: a = rect A boundary, b = rect B boundary, # = both, . = empty";

    private final RectangleDrawerImpl drawer = new RectangleDrawerImpl();

    // ---------- structural invariants ----------

    @Test
    void legendIsAlwaysAppendedAsTheLastLine() {
        String out = drawer.draw(Rectangle.of(0, 0, 5, 5), Rectangle.of(10, 10, 15, 15));
        assertThat(out).endsWith(LEGEND);
    }

    @Test
    void gridRowsAllHaveSameWidth() {
        String[] rows = gridRows(drawer.draw(Rectangle.of(0, 0, 10, 10), Rectangle.of(5, 5, 15, 15)));
        int width = rows[0].length();
        assertThat(rows).allSatisfy(row -> assertThat(row).hasSize(width));
    }

    @Test
    void gridOnlyContainsExpectedCharacters() {
        String[] rows = gridRows(drawer.draw(Rectangle.of(0, 0, 10, 10), Rectangle.of(5, 5, 15, 15)));
        for (String row : rows) {
            assertThat(row).matches("[ab#.]+");
        }
    }

    @Test
    void veryLargeRectanglesAreScaledToFitWithinMaxDimensions() {
        // 1000-unit squares would render to hundreds of cells without scaling.
        String out = drawer.draw(Rectangle.of(0, 0, 1000, 1000), Rectangle.of(500, 500, 1500, 1500));
        String[] rows = gridRows(out);
        assertThat(rows.length).isLessThanOrEqualTo(24);
        for (String row : rows) {
            assertThat(row.length()).isLessThanOrEqualTo(60);
        }
    }

    @Test
    void gridInteriorsAreEmpty() {
        // Perimeter-only rendering: with two disjoint rectangles the space
        // inside each rectangle is all '.'.
        String grid = onlyGrid(drawer.draw(Rectangle.of(0, 0, 10, 10), Rectangle.of(20, 0, 30, 10)));
        // Sample a cell that must lie strictly inside rectangle A (5, 5) — it
        // should be '.'. Given the drawer's layout (margin=1, 2 cols/unit,
        // 1 row/unit), the centre of A is at row ≈ (11-5)=6, col ≈ (5-(-1))*2=12.
        String[] rows = grid.split("\n");
        assertThat(rows[6].charAt(12)).isEqualTo('.');
    }

    // ---------- relationship-specific cell-type assertions ----------

    @Test
    void overlapProducesAllFourCellTypes() {
        // Overlapping rectangles cross at two points → two '#' cells, plus
        // surviving 'a' and 'b' edges, plus empty cells outside both.
        String grid = onlyGrid(drawer.draw(Rectangle.of(0, 0, 10, 10), Rectangle.of(5, 5, 15, 15)));
        assertThat(grid).contains("a").contains("b").contains("#").contains(".");
    }

    @Test
    void containmentHasNoSharedBoundary() {
        // B sits strictly inside A with no boundary contact, so the two
        // outlines do not touch each other — there is no '#' cell.
        String grid = onlyGrid(drawer.draw(Rectangle.of(0, 0, 20, 20), Rectangle.of(5, 5, 15, 15)));
        assertThat(grid).contains("a").contains("b").doesNotContain("#");
    }

    @Test
    void disjointHasNoSharedCells() {
        String grid = onlyGrid(drawer.draw(Rectangle.of(0, 0, 5, 5), Rectangle.of(10, 10, 15, 15)));
        assertThat(grid).contains("a").contains("b").doesNotContain("#");
    }

    @Test
    void equalRectanglesProduceOnlySharedBoundary() {
        // Identical outlines → every perimeter cell belongs to both rectangles.
        String grid = onlyGrid(drawer.draw(Rectangle.of(0, 0, 5, 5), Rectangle.of(0, 0, 5, 5)));
        assertThat(grid).contains("#").doesNotContain("a").doesNotContain("b");
    }

    @Test
    void cornerTouchProducesExactlyOneBothCell() {
        // Rectangles meet at a single corner (5, 5) — exactly one '#' cell.
        String grid = onlyGrid(drawer.draw(Rectangle.of(0, 0, 5, 5), Rectangle.of(5, 5, 10, 10)));
        long hashes = grid.chars().filter(ch -> ch == '#').count();
        assertThat(hashes).isEqualTo(1L);
    }

    @Test
    void properAdjacencyShowsAFullColumnOfBothCells() {
        // A and B share the full vertical edge at x=10, y in [0, 10].
        String grid = onlyGrid(drawer.draw(Rectangle.of(0, 0, 10, 10), Rectangle.of(10, 0, 20, 10)));
        String[] rows = grid.split("\n");
        int sharedCol = rows[rows.length / 2].indexOf('#');
        assertThat(sharedCol).isGreaterThanOrEqualTo(0);
        long hashRowCount = 0;
        for (String row : rows) {
            if (row.charAt(sharedCol) == '#') hashRowCount++;
        }
        // 11 rows for y ∈ [0, 10] with 1 row per unit.
        assertThat(hashRowCount).isEqualTo(11L);
    }

    @Test
    void tinyRectangleCollapsesToASingleLetter() {
        // A much larger A relative to B forces B to scale down to (effectively)
        // a single cell in the rendered grid. B is entirely inside A with no
        // boundary contact, so B should appear as exactly one 'b' cell and
        // there should be no '#' cells.
        String grid = onlyGrid(drawer.draw(Rectangle.of(0, 0, 500, 500), Rectangle.of(250, 250, 251, 251)));
        long bCells = grid.chars().filter(ch -> ch == 'b').count();
        assertThat(bCells).isEqualTo(1L);
        assertThat(grid).doesNotContain("#");
    }

    // ---------- helpers ----------

    private static String[] gridRows(String out) {
        int legendIdx = out.lastIndexOf('\n');
        return out.substring(0, legendIdx).split("\n");
    }

    private static String onlyGrid(String out) {
        int legendIdx = out.lastIndexOf('\n');
        return out.substring(0, legendIdx);
    }
}
