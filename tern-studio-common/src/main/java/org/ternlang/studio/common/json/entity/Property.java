package org.ternlang.studio.common.json.entity;

import org.ternlang.studio.common.json.document.Value;

public interface Property {
   Object getValue(Object source);
   void setValue(Object source, Object value);
   void setValue(Object source, Value value);
   String getName();
   Class getType();
}
