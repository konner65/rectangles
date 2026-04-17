package org.konner.rectangles.model;

public record Rectangle(Point bottomLeft, Point topRight) {
    //todo
    public static Rectangle of(int x1, int y1, int x2, int y2) {
        return new Rectangle(new Point(x1, y1), new Point(x2, y2));
    }

}
