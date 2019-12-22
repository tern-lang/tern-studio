package org.ternlang.studio.common.json.operation;

import org.ternlang.studio.common.json.document.Name;
import org.ternlang.studio.common.json.document.SourceSlice;

public class BlockType extends Name {

   private final OperationPool pool;
   private final SourceSlice slice;
   
   public BlockType(OperationPool pool) {
      this.slice = new SourceSlice();
      this.pool = pool;
   }

   public BlockType with(char[] source, int off, int length) {
      slice.with(source, off, length);
      hash = 0;
      return this;
   }

   @Override
   public SourceSlice toText() {
      return slice;
   }
   
   @Override
   public boolean isEmpty() {
      return slice.isEmpty();
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
