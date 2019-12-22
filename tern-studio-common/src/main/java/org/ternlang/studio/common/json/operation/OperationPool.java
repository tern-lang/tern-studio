package org.ternlang.studio.common.json.operation;

public interface OperationPool {
   void recycle(Attribute attribute);
   void recycle(BlockType type);
   void recycle(BlockBegin begin);
   void recycle(BlockEnd end);
   void recycle(ArrayBegin begin);
   void recycle(ArrayEnd end);
}
