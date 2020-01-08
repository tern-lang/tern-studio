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
import org.ternlang.studio.message.idl.codegen.CodeTemplate;

public class StructBuilder extends CodeTemplate {

   public StructBuilder(Domain domain, Entity entity) {
      super(domain, entity);
   }
   
   @Override
   protected String name() {
      return entity.getName() + "Builder";
   }   

   @Override
   protected String category() {
      return "interface";
   }
   
   @Override
   protected void generateEntity() {
      String name = entity.getName();
      String category = category();      
      
      builder.append("public ");
      builder.append(category);
      builder.append(" ");
      builder.append(name);
      builder.append("Builder implements ");
      builder.append(name);
      builder.append(" {");
      generateBody();
      builder.append("}\n");
   }
   
   @Override
   protected void generateBody() {
      generateSignatures();
   }
   
   private void generateSignatures() {
      Package module = entity.getPackage();
      
      if(module == null) {
         throw new IllegalStateException("Entity has no package");
      }
      Scope scope = module.getScope();
      ScopeState state = scope.getState();
      
      entity.getProperties().forEach(property -> {
         String constraint = property.getConstraint();
         Value value = state.getValue(constraint);
         
         if(value != null) {
            Entity entity = value.getValue();
            EntityType type = entity.getType();
            
            generateSetter(property, type);
         } else {
            generateSetter(property, PRIMITIVE);
         }
      });
      
      builder.append("\n");
   }
   
   private void generateSetter(Property property, EntityType type) {
      String constraint = property.getConstraint();
      String name = property.getName();
      String entity = name();
      
      builder.append("\n");
      builder.append("   /**").append("\n");
      builder.append("    * Updates ").append(name).append("\n");
      builder.append("    * @param ").append(name).append(" value to update to").append("\n");
      builder.append("    * @returns builder to use").append("\n");
      builder.append("    */").append("\n");     
      
      char first = constraint.charAt(0);
      String remainder = constraint.substring(1);
      
      builder.append("   ");
      
      if(property.isArray()) {        
         if(constraint.equals("char")) {
            builder.append(entity);
            builder.append(" ").append(name).append("(");            
            builder.append("CharSequence"); 
            builder.append(" ");
            builder.append(name);
            builder.append(");");
            builder.append("\n");
         } else {
            builder.append(Character.toUpperCase(first));
            builder.append(remainder);
            builder.append("ArrayBuilder");
            builder.append(" ").append(name).append("();");
            builder.append("\n");
         }
      } else {    
         if(!type.isPrimitive() && !type.isEnum()) {  
            builder.append(Character.toUpperCase(first));
            builder.append(remainder);
            builder.append("Builder");
            builder.append(" ").append(name).append("();");
            builder.append("\n");
         } else {
            builder.append(entity);
            builder.append(" ").append(name).append("(");            
            builder.append(constraint); 
            builder.append(" ");
            builder.append(name);
            builder.append(");");
            builder.append("\n");
         }
      }
   }
}

