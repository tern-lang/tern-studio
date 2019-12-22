package org.ternlang.studio.common.json.document;

import org.ternlang.studio.common.json.operation.ArrayBegin;
import org.ternlang.studio.common.json.operation.Attribute;
import org.ternlang.studio.common.json.operation.BlockBegin;
import org.ternlang.studio.common.json.operation.OperationAllocator;

public class DocumentState {
   
   private char[] source;
   private int off;
   private int length;
   
   public DocumentState with(char[] source, int off, int length) {
      this.source = source;
      this.off = off;
      this.length = length;
      return this;
   }

   public boolean match(Name type) {
      if(type != null && source != null) {
         return type.equals(source, off, length);
      }
      return false;
   }
   
   public BlockBegin blockBegin(OperationAllocator allocator) {
      BlockBegin begin = allocator.blockBegin();
      
      if(length > 0) {
         begin.name(source, off, length);
      }
      return begin;
   }
   
   public ArrayBegin arrayBegin(OperationAllocator allocator) {
      ArrayBegin begin = allocator.arrayBegin();
      
      if(length > 0) {
         begin.name(source, off, length);
      }
      return begin;
   }
   
   public Attribute attribute(OperationAllocator allocator) {
      Attribute attribute = allocator.attribute();
      
      if(length > 0) {
         attribute.name(source, off, length);
      }
      return attribute;
   }
   
   public void reset() {
      off = length = 0;
      source = null;
   }
}