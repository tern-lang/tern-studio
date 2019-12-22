package org.ternlang.studio.common.json.entity;

public interface Entity {
   Object getInstance(CharSequence type);
   Property getProperty(CharSequence name);
   String getName();
   Class getType();
}
