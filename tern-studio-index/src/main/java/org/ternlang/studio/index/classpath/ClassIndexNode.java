package org.ternlang.studio.index.classpath;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
      return info.getSimpleName().replace("$", ".");
   }

   @Override
   public String getTypeName() {
      return getFullName();
   }

   @Override
   public String getFullName() {
      return info.getName().replace("$", ".");
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
         set = ClassIndexNodeCollector.collectNodes(this, info);
         nodes.set(set);
      }
      return set;
   }

   @Override
   public String toString() {
      return getFullName();
   }
}
