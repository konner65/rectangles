package org.konner.rectangles.formatter;

import org.konner.rectangles.model.RectangleAnalysisResult;

//todo implement interface as spring boot component
public interface RectangleAnalysisFormatter {
    String format(RectangleAnalysisResult result);
}
