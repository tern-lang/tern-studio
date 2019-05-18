package org.ternlang.studio.resource.template.reflect;

public interface Accessor {
   <T> T getValue(Object source);
   Class getType();
}