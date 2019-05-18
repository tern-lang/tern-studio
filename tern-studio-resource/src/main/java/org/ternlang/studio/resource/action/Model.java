package org.ternlang.studio.resource.action;

import java.util.Map;

public interface Model extends Iterable<String> {
   boolean isEmpty();
   Map<String, Object> getAttributes();
   Object removeAttribute(String name);
   Object getAttribute(String name);
   boolean containsAttribute(String name);
   void setAttribute(String name, String text);
   void setAttribute(String name, Object value);
}
