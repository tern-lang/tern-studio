package org.ternlang.studio.service.json.operation;

import org.ternlang.studio.service.json.Slice;
import org.ternlang.studio.service.json.handler.Name;

public class Type extends Name {

   private final Slice slice;  
   
   public Type() {
      this.slice = new Slice();
   }
   
   @Override
   public Slice toToken() {
      return slice;
   }
   
   public Type with(char[] source, int off, int length) {
      slice.with(source, off, length);
      hash = 0;
      return this;
   }
   
   public boolean isEmpty() {
      return slice.length() <= 0;
   }
   
   public void reset() {
      slice.reset();
   }
   
   @Override
   public String toString() {
      return slice.toString();
   }
}
