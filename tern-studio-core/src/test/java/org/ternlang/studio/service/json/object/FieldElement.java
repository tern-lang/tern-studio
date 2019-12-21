package org.ternlang.studio.service.json.object;

import java.lang.reflect.Field;

import org.ternlang.studio.service.json.document.TextTrie;

public class FieldElement {
   
   private final TextTrie<FieldAttribute> attributes;
   private final TypeIndexer indexer;
   private final String type;
   
   public FieldElement(TypeIndexer indexer, String type) {
      this.attributes = new TextTrie<FieldAttribute>();
      this.indexer = indexer;
      this.type = type;
   }

   public String getType() {
      return type;
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
   
   public FieldAttribute attribute(CharSequence name) {
      return attributes.match(name);
   }
}