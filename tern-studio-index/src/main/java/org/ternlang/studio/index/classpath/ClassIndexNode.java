package org.ternlang.studio.index.classpath;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.FieldInfoList;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.IndexType;

public class ClassIndexNode implements IndexNode {

   private final AtomicReference<Set<IndexNode>> nodes;
   private final ClassIndexNodePath path;
   private final ClassInfo info;

   public ClassIndexNode(ClassIndexNodePath path, ClassInfo info) {
      this.nodes = new AtomicReference<Set<IndexNode>>();
      this.info = info;
      this.path = path;
   }

   @Override
   public int getLine() {
      return -1;
   }

   @Override
   public boolean isPublic() {
      return info.isPublic();
   }

   @Override
   public boolean isNative() {
      return true;
   }

   public ClassIndexNodePath getPath() {
      return path;
   }

   @Override
   public String getResource() {
      File file = getFile();

      if(file != null) {
         try {
            return file.getName();
         } catch(Throwable e) {}
      }
      return "?";
   }

   @Override
   public String getAbsolutePath() {
      File file = getFile();

      if(file != null) {
         try {
            return file.getCanonicalPath();
         } catch(Throwable e) {}
      }
      return "?";
   }

   public File getFile() {
      return info.getClasspathElementFile();
   }

   @Override
   public String getName() {
      return info.getSimpleName();
   }

   @Override
   public String getTypeName() {
      return info.getName();
   }

   @Override
   public String getFullName() {
      return info.getName();
   }

   @Override
   public String getModule() {
      return info.getPackageName();
   }

   @Override
   public IndexNode getConstraint() {
      return this;
   }

   @Override
   public IndexNode getParent() {
      ClassInfo parent = info.getSuperclass();

      if(parent != null) {
         return new ClassIndexNode(path, parent);
      }
      return null;
   }

   @Override
   public IndexType getType() {
      if(info.isInterface()) {
         return IndexType.TRAIT;
      }
      if(info.isEnum()) {
         return IndexType.ENUM;
      }
      return IndexType.CLASS;
   }

   @Override
   public Set<IndexNode> getNodes() {
      Set<IndexNode> set = nodes.get();

      if(set == null) {
         set = getChildren(this, info);
         set = Collections.unmodifiableSet(set);
         nodes.set(set);
      }
      return set;
   }

   @Override
   public String toString(){
      return getFullName();
   }

   private static Set<IndexNode> getChildren(ClassIndexNode node, ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();

      nodes.addAll(getSupers(node, info));
      nodes.addAll(getConstructors(node, info));
      nodes.addAll(getMethods(node, info));
      nodes.addAll(getFields(node, info));
      nodes.addAll(getInnerClasses(node, info));

      return nodes;
   }

   private static Set<IndexNode> getSupers(ClassIndexNode node, ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      ClassInfoList list = info.getSuperclasses();
      ClassIndexNodePath path = node.getPath();

      for(ClassInfo entry : list) {
         nodes.add(new ClassIndexNode(path, entry));
      }
      return nodes;
   }

   private static Set<IndexNode> getConstructors(ClassIndexNode node, ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      MethodInfoList list = info.getConstructorInfo();

      for(MethodInfo entry : list) {
         String name = entry.getName();

         if(entry.isPublic() && !name.startsWith("<")) {
            nodes.add(new MethodIndexNode(node, entry));
         }
      }
      return nodes;
   }

   private static Set<IndexNode> getMethods(ClassIndexNode node, ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      MethodInfoList list = info.getMethodInfo();

      for(MethodInfo entry : list) {
         String name = entry.getName();

         if(entry.isPublic() && !name.startsWith("<")) {
            nodes.add(new MethodIndexNode(node, entry));
         }
      }
      return nodes;
   }

   private static Set<IndexNode> getFields(ClassIndexNode node, ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      FieldInfoList list = info.getFieldInfo();

      for(FieldInfo entry : list) {
         if(entry.isPublic()) {
            nodes.add(new FieldIndexNode(node, entry));
         }
      }
      return nodes;
   }

   private static Set<IndexNode> getInnerClasses(ClassIndexNode node, ClassInfo info) {
      Set<IndexNode> nodes = new HashSet<IndexNode>();
      ClassInfoList list = info.getInnerClasses();
      ClassIndexNodePath path = node.getPath();

      for(ClassInfo entry : list) {
         nodes.add(new ClassIndexNode(path, entry));
      }
      return nodes;
   }


}
