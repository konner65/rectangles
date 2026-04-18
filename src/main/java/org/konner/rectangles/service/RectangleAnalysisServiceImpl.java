package org.konner.rectangles.service;

import org.konner.rectangles.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.konner.rectangles.model.Containment.*;

@Component
public class RectangleAnalysisServiceImpl implements RectangleAnalysisService{
    @Override
    public List<Point> intersection(Rectangle r1, Rectangle r2) {
        //todo complete method. Return empty list if no intersection
        return null;
    }

    @Override
    public Containment containment(Rectangle r1, Rectangle r2) {
        if(r1.equals(r2)) {
            return EQUAL;
        }
        if(r1.contains(r2)) {
            return A_CONTAINS_B;
        }
        if(r2.contains(r1)) {
            return B_CONTAINS_A;
        }
        return NONE;
    }

    @Override
    public Adjacency adjacency(Rectangle r1, Rectangle r2) {
        //todo complete method
        return null;
    }

    @Override
    public RectangleAnalysisResult analyze(Rectangle r1, Rectangle r2) {
        List<Point> intersection = intersection(r1, r2);
        Containment containment = containment(r1, r2);
        Adjacency adjacency = adjacency(r1, r2);
        return new RectangleAnalysisResult(r1, r2, intersection, containment, adjacency);
    }
}
