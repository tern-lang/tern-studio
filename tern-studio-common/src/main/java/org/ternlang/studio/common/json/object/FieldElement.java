package org.ternlang.studio.common.json.object;

import java.lang.reflect.Field;

import org.ternlang.studio.common.json.document.TextTrie;

public class FieldElement {
   
   private final TextTrie<FieldAttribute> attributes;
   private final String type;
   
   public FieldElement(String type) {
      this.attributes = new TextTrie<FieldAttribute>();
      this.type = type;
   }
   
   public FieldAttribute index(CharSequence name, Field field) {
      FieldAttribute accessor = attributes.match(name);
      
      if(accessor == null) {
         accessor = new FieldAttribute(field);  
         
         field.setAccessible(true);
         attributes.index(accessor, name);
      }
      return accessor;
   }
   
   public FieldAttribute match(CharSequence name) {
      return attributes.match(name);
   }

   public String type() {
      return type;
   }
}