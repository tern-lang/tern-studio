package org.ternlang.studio.common.json.entity;

public interface Entity {
   Object getInstance();
   Iterable<Property> getProperties();
   Property getProperty(CharSequence name);
   String getEntity();
   Class getType();
}
