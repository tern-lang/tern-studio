package tern.studio.index;

import java.util.Set;

public class IndexDumper {

   public static String dump(IndexNode node, IndexNode match, String expression) throws Exception {
      StringBuilder builder = new StringBuilder();
      dump(node, match, expression, builder, "");
      return builder.toString();
   }
   
   private static void dump(IndexNode node, IndexNode match, String expression, StringBuilder builder, String indent) throws Exception {
      if(node != null) {
         Set<IndexNode> nodes = node.getNodes();
         IndexType type = node.getType();
         String name = node.getName();

         if(node == match) {
            builder.append("\n");
            builder.append(indent);
            builder.append(">>>");
            builder.append(expression);
            builder.append("<<<");
            builder.append("\n");
            builder.append("\n");
         }
         if(!type.isRoot()) {
            builder.append(indent);
            
            if(!type.isCompound()) {
               String description = type.getName();
               IndexNode constraint = node.getConstraint();

               builder.append(description);
               builder.append(" ");
               builder.append(name);

               if(constraint != null) {
                  String token = constraint.getName();

                  builder.append(": ");
                  builder.append(token);
               }
               builder.append(" ");
            }
            if(type.isLeaf()) {
               builder.append("\n");
            } else {
               builder.append("{\n");
            }
         }
         for(IndexNode entry : nodes) {
            if(type.isRoot()) {
               dump(entry, match, expression, builder, "");
            } else {
               dump(entry, match, expression, builder, indent + "   ");
            }
         }
         if(!type.isRoot() && !type.isLeaf()) {
            builder.append(indent);
            builder.append("}\n");
         }
      }
   }
}
