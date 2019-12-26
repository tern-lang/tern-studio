package org.ternlang.studio.common.json.entity;

import org.ternlang.studio.common.json.document.Value;

public interface Property {
   boolean isArray();
   boolean isPrimitive();
   Value getValue(Object source);
   void setValue(Object source, Object value);
   void setValue(Object source, Value value);
   String getEntity();
   String getName();
   Class getType();
}
