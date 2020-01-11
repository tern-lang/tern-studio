package org.ternlang.studio.message.idl.codegen.struct;

import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.Package;

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
      
      appender.append("public ");
      appender.append(category);
      appender.append(" ");
      appender.append(name);
      appender.append("Codec implements ");
      appender.append(name);
      appender.append("Option, ");      
      appender.append(name);
      appender.append("Builder {");
      generateBody();
      appender.append("}\n");
   }
   
   @Override
   protected void generateBody() {
      generateFields();    
      generateDefaultMethods();
      generatePropertyMethods();
   }
   
   private void generateFields() {
      Package module = entity.getPackage();
      
      if(module == null) {
         throw new IllegalStateException("Entity has no package");
      }
      appender.append("\n\n");
      
      builder.create(entity).stream().forEach(generator -> {
         generator.generateField(appender);
      });
      appender.append("   private Frame frame;\n");
      appender.append("   private int offset;\n");
      appender.append("   private int length;\n");      
   }
   
   private void generateDefaultMethods() {
      String name = entity.getName();
      
      appender.append("\n");
      appender.append("   public ").append(name).append("Codec with(Frame frame, int offset, int length) {\n");
      appender.append("      this.frame = frame;\n");
      appender.append("      this.offset = offset;\n");
      appender.append("      this.length = length;\n");      
      appender.append("      return this;\n");
      appender.append("   }\n");
      appender.append("\n");
      appender.append("   @Override\n");
      appender.append("   public boolean isPresent() {\n");    
      appender.append("      return length > 0;\n");
      appender.append("   }\n");  
      appender.append("\n");
      appender.append("   @Override\n");
      appender.append("   public ").append(name).append(" get() {\n");    
      appender.append("      return length > 0 ? this : null;\n");
      appender.append("   }\n");        
   }

   private void generatePropertyMethods() {
      builder.create(entity).stream().forEach(generator -> {
         appender.append("\n");
         generator.generateGetter(appender);
         appender.append("\n");
         generator.generateSetter(appender);
      });
   }
}