package org.ternlang.studio.agent.debug;

import org.ternlang.agent.message.common.Breakpoint;
import org.ternlang.agent.message.common.BreakpointArray;
import org.ternlang.agent.message.common.BreakpointArrayCodec;
import org.ternlang.agent.message.common.BreakpointCodec;
import org.ternlang.agent.message.common.Line;
import org.ternlang.agent.message.common.LineArray;
import org.ternlang.agent.message.common.LineArrayCodec;
import org.ternlang.message.ByteArrayFrame;

import java.util.Map;
import java.util.Set;

public class BreakpointConverter {

    public static BreakpointMap convert(BreakpointArray breakpoints) {
        BreakpointMap map = new BreakpointMap();

        for(Breakpoint breakpoint : breakpoints) {
            LineArray lines = breakpoint.lines();
            String resource = breakpoint.resource().toString();

            for(Line line : lines) {
                int number = line.line();

                if(line.active()) {
                    map.add(resource, number);
                }
            }
        }
        return map;
    }

    public static BreakpointArray convert(BreakpointMap breakpoints) {
        ByteArrayFrame frame = new ByteArrayFrame();
        BreakpointArrayCodec codec = new BreakpointArrayCodec(Integer.MAX_VALUE);

        codec.with(frame, 0, Integer.MAX_VALUE);
        frame.setCount(8);

        Set<Map.Entry<String, Map<Integer, Boolean>>> entries = breakpoints.getBreakpoints().entrySet();

        for (Map.Entry<String, Map<Integer, Boolean>> entry : entries) {
            String resource = entry.getKey();
            Set<Map.Entry<Integer, Boolean>> lines = entry.getValue().entrySet();
            BreakpointCodec breakpoint = codec.add();
            LineArrayCodec lineArray = breakpoint.lines();

            breakpoint.resource(resource);

            for(Map.Entry<Integer, Boolean> line : lines) {
                Integer number = line.getKey();
                Boolean value = line.getValue();

                if(value != null) {
                    lineArray.add()
                        .line(number)
                        .active(value);
                }
            }
        }
        return codec;
    }
}
