package org.ternlang.studio.service.json;

public class Slice implements CharSequence {

   private char[] source;
   private int off;
   private int length;
   
   public Slice with(char[] source, int off, int length) {
      this.source = source;
      this.off = off;
      this.length = length;
      return this;
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
      Slice slice = new Slice();
            
      if(end - start > 0) {      
         return slice.with(source, off + start, end - start);
      }
      return slice;
   }
   
   @Override
   public String toString() {
      return new String(source, off, length);
   }
}
