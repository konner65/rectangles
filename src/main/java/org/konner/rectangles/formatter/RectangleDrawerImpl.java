package org.konner.rectangles.formatter;

import org.konner.rectangles.model.Rectangle;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Renders two axis-aligned rectangles onto a simple character grid by drawing
 * only the perimeters (rectangle outlines).
 *
 * <p>Each cell is drawn as:
 * <pre>
 *   a — the cell sits on rectangle A's boundary only
 *   b — the cell sits on rectangle B's boundary only
 *   # — the cell sits on both rectangles' boundaries
 *   . — the cell is empty
 * </pre>
 *
 * <p>When a rectangle is so small relative to the overall bounding box that
 * it would render to fewer than two cells along either axis, it collapses to
 * a single letter at its centre.
 */
@Component
public class RectangleDrawerImpl implements RectangleDrawer {

    private static final int MAX_COLS = 60;
    private static final int MAX_ROWS = 24;
    private static final int MIN_COLS = 5;
    private static final int MIN_ROWS = 5;
    private static final int MARGIN = 1;

    // Terminal characters are roughly twice as tall as they are wide, so we
    // use twice as many columns per unit as rows per unit to preserve the
    // geometric aspect ratio of the rendered rectangles.
    private static final double CELLS_PER_UNIT_X = 2.0;
    private static final double CELLS_PER_UNIT_Y = 1.0;

    private static final char A_CHAR = 'a';
    private static final char B_CHAR = 'b';
    private static final char BOTH_CHAR = '#';
    private static final char EMPTY_CHAR = '.';

    @Override
    public String draw(Rectangle a, Rectangle b) {
        int minX = Math.min(a.getLeft(), b.getLeft()) - MARGIN;
        int maxX = Math.max(a.getRight(), b.getRight()) + MARGIN;
        int minY = Math.min(a.getBottom(), b.getBottom()) - MARGIN;
        int maxY = Math.max(a.getTop(), b.getTop()) + MARGIN;

        int spanX = maxX - minX;
        int spanY = maxY - minY;

        int desiredCols = (int) Math.round(spanX * CELLS_PER_UNIT_X) + 1;
        int desiredRows = (int) Math.round(spanY * CELLS_PER_UNIT_Y) + 1;

        double scale = Math.min(1.0,
                Math.min((double) MAX_COLS / desiredCols, (double) MAX_ROWS / desiredRows));
        int cols = Math.max(MIN_COLS, (int) Math.round(desiredCols * scale));
        int rows = Math.max(MIN_ROWS, (int) Math.round(desiredRows * scale));

        double stepX = (double) spanX / (cols - 1);
        double stepY = (double) spanY / (rows - 1);

        char[][] grid = new char[rows][cols];
        for (char[] row : grid) Arrays.fill(row, EMPTY_CHAR);

        drawPerimeter(grid, a, minX, maxY, stepX, stepY, A_CHAR);
        drawPerimeter(grid, b, minX, maxY, stepX, stepY, B_CHAR);

        StringBuilder sb = new StringBuilder((cols + 1) * rows + 80);
        for (char[] row : grid) {
            sb.append(row).append('\n');
        }
        sb.append("Legend: a = rect A boundary, b = rect B boundary, # = both, . = empty");
        return sb.toString();
    }

    private static void drawPerimeter(char[][] grid, Rectangle r,
                                      int minX, int maxY, double stepX, double stepY,
                                      char letter) {
        int c0 = (int) Math.round((r.getLeft() - minX) / stepX);
        int c1 = (int) Math.round((r.getRight() - minX) / stepX);
        int r0 = (int) Math.round((maxY - r.getTop()) / stepY);
        int r1 = (int) Math.round((maxY - r.getBottom()) / stepY);

        // If the rectangle collapses to fewer than two cells along either
        // axis (e.g. a tiny rectangle next to a very large one), reduce it
        // to a single representative letter at its centre.
        if (c1 - c0 < 1 || r1 - r0 < 1) {
            paint(grid, (r0 + r1) / 2, (c0 + c1) / 2, letter);
            return;
        }

        for (int c = c0; c <= c1; c++) {
            paint(grid, r0, c, letter);
            paint(grid, r1, c, letter);
        }
        for (int row = r0; row <= r1; row++) {
            paint(grid, row, c0, letter);
            paint(grid, row, c1, letter);
        }
    }

    private static void paint(char[][] grid, int row, int col, char letter) {
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length) return;
        char existing = grid[row][col];
        if (existing == EMPTY_CHAR) {
            grid[row][col] = letter;
        } else if (existing != letter) {
            grid[row][col] = BOTH_CHAR;
        }
    }
}
