package org.ternlang.studio.resource.action;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class HashModel implements Model {

   private final Map<String, Object> context;

   public HashModel() {
      this.context = new LinkedHashMap<String, Object>();
   }

   @Override
   public boolean isEmpty() {
      return context.isEmpty();
   }

   @Override
   public Iterator<String> iterator() {
      return context.keySet().iterator();
   }

   @Override
   public Map<String, Object> getAttributes() {
      return context;
   }

   @Override
   public Object removeAttribute(String name) {
      return context.remove(name);
   }

   @Override
   public Object getAttribute(String name) {
      return context.get(name);
   }

   @Override
   public boolean containsAttribute(String name) {
      return context.containsKey(name);
   }

   @Override
   public void setAttribute(String name, String text) {
      context.put(name, text);      
   }

   @Override
   public void setAttribute(String name, Object value) {
      context.put(name, value);
   }
}
