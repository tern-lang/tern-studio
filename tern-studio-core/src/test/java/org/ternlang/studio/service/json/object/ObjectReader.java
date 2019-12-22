package org.ternlang.studio.service.json.object;

import org.ternlang.studio.service.json.JsonParser;
import org.ternlang.studio.service.json.document.DirectAssembler;
import org.ternlang.studio.service.json.document.DocumentAssembler;
import org.ternlang.studio.service.json.document.Name;

public class ObjectReader {

   private final DirectStrategy direct;
   private final TypeStrategy type;
   private final Name match;
   
   public ObjectReader(TypeIndexer indexer, ValueConverter converter, ObjectBuilder builder, Name match, String root) {
      this.direct = new DirectStrategy(indexer, converter, builder, root);
      this.type = new TypeStrategy(indexer, converter, builder, match, root);
      this.match = match;
   }
   
   public <T> T read(String source) {
      if(match.isEmpty()) {
         return direct.read(source);
      } 
      return type.read(source);
   }
   
   private static class DirectStrategy {
      
      private final DocumentAssembler assembler;
      private final ObjectHandler handler;
      private final JsonParser parser;
      
      public DirectStrategy(TypeIndexer indexer, ValueConverter converter, ObjectBuilder builder, String root) {
         this.handler = new ObjectHandler(indexer, converter, builder, root);
         this.assembler = new DirectAssembler(handler);
         this.parser = new JsonParser(assembler);
      }
      
      public <T> T read(String source) {
         parser.parse(source);
         return (T)handler.get();
      }
   }
   
   private static class TypeStrategy {
      
      private final DocumentAssembler assembler;
      private final ObjectHandler handler;
      private final JsonParser parser;
      
      public TypeStrategy(TypeIndexer indexer, ValueConverter converter, ObjectBuilder builder, Name match, String root) {
         this.handler = new ObjectHandler(indexer, converter, builder, root);
         this.assembler = new TypeAssembler(handler, match);
         this.parser = new JsonParser(assembler);
      }
      
      public <T> T read(String source) {
         parser.parse(source);
         return (T)handler.get();
      }
   }
   
}
