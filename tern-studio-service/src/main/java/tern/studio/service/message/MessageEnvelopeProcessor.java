package tern.studio.service.message;

import tern.studio.agent.event.MessageEnvelope;
import tern.studio.agent.event.ProcessEventChannel;

public interface MessageEnvelopeProcessor {
   void process(ProcessEventChannel channel, MessageEnvelope message) throws Exception;
}