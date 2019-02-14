package org.ternlang.studio.index.tree;

import static org.ternlang.studio.index.IndexType.SUPER;

import org.ternlang.core.Compilation;
import org.ternlang.core.module.Module;
import org.ternlang.core.module.Path;
import org.ternlang.core.scope.Scope;
import org.ternlang.core.type.Type;
import org.ternlang.studio.index.IndexResult;
import org.ternlang.tree.constraint.ClassConstraint;
import org.ternlang.tree.constraint.TraitConstraint;
import org.ternlang.tree.define.ClassHierarchy;

public class TypeHierarchyIndex implements Compilation {
   
   private final ClassHierarchy hierarchy;
   private final ClassConstraint name;
   
   public TypeHierarchyIndex(TraitConstraint... traits) {
      this(null, traits);     
   }
   
   public TypeHierarchyIndex(ClassConstraint name, TraitConstraint... traits) {
      this.hierarchy = new ClassHierarchy(name, traits);
      this.name = name;
   }

   @Override
   public Object compile(Module module, Path path, int line) throws Exception {
      if(name != null) {
         Scope scope = module.getScope();
         Type constraint = name.getType(scope);
         String type = String.valueOf(constraint);
         String prefix = module.getName();
         
         return new IndexResult(SUPER, hierarchy, null, prefix, type, path, line);
      }
      return hierarchy;
   }
}
