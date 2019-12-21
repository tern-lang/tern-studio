package org.ternlang.studio.service.json.object;

import org.ternlang.common.ArrayStack;
import org.ternlang.studio.service.json.document.DocumentAssembler;
import org.ternlang.studio.service.json.document.DocumentHandler;
import org.ternlang.studio.service.json.document.DocumentState;
import org.ternlang.studio.service.json.document.TextSlice;
import org.ternlang.studio.service.json.operation.ArrayBegin;
import org.ternlang.studio.service.json.operation.ArrayEnd;
import org.ternlang.studio.service.json.operation.Attribute;
import org.ternlang.studio.service.json.operation.BlockBegin;
import org.ternlang.studio.service.json.operation.BlockEnd;
import org.ternlang.studio.service.json.operation.Operation;
import org.ternlang.studio.service.json.operation.OperationAllocator;
import org.ternlang.studio.service.json.operation.BlockType;

public class TypeAssembler implements DocumentAssembler {
   
   private final DocumentHandler handler;
   private final OperationAllocator allocator;
   private final ArrayStack<Operation> active;
   private final ArrayStack<Operation> ready;
   private final ArrayStack<BlockType> blocks;
   private final DocumentState name;
   private final char[] type;
   
   public TypeAssembler(DocumentHandler handler, char[] type) {
      this.active = new ArrayStack<Operation>();
      this.ready = new ArrayStack<Operation>();
      this.allocator = new OperationAllocator();
      this.blocks= new ArrayStack<BlockType>();
      this.name = new DocumentState();
      this.handler = handler;
      this.type = type;
   }
   
   @Override
   public void begin() {
      handler.onBegin();
   }

   @Override
   public void name(char[] source, int off, int length) {
      name.with(source, off, length);
   }

   @Override
   public void text(char[] source, int off, int length) {
      Attribute attribute = name.attribute(allocator);
      
      if(name.match(type, 0, type.length)) {
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
   public void decimal(char[] source, int off, int length) {
      Attribute attribute = name.attribute(allocator);
      
      attribute.decimal(source, off, length);
      active.push(attribute);
   }

   @Override
   public void integer(char[] source, int off, int length) {
      Attribute attribute = name.attribute(allocator);
      
      attribute.integer(source, off, length);
      active.push(attribute); 
   }

   @Override
   public void bool(char[] source, int off, int length) {
      Attribute attribute = name.attribute(allocator);
      
      attribute.bool(source, off, length);
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
               TextSlice slice = type.toToken();
               BlockBegin begin = (BlockBegin)next;
               char[] source = slice.source();
               int off = slice.offset();
               int len = slice.length();
               
               begin.type(source, off, len);
            }
            break;
         }
      }
      while(!ready.isEmpty()) {
         Operation next = ready.pop();
         next.execute(handler);
      }
      BlockEnd end = allocator.blockEnd(); 
      
      end.execute(handler);
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
      handler.onEnd();
   }
}
