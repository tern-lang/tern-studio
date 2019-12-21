package org.ternlang.studio.service.json.operation;

import org.ternlang.studio.service.json.document.DocumentHandler;
import org.ternlang.studio.service.json.document.Name;
import org.ternlang.studio.service.json.document.TextSlice;

public abstract class Operation {

   public boolean isBegin() {
      return false;
   }

   public boolean isEnd() {
      return false;
   }
   
   public abstract void execute(DocumentHandler handler);
   public abstract void reset();

   
   protected static class NameSlice extends Name {
      
      private final TextSlice slice = new TextSlice();
      
      @Override
      public CharSequence toToken() {
         return slice;
      }
      
      public NameSlice with(char[] source, int off, int length) {
         slice.with(source, off, length);
         hash = 0;
         return this;
      }
      
      @Override
      public boolean isEmpty() {
         return slice.isEmpty();
      }
      
      public void reset() {
         slice.reset();
      }
      
      @Override
      public String toString() {
         return slice.toString();
      }
   }
}
