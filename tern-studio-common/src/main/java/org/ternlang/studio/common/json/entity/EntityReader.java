package org.ternlang.studio.common.json.entity;

import org.ternlang.studio.common.json.JsonParser;
import org.ternlang.studio.common.json.document.DirectAssembler;
import org.ternlang.studio.common.json.document.DocumentAssembler;
import org.ternlang.studio.common.json.document.Name;
import org.ternlang.studio.common.json.document.PriorityAssembler;

public class EntityReader {

   private final PriorityStrategy priority;
   private final DirectStrategy direct;
   private final Name match;
   private final Name root;
   
   public EntityReader(EntityProvider provider, Name match, Name root) {
      this.direct = new DirectStrategy(provider, root);
      this.priority = new PriorityStrategy(provider, match, root);
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
      return priority.read(source);
   }
   
   private static class DirectStrategy {
      
      private final DocumentAssembler assembler;
      private final EntityHandler handler;
      private final JsonParser parser;
      
      public DirectStrategy(EntityProvider provider, Name root) {
         this.handler = new EntityHandler(provider, root);
         this.assembler = new DirectAssembler(handler);
         this.parser = new JsonParser(assembler);
      }
      
      public <T> T read(String source) {
         parser.parse(source);
         return (T)handler.get();
      }
   }
   
   private static class PriorityStrategy {
      
      private final DocumentAssembler assembler;
      private final EntityHandler handler;
      private final JsonParser parser;
      
      public PriorityStrategy(EntityProvider provider, Name match, Name root) {
         this.handler = new EntityHandler(provider, root);
         this.assembler = new PriorityAssembler(handler, match);
         this.parser = new JsonParser(assembler);
      }
      
      public <T> T read(String source) {
         parser.parse(source);
         return (T)handler.get();
      }
   }
   
}
