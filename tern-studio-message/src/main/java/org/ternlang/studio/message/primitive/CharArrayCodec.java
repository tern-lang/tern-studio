package org.ternlang.studio.message.primitive;

import org.ternlang.studio.message.ByteSize;
import org.ternlang.studio.message.Frame;

public class CharArrayCodec implements CharArrayOption, CharArrayBuilder {
   
   private Frame frame;
   private int offset;
   private int length;

   public CharArrayCodec with(Frame frame, int offset, int length) {
      this.frame = frame;
      this.offset = offset;
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
      return length / ByteSize.CHAR_SIZE;
   }

   @Override
   public char charAt(int index) {
      return frame.getChar(offset + index * ByteSize.CHAR_SIZE);
   }

   @Override
   public CharSequence subSequence(int start, int end) {
      return null;
   }

   @Override
   public CharArrayBuilder setLength(final int length) {
      frame.setInt(offset, length);
      return this;
   }

   @Override
   public CharArrayBuilder setChar(int start, char value) {
      frame.setChar(offset + start, value);
      return this;
   }

   @Override
   public CharArrayBuilder setChars(int start, CharSequence values) {
      int size = values.length();

      if(size + start + offset > length) {
         throw new IllegalStateException("Length exceeds available space");
      }
      for(int i = 0; i < size; i++) {
         char next = values.charAt(i);
         frame.setChar((start + offset) + (i * ByteSize.CHAR_SIZE), next);
      }
      return this;
   }

   @Override
   public CharArrayBuilder getChars(int start, char[] array, int offset, int size) {
      if(size + start + offset > length) {
         throw new IllegalStateException("Length exceeds available space");
      }
      for(int i = 0; i < size; i++) {
         frame.setChar(offset + (i * ByteSize.CHAR_SIZE), array[i + offset]);
      }
      return this;
   }
}
