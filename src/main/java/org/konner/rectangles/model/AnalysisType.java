package org.konner.rectangles.model;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public enum AnalysisType {
    INTERSECTION,
    CONTAINMENT,
    ADJACENCY;

    public static Set<AnalysisType> all() {
        return EnumSet.allOf(AnalysisType.class);
    }

    public static Set<AnalysisType> parse(String csv) {
        if (csv == null || csv.isBlank()) {
            return all();
        }
        EnumSet<AnalysisType> out = EnumSet.noneOf(AnalysisType.class);
        for (String token : csv.split(",")) {
            String t = token.trim();
            if (t.isEmpty()) continue;
            try {
                out.add(AnalysisType.valueOf(t.toUpperCase().replace('-', '_')));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(
                        "Unknown analysis type '" + t + "'. Valid values: "
                                + Arrays.toString(AnalysisType.values()));
            }
        }
        return out.isEmpty() ? all() : out;
    }
}
