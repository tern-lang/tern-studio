package org.ternlang.studio.message.idl.codegen.struct;

import static org.ternlang.studio.message.idl.EntityType.PRIMITIVE;

import org.ternlang.core.scope.Scope;
import org.ternlang.core.scope.ScopeState;
import org.ternlang.core.variable.Value;
import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.EntityType;
import org.ternlang.studio.message.idl.Package;
import org.ternlang.studio.message.idl.Property;

public class StructCodec extends StructInterface {

   public StructCodec(Domain domain, Entity entity) {
      super(domain, entity);
   }
   
   @Override
   protected String name() {
      return entity.getName() + "Codec";
   }   

   @Override
   protected String category() {
      return "class";
   }
   
   @Override
   protected void generateEntity() {
      String name = entity.getName();
      String category = category();      
      
      builder.append("public ");
      builder.append(category);
      builder.append(" ");
      builder.append(name);
      builder.append("Codec implements ");
      builder.append(name);
      builder.append("Option, ");      
      builder.append(name);
      builder.append("Builder {");
      generateBody();
      builder.append("}\n");
   }
   
   @Override
   protected void generateBody() {
      generateFields();    
      generateDefaultMethods();
      generateProperties();
   }
   
   private void generateFields() {
      Package module = entity.getPackage();
      
      if(module == null) {
         throw new IllegalStateException("Entity has no package");
      }
      Scope scope = module.getScope();
      ScopeState state = scope.getState();
      
      builder.append("\n\n");
      
      entity.getProperties().forEach(property -> {
         String constraint = property.getConstraint();
         Value value = state.getValue(constraint);
         char first = constraint.charAt(0);
         String remainder = constraint.substring(1);
         String name = Character.toUpperCase(first) + remainder;  
                           
         if(property.isArray()) {
            generateField(property, name + "ArrayCodec");
         } else {
            if(value != null) {
               Entity entity = value.getValue();
               EntityType type = entity.getType();
               
               if(type.isStruct() || type.isUnion()) {
                  generateField(property, name + "Codec");
               }
            }
         } 
      });      
      builder.append("   private Frame frame;\n");
      builder.append("   private int off;\n");
      builder.append("   private int length;\n");      
   }
   
   private void generateField(Property property, String type) {
      String name = property.getName();
      
      builder.append("   private ");
      builder.append(type);
      builder.append(" ");
      builder.append(name);
      builder.append(" = new ");
      builder.append(type);
      builder.append("();\n");  
   }
   
   private void generateDefaultMethods() {
      String name = entity.getName();
      
      builder.append("\n");
      builder.append("   public ").append(name).append("Encoder with(Frame frame, int off, int length) {\n");
      builder.append("      this.frame = frame;\n");
      builder.append("      this.off = off;\n");
      builder.append("      this.length = length;\n");      
      builder.append("      return this;\n");
      builder.append("   }\n");
      builder.append("\n");
      builder.append("   @Override\n");
      builder.append("   public boolean isPresent() {\n");    
      builder.append("      return length > 0;\n");
      builder.append("   }\n");  
      builder.append("\n");
      builder.append("   @Override\n");
      builder.append("   public ").append(name).append(" get() {\n");    
      builder.append("      return length > 0 ? this : null;\n");
      builder.append("   }\n");        
   }
}