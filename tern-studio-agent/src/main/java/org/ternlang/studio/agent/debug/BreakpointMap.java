package org.ternlang.studio.agent.debug;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BreakpointMap {

    private final Map<String, Map<Integer, Boolean>> breakpoints;

    public BreakpointMap() {
        this.breakpoints = new LinkedHashMap<String, Map<Integer, Boolean>>();
    }

    public Map<String, Map<Integer, Boolean>> getBreakpoints() {
        return Collections.unmodifiableMap(breakpoints);
    }

    public void add(String resource, int line) {
        Map<Integer, Boolean> lines = breakpoints.get(resource);

        if(lines == null) {
            lines = new HashMap<Integer, Boolean>();
            breakpoints.put(resource, lines);
        }
        lines.put(line, Boolean.TRUE);
    }

    public void remove(String resource, int line) {
        Map<Integer, Boolean> lines = breakpoints.get(resource);

        if(lines == null) {
            lines = new HashMap<Integer, Boolean>();
            breakpoints.put(resource, lines);
        }
        lines.put(line, Boolean.FALSE);
    }
}