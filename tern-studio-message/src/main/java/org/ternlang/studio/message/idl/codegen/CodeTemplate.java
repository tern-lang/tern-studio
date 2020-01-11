package org.ternlang.studio.message.idl.codegen;

import org.ternlang.core.scope.Scope;
import org.ternlang.core.scope.ScopeState;
import org.ternlang.core.variable.Value;
import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.Package;
import org.ternlang.studio.message.idl.codegen.build.PropertyGeneratorBuilder;

public abstract class CodeTemplate {

   protected final PropertyGeneratorBuilder builder;
   protected final CodeAppender appender;
   protected final Domain domain;
   protected final Entity entity;
   
   public CodeTemplate(Domain domain, Entity entity) {
      this.builder = new PropertyGeneratorBuilder(domain);
      this.appender = new CodeAppender();
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
      String source = appender.toString();
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

      appender.append("public ");
      appender.append(category);
      appender.append(" ");
      appender.append(name);
      appender.append(" {\n");
      generateBody();
      appender.append("}\n");
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

               appender.append("import ");
               appender.append(namespace);
               appender.append(".");
               appender.append(constraint);
               appender.append(";\n");
            }
         }
      });
      appender.append("import java.nio.ByteOrder;\n");
      appender.append("import org.ternlang.studio.message.ByteSize;\n");
      appender.append("import org.ternlang.studio.message.Frame;\n");
      appender.append("import org.ternlang.studio.message.primitive.*;\n");
      appender.append("\n");
   }
   
   protected void generatePackage() {
      Package module = entity.getPackage();
      
      if(module == null) {
         throw new IllegalStateException("Entity has no package");
      }
      String namespace = module.getName();

      appender.append("// Generated Code\n");
      appender.append("package ");
      appender.append(namespace);
      appender.append(";\n\n");
   }

   protected abstract String name();
   protected abstract String category();
   protected abstract void generateBody();
}
