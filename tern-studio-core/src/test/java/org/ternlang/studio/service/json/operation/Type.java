package org.ternlang.studio.service.json.operation;

import org.ternlang.studio.service.json.common.Slice;
import org.ternlang.studio.service.json.handler.Name;

public class Type extends Name implements AutoCloseable {

   private final OperationPool pool;
   private final Slice slice;  
   
   public Type(OperationPool pool) {
      this.slice = new Slice();
      this.pool = pool;
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
   
   @Override
   public boolean isEmpty() {
      return slice.length() <= 0;
   }
   
   public void reset() {
      slice.reset();
   }
   
   @Override
   public void close() {
      if(pool != null) {
         pool.recycle(this);
      }
   }
   
   @Override
   public String toString() {
      return slice.toString();
   }
}
