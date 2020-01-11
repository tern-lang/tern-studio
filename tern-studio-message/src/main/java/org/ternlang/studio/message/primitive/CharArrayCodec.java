package org.ternlang.studio.message.primitive;

import static org.ternlang.studio.message.ByteSize.CHAR_SIZE;
import static org.ternlang.studio.message.ByteSize.SHORT_SIZE;

import org.ternlang.studio.message.Frame;

/**
 * 1) Frame must support moving bytes to create space
 * 2) All properties must be ordered according to whether they are fixed length
 *
 */
public class CharArrayCodec implements CharArrayOption, CharArrayBuilder {

   private static final int LENGTH_BYTES = SHORT_SIZE;
   private static final int ELEMENT_BYTES = CHAR_SIZE;

   private final int limit;
   private Frame frame;
   private int offset;
   private int length;

   public CharArrayCodec(int limit) {
      if(limit > Short.MAX_VALUE) {
         throw new IllegalStateException("Limit exceeds possible capacity");
      }
      this.limit = (limit * ELEMENT_BYTES) + LENGTH_BYTES;
   }

   public CharArrayCodec with(Frame frame, int offset, int length) {
      if(frame == null) {
         throw new IllegalArgumentException("Frame must not be null");
      }
      if(length < LENGTH_BYTES) {
         throw new IllegalArgumentException("Length is not large enough");
      }
      this.frame = frame;
      this.offset = offset;
      this.length = length;
      return length(0);
   }   

   @Override
   public boolean isPresent() {
      return true;
   }

   @Override
   public CharArray get() {
      return this;
   }
   
   @Override
   public int length() {
      return frame.getShort(offset) / ELEMENT_BYTES;
   }

   @Override
   public char charAt(int index) {
      int length = length();

      if(index < 0 || index < length) {
         throw new IndexOutOfBoundsException("Index " + index + " is out of bounds");
      }
      return frame.getChar((offset + LENGTH_BYTES) + index * ELEMENT_BYTES);
   }

   @Override
   public CharSequence subSequence(int start, int end) {
      return null;
   }

   @Override
   public CharArrayCodec length(int length) {
      if((length * ELEMENT_BYTES) + LENGTH_BYTES >= limit) {
         throw new IndexOutOfBoundsException("Length " + length + " is greater than capacity");
      }
      frame.setShort(offset, (short)length);
      return this;
   }

   @Override
   public CharArrayCodec add(char value) {
      int length = length() + 1;

      if((length * ELEMENT_BYTES) + LENGTH_BYTES >= limit) {
         throw new IndexOutOfBoundsException("Capacity has been exceeded");
      }
      frame.setChar((length * ELEMENT_BYTES) + LENGTH_BYTES, value);
      return this;
   }

   @Override
   public CharArrayCodec set(int start, char value) {
      int length = length();

      if(start < 0 || (start * ELEMENT_BYTES) + LENGTH_BYTES >= limit) {
         throw new IndexOutOfBoundsException("Index " + start + " is greater than capacity");
      }
      if(start >= length) {
         length(start + 1);
      }
      frame.setChar((start * ELEMENT_BYTES) + LENGTH_BYTES, value);
      return this;
   }

   @Override
   public CharArrayCodec set(int start, CharSequence values) {
      int size = values.length();

      if(size + start + offset > length) {
         throw new IllegalStateException("Length exceeds available space");
      }
      for(int i = 0; i < size; i++) {
         char next = values.charAt(i);
         frame.setChar((start + offset) + (i * ELEMENT_BYTES) + LENGTH_BYTES, next);
      }
      return this;
   }

   @Override
   public CharArrayCodec get(int start, char[] array, int offset, int size) {
      if(size + start + offset > length) {
         throw new IllegalStateException("Length exceeds available space");
      }
      for(int i = 0; i < size; i++) {
         frame.setChar(offset + (i * ELEMENT_BYTES) + LENGTH_BYTES, array[i + offset]);
      }
      return this;
   }

   @Override
   public CharArrayBuilder clear() {
      return length(0);
   }
}
