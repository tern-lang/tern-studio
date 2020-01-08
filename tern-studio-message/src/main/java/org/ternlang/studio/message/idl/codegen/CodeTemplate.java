package org.ternlang.studio.message.idl.codegen;

import org.ternlang.core.scope.Scope;
import org.ternlang.core.scope.ScopeState;
import org.ternlang.core.variable.Value;
import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.Package;

public abstract class CodeTemplate {

   protected final StringBuilder builder;
   protected final Domain domain;
   protected final Entity entity;
   
   public CodeTemplate(Domain domain, Entity entity) {
      this.builder = new StringBuilder();
      this.domain = domain;
      this.entity = entity;
   }
   
   public GeneratedFile generate() {
      generatePackage();
      generateImports();
      generateEntity();      
      return generateFile();
   }
   
   protected GeneratedFile generateFile() {
      String source = builder.toString();
      Package origin = entity.getPackage();
      
      if(origin == null) {
         throw new IllegalStateException("Entity has no package");
      }
      String name = name();
      String namespace = origin.getName();
      String parent = namespace.replace(".", "/");
      String path = parent + "/" + name + ".java";
      
      return new GeneratedFile(path, source);
   }
   
   protected void generateEntity() {
      String name = name();
      String category = category();      
      
      builder.append("public ");
      builder.append(category);
      builder.append(" ");
      builder.append(name);
      builder.append(" {\n");
      generateBody();
      builder.append("}\n");
   }
   
   protected void generateImports() {
      Package module = entity.getPackage();
      
      if(module == null) {
         throw new IllegalStateException("Entity has no package");
      }
      Scope scope = module.getScope();
      ScopeState state = scope.getState();
      
      entity.getProperties().forEach(property -> {
         if(!property.isPrimitive()) {
            String constraint = property.getConstraint();
            Value value = state.getValue(constraint);
            
            if(value != null) {
               Entity entity = value.getValue();
               Package origin = entity.getPackage();
               String namespace = origin.getName();
               
               builder.append("import ");
               builder.append(namespace);
               builder.append(".");
               builder.append(constraint);
               builder.append(";\n");
            }
         }
      });
      
      builder.append("\n");
   }
   
   protected void generatePackage() {
      Package module = entity.getPackage();
      
      if(module == null) {
         throw new IllegalStateException("Entity has no package");
      }
      String namespace = module.getName();
      
      builder.append("// Generated Code\n");
      builder.append("package ");
      builder.append(namespace);
      builder.append(";\n\n");
   }

   protected abstract String name();
   protected abstract String category();
   protected abstract void generateBody();
}
