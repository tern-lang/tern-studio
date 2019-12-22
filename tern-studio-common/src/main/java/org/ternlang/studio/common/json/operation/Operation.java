package org.ternlang.studio.common.json.operation;

import org.ternlang.studio.common.json.document.DocumentHandler;
import org.ternlang.studio.common.json.document.Name;
import org.ternlang.studio.common.json.document.TextSlice;

public abstract class Operation {

   public boolean isBegin() {
      return false;
   }

   public boolean isEnd() {
      return false;
   }
   
   public abstract void execute(DocumentHandler handler);
   public abstract void reset();

   protected static class NameValue extends Name {
      
      private final TextSlice slice;

      public NameValue() {
         this.slice = new TextSlice();
      }

      public NameValue with(char[] source, int off, int length) {
         slice.with(source, off, length);
         hash = 0;
         return this;
      }

      @Override
      public CharSequence toText() {
         return slice;
      }
      
      @Override
      public boolean isEmpty() {
         return slice.isEmpty();
      }
      
      public void reset() {
         slice.reset();
         hash = 0;
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
}
