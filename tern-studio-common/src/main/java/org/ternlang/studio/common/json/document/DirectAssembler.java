package org.ternlang.studio.common.json.document;

import org.ternlang.studio.common.json.operation.ArrayBegin;
import org.ternlang.studio.common.json.operation.ArrayEnd;
import org.ternlang.studio.common.json.operation.Attribute;
import org.ternlang.studio.common.json.operation.BlockBegin;
import org.ternlang.studio.common.json.operation.BlockEnd;
import org.ternlang.studio.common.json.operation.BlockType;
import org.ternlang.studio.common.json.operation.OperationAllocator;

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
      handler.begin();
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
   public void decimal(char[] source, int off, int length, double value) {
      Attribute decimal = state.attribute(allocator);
      
      decimal.decimal(source, off, length, value);
      decimal.execute(handler);
      decimal.reset();
      state.reset();
   }

   @Override
   public void integer(char[] source, int off, int length, long value) {
      Attribute integer = state.attribute(allocator);
      
      integer.integer(source, off, length, value);
      integer.execute(handler);
      integer.reset();
      state.reset();
   }

   @Override
   public void bool(char[] source, int off, int length, boolean value) {
      Attribute bool = state.attribute(allocator);
      
      bool.bool(source, off, length, value);
      bool.execute(handler);
      bool.reset();
      state.reset();
   }

   @Override
   public void none(char[] source, int off, int length) {
      Attribute none = state.attribute(allocator);
      
      none.none(source, off, length);
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
      handler.end();
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
