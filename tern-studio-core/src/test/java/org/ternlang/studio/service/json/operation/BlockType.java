package org.ternlang.studio.service.json.operation;

import org.ternlang.studio.service.json.document.Name;
import org.ternlang.studio.service.json.document.TextSlice;

public class BlockType extends Name {

   private final OperationPool pool;
   private final TextSlice slice;
   
   public BlockType(OperationPool pool) {
      this.slice = new TextSlice();
      this.pool = pool;
   }
   
   @Override
   public TextSlice toToken() {
      return slice;
   }
   
   public BlockType with(char[] source, int off, int length) {
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

   public void dispose() {
      if(pool != null) {
         pool.recycle(this);
      }
   }
   
   @Override
   public String toString() {
      return slice.toString();
   }
}
