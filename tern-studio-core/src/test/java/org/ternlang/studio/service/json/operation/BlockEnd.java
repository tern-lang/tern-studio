package org.ternlang.studio.service.json.operation;

import org.ternlang.studio.service.json.document.DocumentHandler;

public class BlockEnd extends Operation {

   private final OperationPool pool;

   public BlockEnd(OperationPool pool) {
      this.pool = pool;
   }

   @Override
   public void execute(DocumentHandler handler) { 
      handler.onBlockEnd();
      
      if(pool != null) {
         pool.recycle(this);
      }
   }

   @Override
   public void reset() {}
   
   @Override
   public boolean isEnd() {
      return true;
   }
   
}
