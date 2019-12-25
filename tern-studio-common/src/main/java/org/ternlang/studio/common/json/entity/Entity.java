package org.ternlang.studio.common.json.entity;

public interface Entity {
   Object getInstance();
   Property getProperty(CharSequence name);
   String getName();
   Class getType();
}
