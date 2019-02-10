package tern.studio.agent.event;

import static tern.studio.agent.event.ProcessEventType.PROGRESS;

public class ProgressEventMarshaller extends PongEventMarshaller<ProgressEvent> {
   
   public ProgressEventMarshaller() {
      super(PROGRESS.code);
   }
   
   @Override
   protected ProgressEvent.Builder getBuilder(String process) {
      return new ProgressEvent.Builder(process);
   }
}