package org.ternlang.studio.agent.event;

public class MessageEnvelope {
   
   private final byte[] data;
   private final int offset;
   private final int length;
   private final int code;
   
   public MessageEnvelope(int code, byte[] data, int offset, int length) {
      this.offset = offset;
      this.length = length;
      this.data = data;
      this.code = code;
   }
   
   public int getCode() {
      return code;
   }
   
   public byte[] getData() {
      return data;
   }
   
   public int getLength() {
      return length;
   }
   
   public int getOffset() {
      return offset;
   }
}