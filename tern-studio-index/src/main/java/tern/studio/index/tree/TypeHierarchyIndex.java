package tern.studio.index.tree;

import static tern.studio.index.IndexType.SUPER;

import tern.core.Compilation;
import tern.core.module.Module;
import tern.core.module.Path;
import tern.core.scope.Scope;
import tern.core.type.Type;
import tern.studio.index.IndexResult;
import tern.tree.constraint.ClassConstraint;
import tern.tree.constraint.TraitConstraint;
import tern.tree.define.ClassHierarchy;

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
