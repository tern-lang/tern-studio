package org.ternlang.studio.service.json.operation;

import org.ternlang.studio.service.json.common.Slice;
import org.ternlang.studio.service.json.handler.AttributeHandler;
import org.ternlang.studio.service.json.handler.Name;

public abstract class Operation {

   public boolean isBegin() {
      return false;
   }

   public boolean isEnd() {
      return false;
   }
   
   public abstract void execute(AttributeHandler handler);
   public abstract void reset();

   
   protected static class NameSlice extends Name {
      
      private final Slice slice = new Slice();    
      
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
