package org.ternlang.studio.common.json.object;

import org.ternlang.studio.common.json.document.Name;

public class ObjectMapper {
   
   private final ValueConverter converter;
   private final ObjectBuilder builder;
   private final ObjectReader reader;
   private final TypeIndexer indexer;
   private final NameValue match;
   private final NameValue root;
   
   public ObjectMapper() {
      this.converter = new ValueConverter();
      this.builder = new ObjectBuilder();
      this.indexer = new TypeIndexer(converter, builder);
      this.match = new NameValue();
      this.root = new NameValue();
      this.reader = new ObjectReader(indexer, converter, builder, match, root);
   }

   public ObjectMapper register(Class type) {
      indexer.index(type);
      return this;
   }
   
   public ObjectMapper match(String type) {
      if(type != null) {
         match.with(type);
      } else {
         match.reset();
      }
      return this;
   }

   public ObjectReader read(Class type) {
      FieldElement tree = indexer.index(type);
      String name = type.getSimpleName();
      
      root.with(name);
      return reader;
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
