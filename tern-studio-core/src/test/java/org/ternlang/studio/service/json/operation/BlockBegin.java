package org.ternlang.studio.service.json.operation;

import org.ternlang.studio.service.json.document.DocumentHandler;
import org.ternlang.studio.service.json.document.Name;
import org.ternlang.studio.service.json.document.TextSlice;

public class BlockBegin extends Operation {
   
   private final OperationPool pool;
   private final TypeSlice type;
   private final NameSlice name;

   public BlockBegin(OperationPool pool) {
      this.name = new NameSlice();
      this.type = new TypeSlice();
      this.pool = pool;
   }

   @Override
   public void execute(DocumentHandler handler) {
      if(!type.isEmpty()) {
         handler.onBlockBegin(name, type);
      } else {
         handler.onBlockBegin(name);
      }
      if(pool != null) {
         pool.recycle(this);
      }
   }
   
   public void name(char[] source, int off, int length) {
      name.with(source, off, length);
   }
   
   public void type(char[] source, int off, int length) {
      type.with(source, off, length);
   }
   
   @Override
   public void reset() {
      name.reset();
      type.reset();
   }
   
   @Override
   public boolean isBegin() {
      return true;
   }

   private static class TypeSlice extends Name {
      
      private final TextSlice slice = new TextSlice();
      
      public boolean isEmpty() {
         return slice.length() <= 0;
      }
      
      @Override
      public CharSequence toToken() {
         return slice;
      }
      
      public TypeSlice with(char[] source, int off, int length) {
         slice.with(source, off, length);
         return this;
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
 