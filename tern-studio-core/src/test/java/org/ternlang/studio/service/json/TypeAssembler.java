package org.ternlang.studio.service.json;

import java.util.ArrayList;
import java.util.List;

import org.ternlang.common.ArrayStack;
import org.ternlang.studio.service.json.handler.AttributeHandler;
import org.ternlang.studio.service.json.operation.ArrayBegin;
import org.ternlang.studio.service.json.operation.ArrayEnd;
import org.ternlang.studio.service.json.operation.Attribute;
import org.ternlang.studio.service.json.operation.BlockBegin;
import org.ternlang.studio.service.json.operation.BlockEnd;
import org.ternlang.studio.service.json.operation.Operation;
import org.ternlang.studio.service.json.operation.OperationAllocator;
import org.ternlang.studio.service.json.operation.OperationPool;
import org.ternlang.studio.service.json.operation.Type;

public class TypeAssembler implements JsonAssembler {
   
   private final OperationAllocator allocator;
   private final AttributeHandler handler;
   private final List<Operation> operations;
   private final ArrayStack<Type> stack;
   private final OperationPool pool;
   private final JsonState name;
   private final char[] type;
   
   public TypeAssembler(AttributeHandler handler, OperationPool pool, char[] type) {
      this.operations = new ArrayList<Operation>();
      this.allocator = new OperationAllocator();
      this.stack= new ArrayStack<Type>();
      this.name = new JsonState();
      this.handler = handler;
      this.pool = pool;
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
         Type top = stack.peek();
         
         if(!top.isEmpty()) {
            throw new IllegalStateException("Duplicate attribute");
         }
         top.with(source, off, length);
      }
      attribute.text(source, off, length);
      operations.add(attribute);
   }

   @Override
   public void decimal(char[] source, int off, int length) {
      Attribute attribute = name.attribute(allocator);
      
      attribute.decimal(source, off, length);
      operations.add(attribute);
   }

   @Override
   public void integer(char[] source, int off, int length) {
      Attribute attribute = name.attribute(allocator);
      
      attribute.integer(source, off, length);
      operations.add(attribute); 
   }

   @Override
   public void bool(char[] source, int off, int length) {
      Attribute attribute = name.attribute(allocator);
      
      attribute.bool(source, off, length);
      operations.add(attribute); 
   }

   @Override
   public void none(char[] source, int off, int length) {
      Attribute attribute = name.attribute(allocator);
      
      attribute.none(source, off, length);
      operations.add(attribute); 
   }

   @Override
   public void blockBegin() {
      Type type = allocator.type();
      BlockBegin begin = name.blockBegin(allocator);
      
      stack.push(type);
      operations.add(begin); 
   }

   @Override
   public void blockEnd() {
      Type type = stack.pop();
      
      if(!type.isEmpty()) {
         Slice slice = type.toToken();
         int length = operations.size();
         int index = length -1;
         
         while(index >= 0) {
            Operation next = operations.get(index);
            
            if(next.isBegin()) {
               BlockBegin begin = (BlockBegin)next;
               char[] source = slice.source();
               int off = slice.offset();
               int len = slice.length();
               
               begin.type(source, off, len);
               break;
            }
            index--;
         }
         while(index < length) {
            Operation next = operations.get(index);
            next.execute(handler);
         }
         while(index >= 0) {
            Operation next = operations.remove(index);
            
            if(next.isBegin()) {
               break;
            }
         }
      }
      BlockEnd end = allocator.blockEnd(); 
      
      pool.recycle(type);
      operations.add(end); 
   }

   @Override
   public void arrayBegin() {
      ArrayBegin begin = name.arrayBegin(allocator);
      operations.add(begin); 
   }

   @Override
   public void arrayEnd() {
      ArrayEnd end = allocator.arrayEnd();
      operations.add(end); 
   }
   
   @Override
   public void end() {
      handler.onEnd();
   }
}
