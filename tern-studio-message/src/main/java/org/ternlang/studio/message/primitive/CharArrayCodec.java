package org.ternlang.studio.message.primitive;

import org.ternlang.studio.message.Frame;

public class CharArrayCodec implements CharArrayOption, CharArrayBuilder {
   
   private Frame frame;
   private int off;
   private int length;

   public CharArrayCodec with(Frame frame, int off, int length) {
      this.frame = frame;
      this.off = off;
      this.length = length;
      return this;
   }   

   @Override
   public boolean isPresent() {
      return length > 0;
   }

   @Override
   public CharArray get() {
      return length > 0 ? this : null;
   }
   
   @Override
   public int length() {
      return length / 2; // 16 bits
   }

   @Override
   public char charAt(int index) {
      return frame.getChar(off + index * 2);
   }

   @Override
   public CharSequence subSequence(int start, int end) {
      return null;
   }
}
