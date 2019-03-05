package org.ternlang.studio.index.classpath;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.FieldInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import org.ternlang.studio.index.IndexNode;

public class ClassIndexNodeCollector {

   public static Set<IndexNode> collectNodes(ClassIndexNode node, ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();

      nodes.addAll(collectSupers(node, info));
      nodes.addAll(collectConstructors(node, info));
      nodes.addAll(collectMethods(node, info));
      nodes.addAll(collectFields(node, info));
      nodes.addAll(collectInnerClasses(node, info));

      return Collections.unmodifiableSet(nodes);
   }

   private static Set<IndexNode> collectSupers(ClassIndexNode node, ClassInfo info) {
      Map<String, IndexNode> nodes = new HashMap<String, IndexNode>();
      Set<IndexNode> set = new HashSet<IndexNode>();
      ClassIndexNodePath path = node.getPath();

      collectSupers(path, info, nodes);
      set.addAll(nodes.values());
      return set;
   }

   private static void collectSupers(ClassIndexNodePath path, ClassInfo info, Map<String, IndexNode> done) {
      String name = info.getName();

      if(!done.containsKey(name)) {
         ClassInfoList supers = info.getSuperclasses();
         ClassInfoList interfaces = info.getInterfaces();

         for (ClassInfo entry : supers) {
            String type = entry.getName();
            IndexNode real = path.getNode(type);

            if (real != null) {
               SuperIndexNode superNode = new SuperIndexNode(real);
               done.put(type, superNode);
            }
            collectSupers(path, entry, done);
         }
         for (ClassInfo entry : interfaces) {
            String type = entry.getName();
            IndexNode real = path.getNode(type);

            if (real != null) {
               SuperIndexNode superNode = new SuperIndexNode(real);
               done.put(type, superNode);
            }
            collectSupers(path, entry, done);
         }
      }
   }

   private static Set<IndexNode> collectConstructors(ClassIndexNode node, ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      MethodInfoList list = info.getConstructorInfo();

      for(MethodInfo entry : list) {
         if(entry.isPublic()) {
            IndexNode constructor = new ConstructorIndexNode(node, entry);
            IndexNode cache = new CacheIndexNode(constructor);

            nodes.add(cache);
         }
      }
      return nodes;
   }

   private static Set<IndexNode> collectMethods(ClassIndexNode node, ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      MethodInfoList list = info.getMethodInfo();

      for(MethodInfo entry : list) {
         if(entry.isPublic()) {
            IndexNode method = new MethodIndexNode(node, entry);
            IndexNode cache = new CacheIndexNode(method);

            nodes.add(cache);
         }
      }
      return nodes;
   }

   private static Set<IndexNode> collectFields(ClassIndexNode node, ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      FieldInfoList list = info.getFieldInfo();

      for(FieldInfo entry : list) {
         if(entry.isPublic()) {
            IndexNode field = new FieldIndexNode(node, entry);
            IndexNode cache = new CacheIndexNode(field);

            nodes.add(cache);
         }
      }
      return nodes;
   }

   private static Set<IndexNode> collectInnerClasses(ClassIndexNode node, ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      ClassInfoList list = info.getInnerClasses();
      ClassIndexNodePath path = node.getPath();

      for(ClassInfo entry : list) {
         String type = entry.getName();
         IndexNode real = path.getNode(type);

         if(real != null) {
            nodes.add(real);
         }
      }
      return nodes;
   }
}
