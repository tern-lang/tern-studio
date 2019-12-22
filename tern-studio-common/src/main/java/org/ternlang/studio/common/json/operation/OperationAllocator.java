package org.ternlang.studio.common.json.operation;

import org.ternlang.common.ArrayStack;

public class OperationAllocator {

   private final ArrayStack<BlockBegin> blockBegins;
   private final ArrayStack<BlockEnd> blockEnds;
   private final ArrayStack<ArrayBegin> arrayBegins;
   private final ArrayStack<ArrayEnd> arrayEnds;
   private final ArrayStack<Attribute> attributes;
   private final ArrayStack<BlockType> types;
   private final OperationRecycler recycler;
   
   public OperationAllocator() {
      this.blockBegins = new ArrayStack<BlockBegin>();
      this.blockEnds = new ArrayStack<BlockEnd>();
      this.arrayBegins = new ArrayStack<ArrayBegin>();
      this.arrayEnds = new ArrayStack<ArrayEnd>();
      this.attributes = new ArrayStack<Attribute>();
      this.types = new ArrayStack<BlockType>();
      this.recycler = new OperationRecycler();
   }
   
   public BlockType type() {
      BlockType type = types.pop();
      
      if(type == null) {
         return new BlockType(recycler);
      }
      return type;
   }

   public Attribute attribute() {
      Attribute attribute = attributes.pop();
      
      if(attribute == null) {
         return new Attribute(recycler);
      }
      return attribute;
   }
   
   public BlockBegin blockBegin() {
      BlockBegin begin = blockBegins.pop();
      
      if(begin == null) {
         return new BlockBegin(recycler);
      }
      return begin;
   }
   
   public BlockEnd blockEnd() {
      BlockEnd end = blockEnds.pop();
      
      if(end == null) {
         return new BlockEnd(recycler);
      }
      return end;
   }
   
   public ArrayBegin arrayBegin() {
      ArrayBegin begin = arrayBegins.pop();
      
      if(begin == null) {
         return new ArrayBegin(recycler);
      }
      return begin;
   }
   
   public ArrayEnd arrayEnd() {
      ArrayEnd end = arrayEnds.pop();
      
      if(end == null) {
         return new ArrayEnd(recycler);
      }
      return end;
   }

   private class OperationRecycler implements OperationPool {

      @Override
      public void recycle(BlockType type) {
         type.reset();
         types.push(type);
      }
      
      @Override
      public void recycle(Attribute attribute) {
         attribute.reset();
         attributes.push(attribute);
      }

      @Override
      public void recycle(BlockBegin begin) {
         begin.reset();
         blockBegins.push(begin);
      }

      @Override
      public void recycle(BlockEnd end) {
         end.reset();
         blockEnds.push(end);
      }

      @Override
      public void recycle(ArrayBegin begin) {
         begin.reset();
         arrayBegins.push(begin);
      }

      @Override
      public void recycle(ArrayEnd end) {
         end.reset();
         arrayEnds.push(end);
      }
   }
}
