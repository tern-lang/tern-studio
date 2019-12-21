package org.ternlang.studio.service.json.document;

import org.ternlang.studio.service.json.operation.ArrayBegin;
import org.ternlang.studio.service.json.operation.Attribute;
import org.ternlang.studio.service.json.operation.BlockBegin;
import org.ternlang.studio.service.json.operation.OperationAllocator;

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
   
   public boolean match(char[] type, int off, int length) {
      if(type.length > 0 && type.length == length) {
         int remain = length;
         
         while(--remain >= 0) {
            if(source[remain] != type[remain]) {
               return false;
            }
         }
         return true;
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