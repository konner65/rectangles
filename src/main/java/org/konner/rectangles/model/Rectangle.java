package org.konner.rectangles.model;

import org.konner.rectangles.exception.InvalidRectangleException;

import java.util.Objects;

public final class Rectangle {
    private final int left;
    private final int right;
    private final int top;
    private final int bottom;

    public Rectangle(Point p1, Point p2) {
        if (p1 == null || p2 == null) {
            throw new InvalidRectangleException("Invalid Rectangle: points cannot be null");
        }

        this.left = Math.min(p1.x(), p2.x());
        this.right = Math.max(p1.x(), p2.x());
        this.bottom = Math.min(p1.y(), p2.y());
        this.top = Math.max(p1.y(), p2.y());

        if (right - left <= 0) {
            throw new InvalidRectangleException(
                    "Invalid Rectangle: width must be greater than 0 "
                            + "(got left=" + left + ", right=" + right + " from " + p1 + " and " + p2 + ").");
        }
        if (top - bottom <= 0) {
            throw new InvalidRectangleException(
                    "Invalid Rectangle: height must be greater than 0 "
                            + "(got bottom=" + bottom + ", top=" + top + " from " + p1 + " and " + p2 + ").");
        }
    }

    public int getLeft() { return left; }
    public int getRight() { return right; }
    public int getTop() { return top; }
    public int getBottom() { return bottom; }

    public boolean contains(Rectangle r) {
        return this.left <= r.left
                && this.bottom <= r.bottom
                && this.right >= r.right
                && this.top >= r.top;
    }

    public static Rectangle of(int x1, int y1, int x2, int y2) {
        return new Rectangle(new Point(x1, y1), new Point(x2, y2));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rectangle other)) return false;
        return left == other.left
                && right == other.right
                && top == other.top
                && bottom == other.bottom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right, top, bottom);
    }

    @Override
    public String toString() {
        return "Rectangle[(" + left + "," + bottom + ")-(" + right + "," + top + ")]";
    }
}
