package org.ternlang.studio.service.json.operation;

public class OperationAllocator {

   private final OperationRecycler recycler;
   
   public OperationAllocator() {
      this.recycler = new OperationRecycler();
   }
   
   public Type type() {
      return new Type();
   }

   public Attribute attribute() {
      return new Attribute(recycler);
   }
   
   public BlockBegin blockBegin() {
      return new BlockBegin(recycler);
   }
   
   public BlockEnd blockEnd() {
      return new BlockEnd(recycler);
   }
   
   public ArrayBegin arrayBegin() {
      return new ArrayBegin(recycler);
   }
   
   public ArrayEnd arrayEnd() {
      return new ArrayEnd(recycler);
   }
   
   private class OperationRecycler implements OperationPool {

      @Override
      public void recycle(Type type) {
         type.reset();
      }
      
      @Override
      public void recycle(Attribute attribute) {
         attribute.reset();
      }

      @Override
      public void recycle(BlockBegin begin) {
         begin.reset();
      }

      @Override
      public void recycle(BlockEnd end) {
         end.reset();
      }

      @Override
      public void recycle(ArrayBegin begin) {
         begin.reset();
      }

      @Override
      public void recycle(ArrayEnd end) {
         end.reset();
      }
      
   }
}
