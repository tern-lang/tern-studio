package org.ternlang.studio.service.json.operation;

public interface OperationPool {
   void recycle(Type type);
   void recycle(Attribute attribute);
   void recycle(BlockBegin begin);
   void recycle(BlockEnd end);
   void recycle(ArrayBegin begin);
   void recycle(ArrayEnd end);
}
