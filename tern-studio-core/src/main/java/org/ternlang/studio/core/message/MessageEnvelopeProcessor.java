package org.ternlang.studio.core.message;

import org.ternlang.studio.agent.event.MessageEnvelope;
import org.ternlang.studio.agent.event.ProcessEventChannel;

public interface MessageEnvelopeProcessor {
   void process(ProcessEventChannel channel, MessageEnvelope message) throws Exception;
}