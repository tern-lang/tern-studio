package tern.studio.agent.debug;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import tern.studio.agent.debug.ScopeNode;
import tern.studio.agent.debug.ScopeNodeBuilder;

public class ArrayScopeNode implements ScopeNode {
   
   private final ScopeNodeBuilder builder;
   private final List<ScopeNode> nodes;
   private final Object object;
   private final String alias;
   private final String name;
   private final String path;
   private final int depth;
   
   public ArrayScopeNode(ScopeNodeBuilder builder, Object object, String path, String name, String alias, int depth) {
      this.nodes = new ArrayList<ScopeNode>();
      this.builder = builder;
      this.object = object;
      this.depth = depth;
      this.alias = alias;
      this.path = path;
      this.name = name;
   }
   
   @Override
   public int getDepth() {
      return depth;
   }
   
   @Override
   public String getName() {
      return name;
   }
   
   @Override
   public String getAlias() {
      return alias;
   }   
   
   @Override
   public String getPath() {
      return path;
   }

   @Override
   public List<ScopeNode> getNodes() {
      if(nodes.isEmpty()) {
         int length = Array.getLength(object);
         
         if(length > 0) {
            for(int i = 0; i < length; i++) {
               try {
                  Object value = Array.get(object, i);
                  
                  if(value != null) {
                     String index =  "[" + i + "]";
                     ScopeNode node = builder.createNode(path + "." + index,  index, index, value, 0, depth);
                     
                     if(node != null) {
                        nodes.add(node);
                     }
                  }
               } catch(Exception e) {
                  e.printStackTrace();
               }
            }
         }
      }
      return nodes;
   }
}