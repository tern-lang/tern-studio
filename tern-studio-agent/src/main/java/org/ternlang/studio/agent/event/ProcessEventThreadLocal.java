package org.ternlang.studio.agent.event;

import java.util.Arrays;

import org.ternlang.agent.message.event.ProcessEventCodec;
import org.ternlang.message.ArrayByteBuffer;
import org.ternlang.studio.agent.event.ProcessEventThreadLocal.ProcessEventSender;

public class ProcessEventThreadLocal extends ThreadLocal<ProcessEventSender> {

   @Override
   public ProcessEventSender initialValue() {      
      return new ProcessEventSender().start();
   }   
   
   public static class ProcessEventSender extends ProcessEventCodec {
      
      private final ArrayByteBuffer frame;
      
      public ProcessEventSender() {
         this.frame = new ArrayByteBuffer();
      }
      
      public ProcessEventSender start() {
         frame.setCount(16);
         with(frame, 0, Integer.MAX_VALUE);
         return this;
      }
      
      @Override
      public ProcessEventSender clear() {
         frame.clear();
         super.clear();
         return this;
      }
      
      public MessageEnvelope envelope() {
         int length = frame.length();
         byte[] array = frame.getByteArray();
         byte[] copy = Arrays.copyOf(array, length); // copy if async
         
         return new MessageEnvelope(0, copy, 0, length);
      }
   }
}
