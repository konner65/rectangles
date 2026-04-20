package org.konner.rectangles.service;

import org.konner.rectangles.model.*;

import java.util.List;
import java.util.Set;

public interface RectangleAnalysisService {
    List<Point> intersection(Rectangle r1, Rectangle r2);
    Containment containment(Rectangle r1, Rectangle r2);
    Adjacency adjacency(Rectangle r1, Rectangle r2);
    RectangleAnalysisResult analyze(Rectangle r1, Rectangle r2);
    RectangleAnalysisResult analyze(Rectangle r1, Rectangle r2, Set<AnalysisType> types);
}
