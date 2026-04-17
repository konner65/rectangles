package org.konner.rectangles.service;

import org.konner.rectangles.model.*;

//todo implement interface as spring boot component
public interface RectangleAnalysisService {
    Intersection intersection(Rectangle r1, Rectangle r2);
    Containment containment(Rectangle r1, Rectangle r2);
    Adjacency adjacency(Rectangle r1, Rectangle r2);
    RectangleAnalysisResult analyze(Rectangle r1, Rectangle r2);
}
