package org.ternlang.studio.agent.debug;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.ternlang.agent.message.common.Variable;
import org.ternlang.agent.message.common.VariableArray;
import org.ternlang.agent.message.common.VariableArrayBuilder;
import org.ternlang.agent.message.common.VariableAttribute;
import org.ternlang.agent.message.common.VariableAttributeArray;
import org.ternlang.agent.message.common.VariableBuilder;
import org.ternlang.agent.message.common.VariableTree;
import org.ternlang.agent.message.common.VariableTreeCodec;
import org.ternlang.message.ArrayByteBuffer;

public class ScopeVariableConverter {

   public static ScopeVariableTree convert(VariableTree variableTree) {
      Map<String, Map<String, String>> evaluation = new LinkedHashMap<String, Map<String, String>>();
      Map<String, Map<String, String>> local = new LinkedHashMap<String, Map<String, String>>();

      convert(local, variableTree.local());
      convert(evaluation, variableTree.evaluation());

      return new ScopeVariableTree.Builder(variableTree.change()).withEvaluation(evaluation).withLocal(local).build();
   }

   private static void convert(Map<String, Map<String, String>> variables, VariableArray data) {
      Iterator<Variable> iterator = data.iterator();
      
      while(iterator.hasNext()) {
         Variable local = iterator.next();
         String path = local.path().path().toString();
         VariableAttributeArray attributes = local.attributes();
         int length = attributes.length();

         if(length > 0) {
            Map<String, String> container = new LinkedHashMap<String, String>();

            for(VariableAttribute attribute : attributes) {
               String name = attribute.name().toString();
               String value = attribute.value().toString();

               container.put(name, value);
            }
            variables.put(path, container);
         }
      }
   }

   public static VariableTree convert(ScopeVariableTree variableTree) {
      ArrayByteBuffer frame = new ArrayByteBuffer();
      VariableTreeCodec codec = new VariableTreeCodec();

      codec.with(frame, 0, Integer.MAX_VALUE);
      frame.setCount(8);

      convert(codec.local(), variableTree.getLocal().entrySet());
      convert(codec.evaluation(), variableTree.getEvaluation().entrySet());
      
      // this line is breaking things !!!!!!!!!!!!!!!!!!!!!!!!!! TODO XXX FIXME
      codec.change(variableTree.getChange());
      
      ScopeVariableTree rebuildTree = convert(codec);
      
      if(!rebuildTree.getLocal().equals(variableTree.getLocal())) {
         throw new IllegalStateException("Tree is not the same");
      }
      if(!rebuildTree.getEvaluation().equals(variableTree.getEvaluation())) {
         throw new IllegalStateException("Tree is not the same");
      }
      return codec;
   }

   private static void convert(VariableArrayBuilder variables, Set<Map.Entry<String, Map<String, String>>> data) {
      for(Map.Entry<String, Map<String, String>> local : data) {
         Set<Map.Entry<String, String>> attributes = local.getValue().entrySet();
         VariableBuilder variable = variables.add();
         String path = local.getKey();

         variable.path().path(path);

         for(Map.Entry<String, String> attribute : attributes) {
            variable.attributes()
               .add()
               .name(attribute.getKey())
               .value(attribute.getValue());
         }
      }
   }
}
