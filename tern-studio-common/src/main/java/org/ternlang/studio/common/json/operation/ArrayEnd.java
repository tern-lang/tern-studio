package org.ternlang.studio.common.json.operation;

import org.ternlang.studio.common.json.document.DocumentHandler;

public class ArrayEnd extends Operation {

   private final OperationPool pool;

   public ArrayEnd(OperationPool pool) {
      this.pool = pool;
   }

   @Override
   public void execute(DocumentHandler handler) {
      handler.arrayEnd();
      
      if(pool != null) {
         pool.recycle(this);
      }
   }

   @Override
   public void reset() {}
}