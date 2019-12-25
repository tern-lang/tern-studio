package org.ternlang.studio.common.json.entity;

import org.ternlang.studio.common.json.document.Name;

public class EntityMapper {
   
   private final EntityReader reader;
   private final EntityWriter writer;
   private final NameValue match;
   private final NameValue root;
   
   public EntityMapper(EntityProvider provider) {
      this.match = new NameValue();
      this.root = new NameValue();
      this.reader = new EntityReader(provider, match, root);
      this.writer = new EntityWriter(provider);
   }

   public EntityMapper match(String attribute) {
      if(attribute != null) {
         match.with(attribute);
      } else {
         match.reset();
      }
      return this;
   }

   public EntityReader read(String type) {
      if(type != null) {
         root.with(type);
      } else {
         root.reset();
      }
      return reader;
   }
   
   public EntityWriter write() {
      return writer;
   }
   
   private static class NameValue extends Name {

      private String value;
      
      public NameValue() {
         this.value = "";
      }

      public NameValue with(String value) {
         this.value = value;
         this.hash = 0;
         return this;
      }
      
      @Override
      public int hashCode() {
         return value.hashCode();
      }

      @Override
      public CharSequence toText() {
         return value;
      }
      
      @Override
      public boolean isEmpty() {
         return value.isEmpty();
      }
      
      public void reset() {
         value = "";
      }

      @Override
      public String toString() {
         return value;
      }
   }
}
