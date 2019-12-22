package org.ternlang.studio.common.json.document;

public class SourceSlice implements CharSequence {

   private char[] source;
   private int off;
   private int length;
   
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
   
   public boolean isEmpty() {
      return length <= 0;
   }
   
   public void reset() {
      off = length = 0;
      source = null;
   }
   
   @Override
   public String toString() {
      return new String(source, off, length);
   }
}
