package org.ternlang.studio.agent.event;

import java.io.IOException;

public interface ProcessEventMarshaller<T extends ProcessEvent> {
   T fromMessage(MessageEnvelope message) throws IOException;
   MessageEnvelope toMessage(T value) throws IOException;
}