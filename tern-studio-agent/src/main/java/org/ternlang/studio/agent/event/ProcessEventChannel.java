package org.ternlang.studio.agent.event;

import org.ternlang.agent.message.event.ProcessEventBuilder;

public interface ProcessEventChannel {
   ProcessEventBuilder begin() throws Exception;
   boolean send() throws Exception;
   boolean sendAsync() throws Exception;
   void close(String reason) throws Exception;
}