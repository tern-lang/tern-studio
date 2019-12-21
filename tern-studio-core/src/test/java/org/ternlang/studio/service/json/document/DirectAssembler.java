package org.ternlang.studio.service.json.document;

import org.ternlang.studio.service.json.operation.ArrayBegin;
import org.ternlang.studio.service.json.operation.ArrayEnd;
import org.ternlang.studio.service.json.operation.Attribute;
import org.ternlang.studio.service.json.operation.BlockBegin;
import org.ternlang.studio.service.json.operation.BlockEnd;
import org.ternlang.studio.service.json.operation.OperationAllocator;
import org.ternlang.studio.service.json.operation.BlockType;

public class DirectAssembler implements DocumentAssembler {

   private final StaticAllocator allocator;
   private final DocumentHandler handler;
   private final DocumentState state;
   
   public DirectAssembler(DocumentHandler handler) {
      this.allocator = new StaticAllocator();
      this.state = new DocumentState();
      this.handler = handler;
   }
   
   @Override
   public void begin() {
      handler.onBegin();
   }
   
   @Override
   public void name(char[] source, int off, int length) {
      state.with(source, off, length);
   }

   @Override
   public void text(char[] source, int off, int length) {
      Attribute text = state.attribute(allocator);
      
      text.text(source, off, length);
      text.execute(handler);
      text.reset();
      state.reset();
   }

   @Override
   public void decimal(char[] source, int off, int length) {
      Attribute decimal = state.attribute(allocator);
      
      decimal.decimal(source, off, length);
      decimal.execute(handler);
      decimal.reset();
      state.reset();
   }

   @Override
   public void integer(char[] source, int off, int length) {
      Attribute integer = state.attribute(allocator);
      
      integer.integer(source, off, length);
      integer.execute(handler);
      integer.reset();
      state.reset();
   }

   @Override
   public void bool(char[] source, int off, int length) {
      Attribute bool = state.attribute(allocator);
      
      bool.integer(source, off, length);
      bool.execute(handler);
      bool.reset();
      state.reset();
   }

   @Override
   public void none(char[] source, int off, int length) {
      Attribute none = state.attribute(allocator);
      
      none.integer(source, off, length);
      none.execute(handler);
      none.reset();
      state.reset();
   }

   @Override
   public void blockBegin() {
      BlockBegin begin = state.blockBegin(allocator);
      
      begin.execute(handler);
      begin.reset();
      state.reset();
   }

   @Override
   public void blockEnd() {
      BlockEnd blockEnd = allocator.blockEnd();
      
      blockEnd.execute(handler);
      blockEnd.reset();
   }

   @Override
   public void arrayBegin() {
      ArrayBegin begin = state.arrayBegin(allocator);
      
      begin.execute(handler);
      begin.reset();
      state.reset();
   }

   @Override
   public void arrayEnd() {
      ArrayEnd arrayEnd = allocator.arrayEnd();
      
      arrayEnd.execute(handler);
      arrayEnd.reset();
   }
   
   @Override
   public void end() {
      handler.onEnd();
   }
   
   private static class StaticAllocator extends OperationAllocator {
      
      private final BlockBegin blockBegin;
      private final BlockEnd blockEnd;
      private final ArrayBegin arrayBegin;
      private final ArrayEnd arrayEnd;
      private final Attribute attribute;
      private final BlockType type;
      
      public StaticAllocator() {
         this.attribute = new Attribute(null);
         this.blockBegin = new BlockBegin(null);
         this.blockEnd = new BlockEnd(null);
         this.arrayBegin = new ArrayBegin(null);
         this.arrayEnd = new ArrayEnd(null);
         this.type = new BlockType(null);
      }
      
      @Override
      public BlockType type() {
         return type;
      }
      
      @Override
      public Attribute attribute() {
         return attribute;
      }
      
      @Override
      public BlockBegin blockBegin() {
         return blockBegin;
      }
      
      @Override
      public BlockEnd blockEnd() {
         return blockEnd;
      }
      
      @Override
      public ArrayBegin arrayBegin() {
         return arrayBegin;
      }
      
      @Override
      public ArrayEnd arrayEnd() {
         return arrayEnd;
      }
   }

}
