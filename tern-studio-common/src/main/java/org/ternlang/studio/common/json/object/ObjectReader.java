package org.ternlang.studio.common.json.object;

import org.ternlang.studio.common.json.JsonParser;
import org.ternlang.studio.common.json.document.DirectAssembler;
import org.ternlang.studio.common.json.document.DocumentAssembler;
import org.ternlang.studio.common.json.document.Name;

public class ObjectReader {

   private final DirectStrategy direct;
   private final TypeStrategy type;
   private final Name match;
   private final Name root;
   
   public ObjectReader(TypeIndexer indexer, ValueConverter converter, ObjectBuilder builder, Name match, Name root) {
      this.direct = new DirectStrategy(indexer, converter, builder, root);
      this.type = new TypeStrategy(indexer, converter, builder, match, root);
      this.match = match;
      this.root = root;
   }
   
   public <T> T read(String source) {
      if(root.isEmpty()) {
         throw new IllegalStateException("No root class specified");
      }
      if(match.isEmpty()) {
         return direct.read(source);
      } 
      return type.read(source);
   }
   
   private static class DirectStrategy {
      
      private final DocumentAssembler assembler;
      private final ObjectHandler handler;
      private final JsonParser parser;
      
      public DirectStrategy(TypeIndexer indexer, ValueConverter converter, ObjectBuilder builder, Name root) {
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
      
      public TypeStrategy(TypeIndexer indexer, ValueConverter converter, ObjectBuilder builder, Name match, Name root) {
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
