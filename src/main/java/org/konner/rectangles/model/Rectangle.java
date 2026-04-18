package org.konner.rectangles.model;

import lombok.Data;
import org.konner.rectangles.exception.InvalidRectangleException;

@Data
public class Rectangle {
    private final int left;
    private final int right;
    private final int top;
    private final int bottom;

    //todo the constructor should validate that it is a valid rectangle
    public Rectangle(Point p1, Point p2) {
        if (p1.x() == p2.x() || p1.y() == p2.y()) {
            throw new InvalidRectangleException("Invalid Rectangle: the 2 points cannot share the same x or y coordinate");
        }

        this.left = Math.min(p1.x(), p2.x());
        this.right = Math.max(p1.x(), p2.x());
        this.top = Math.max(p1.y(), p2.y());
        this.bottom = Math.min(p1.y(), p2.y());
    }

    public boolean contains(Rectangle r) {
        return this.left <= r.getLeft() &&
                this.bottom <= r.getBottom() &&
                this.right >= r.getRight() &&
                this.top >= r.getTop();
    }

    public static Rectangle of(int x1, int y1, int x2, int y2) {
        return new Rectangle(new Point(x1, y1), new Point(x2, y2));
    }

}
