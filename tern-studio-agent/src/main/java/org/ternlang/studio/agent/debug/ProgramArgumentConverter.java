package org.ternlang.studio.agent.debug;

import org.ternlang.agent.message.common.ProgramArgumentArray;
import org.ternlang.agent.message.common.ProgramArgumentArrayCodec;
import org.ternlang.message.ArrayByteBuffer;

import java.util.List;

public class ProgramArgumentConverter {

    public static ProgramArgumentArray convert(List<String> arguments) {
        ArrayByteBuffer frame = new ArrayByteBuffer();
        ProgramArgumentArrayCodec codec = new ProgramArgumentArrayCodec(Integer.MAX_VALUE);

        codec.with(frame, 0, Integer.MAX_VALUE);
        frame.setCount(8);

        for(String argument : arguments) {
            codec.add().argument(argument);
        }
        return codec;
    }
}
