package tern.studio.agent.event;

public interface ProcessEventChannel {
   boolean send(ProcessEvent event) throws Exception;
   boolean sendAsync(ProcessEvent event) throws Exception;
   void close(String reason) throws Exception;
}