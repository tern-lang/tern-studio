package org.ternlang.studio.common.json.document;

import org.ternlang.common.ArrayStack;
import org.ternlang.studio.common.json.operation.ArrayBegin;
import org.ternlang.studio.common.json.operation.ArrayEnd;
import org.ternlang.studio.common.json.operation.Attribute;
import org.ternlang.studio.common.json.operation.BlockBegin;
import org.ternlang.studio.common.json.operation.BlockEnd;
import org.ternlang.studio.common.json.operation.BlockType;
import org.ternlang.studio.common.json.operation.Operation;
import org.ternlang.studio.common.json.operation.OperationAllocator;

public class PriorityAssembler implements DocumentAssembler {
   
   private final ArrayStack<Operation> active;
   private final ArrayStack<Operation> ready;
   private final ArrayStack<Operation> commit;
   private final ArrayStack<BlockType> blocks;
   private final OperationAllocator allocator;
   private final DocumentHandler handler;
   private final DocumentState name;
   private final Name type;
   
   public PriorityAssembler(DocumentHandler handler, Name type) {
      this.commit = new ArrayStack<Operation>();
      this.active = new ArrayStack<Operation>();
      this.ready = new ArrayStack<Operation>();
      this.allocator = new OperationAllocator();
      this.blocks = new ArrayStack<BlockType>();
      this.name = new DocumentState();
      this.handler = handler;
      this.type = type;
   }
   
   @Override
   public void begin() {
      handler.begin();
   }

   @Override
   public void name(char[] source, int off, int length) {
      name.with(source, off, length);
   }

   @Override
   public void text(char[] source, int off, int length) {
      Attribute attribute = name.attribute(allocator);
      
      if(name.match(type)) {
         BlockType top = blocks.peek();
         
         if(!top.isEmpty()) {
            throw new IllegalStateException("Duplicate attribute");
         }
         top.with(source, off, length);
      }
      attribute.text(source, off, length);
      active.push(attribute);
   }

   @Override
   public void decimal(char[] source, int off, int length, double value) {
      Attribute attribute = name.attribute(allocator);
      
      attribute.decimal(source, off, length, value);
      active.push(attribute);
   }

   @Override
   public void integer(char[] source, int off, int length, long value) {
      Attribute attribute = name.attribute(allocator);
      
      attribute.integer(source, off, length, value);
      active.push(attribute); 
   }

   @Override
   public void bool(char[] source, int off, int length, boolean value) {
      Attribute attribute = name.attribute(allocator);
      
      attribute.bool(source, off, length, value);
      active.push(attribute); 
   }

   @Override
   public void none(char[] source, int off, int length) {
      Attribute attribute = name.attribute(allocator);
      
      attribute.none(source, off, length);
      active.push(attribute); 
   }

   @Override
   public void blockBegin() {
      BlockType type = allocator.type();
      BlockBegin begin = name.blockBegin(allocator);
      
      blocks.push(type);
      active.push(begin); 
   }

   @Override
   public void blockEnd() {
      BlockType type = blocks.pop();

      while(!active.isEmpty()) {
         Operation next = active.pop();
         ready.push(next);
         
         if(next.isBegin()) {
            if(!type.isEmpty()) {
               SourceSlice slice = type.toText();
               BlockBegin begin = (BlockBegin)next;
               char[] source = slice.source();
               int off = slice.offset();
               int len = slice.length();
               
               begin.type(source, off, len);
            }
            break;
         }
      }
      BlockEnd end = allocator.blockEnd();

      commit.push(end);
      type.dispose(); // recycle it
   }

   @Override
   public void arrayBegin() {
      ArrayBegin begin = name.arrayBegin(allocator);
      active.push(begin); 
   }

   @Override
   public void arrayEnd() {
      ArrayEnd end = allocator.arrayEnd();
      active.push(end); 
   }
   
   @Override
   public void end() {
      if(!active.isEmpty()) {
         throw new IllegalStateException("Operations not recycled");
      }
      while(!ready.isEmpty()) {
         Operation next = ready.pop();
         next.execute(handler);
      }
      while(!commit.isEmpty()) {
         Operation next = commit.pop();
         next.execute(handler);
      }
      handler.end();
      name.reset();
   }
}
