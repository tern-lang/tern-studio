package org.ternlang.studio.service.json.operation;

import org.ternlang.studio.service.json.handler.AttributeHandler;

public class ArrayEnd extends Operation {

   private final OperationPool pool;

   public ArrayEnd(OperationPool pool) {
      this.pool = pool;
   }

   @Override
   public void execute(AttributeHandler handler) {
      handler.onArrayEnd();
      
      if(pool != null) {
         pool.recycle(this);
      }
   }

   @Override
   public void reset() {}
}
