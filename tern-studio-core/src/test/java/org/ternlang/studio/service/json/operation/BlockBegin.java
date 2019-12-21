package org.ternlang.studio.service.json.operation;

import org.ternlang.studio.service.json.document.DocumentHandler;
import org.ternlang.studio.service.json.document.Name;
import org.ternlang.studio.service.json.document.TextSlice;

public class BlockBegin extends Operation {
   
   private final OperationPool pool;
   private final TypeValue type;
   private final NameValue name;

   public BlockBegin(OperationPool pool) {
      this.name = new NameValue();
      this.type = new TypeValue();
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

   private static class TypeValue extends Name {
      
      private final TextSlice slice;

      public TypeValue() {
         this.slice = new TextSlice();
      }

      public TypeValue with(char[] source, int off, int length) {
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
 