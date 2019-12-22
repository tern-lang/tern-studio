package org.ternlang.studio.common.json.document;

public class SourceSlice implements CharSequence {

   private char[] source;
   private int off;
   private int length;
   private int hash;

   public SourceSlice() {
      super();
   }

   public SourceSlice with(char[] source, int off, int length) {
      this.source = source;
      this.off = off;
      this.length = length;
      return this;
   }

   public char[] source() {
      return source;
   }

   public int offset() {
      return off;
   }

   @Override
   public int length() {
      return length;
   }

   @Override
   public char charAt(int index) {
      return source[off + index];
   }

   @Override
   public CharSequence subSequence(int start, int end) {
      SourceSlice slice = new SourceSlice();

      if(end - start > 0) {
         return slice.with(source, off + start, end - start);
      }
      return slice;
   }

   @Override
   public int hashCode() {
      int local = hash;

      if(local == 0 && length > 0) {
         for(int i = 0; i < length; i++) {
            local = 31 * local + source[off + i];
         }
         hash = local;
      }
      return local;
   }

   @Override
   public boolean equals(Object other) {
      if(other == null) {
         return false;
      }
      if(other == this) {
         return true;
      }
      if(other instanceof CharSequence) {
         CharSequence text = (CharSequence) other;
         int size = text.length();

         if(size != length) {
            return false;
         }
         for(int i = 0; i < size; i++) {
            char next = text.charAt(i);

            if(next != source[off + i]) {
               return false;
            }
         }
         return true;
      }
      return false;
   }

   public boolean isEmpty() {
      return length <= 0;
   }

   public void reset() {
      off = length = hash = 0;
      source = null;
   }

   @Override
   public String toString() {
      return new String(source, off, length);
   }
}
