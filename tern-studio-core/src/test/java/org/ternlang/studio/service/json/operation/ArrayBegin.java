package org.ternlang.studio.service.json.operation;

import org.ternlang.studio.service.json.document.DocumentHandler;

public class ArrayBegin extends Operation {
   
   private final OperationPool pool;
   private final NameValue name;

   public ArrayBegin(OperationPool pool) {
      this.name = new NameValue();
      this.pool = pool;
   }

   @Override
   public void execute(DocumentHandler handler) {
      handler.arrayBegin(name);
      
      if(pool != null) {
         pool.recycle(this);
      }
   }
   
   public void name(char[] source, int off, int length) {
      name.with(source, off, length);
   }

   @Override
   public void reset() {
      name.reset();
   }
}
 