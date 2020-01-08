package org.ternlang.studio.message.idl.codegen;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ternlang.core.type.extend.FileExtension;
import org.ternlang.studio.common.ClassPathPatternScanner;
import org.ternlang.studio.message.idl.Domain;
import org.ternlang.studio.message.idl.DomainLoader;
import org.ternlang.studio.message.idl.Entity;
import org.ternlang.studio.message.idl.EntityType;
import org.ternlang.studio.message.idl.codegen.struct.StructArray;
import org.ternlang.studio.message.idl.codegen.struct.StructArrayBuilder;
import org.ternlang.studio.message.idl.codegen.struct.StructBuilder;
import org.ternlang.studio.message.idl.codegen.struct.StructCodec;
import org.ternlang.studio.message.idl.codegen.struct.StructInterface;
import org.ternlang.studio.message.idl.codegen.struct.StructOption;

public class CodeGenerator {

   public static void main(String[] list) throws Exception {
      File root = new File("C:\\Work\\development\\tern-lang\\tern-studio\\tern-studio-message");      
      generate(root);
   }
   
   public static void generate(File root) throws Exception {
      File output = new File(root, "target/generated-sources");
      FileExtension extension = new FileExtension();
      
      if(!output.exists()) {
         output.mkdirs();
      }      
      Iterator<URL> resources = ClassPathPatternScanner.scan("**/*.idl");
      Domain domain = DomainLoader.load(resources);      
      
      domain.getEntities().forEach(entity -> {
         List<CodeTemplate> templates = resolve(domain, entity);
         
         for(CodeTemplate template : templates) {
            GeneratedFile result = template.generate();
            String path = result.getPath();
            String source = result.getSource();
            
            try {
               File file = new File(output, path);               
               
               file.getParentFile().mkdirs();
               extension.writeText(file, source);
            } catch(Exception e) {
               throw new IllegalStateException("Could not generate " + path, e);
            }
         }
      });
   }
   
   private static List<CodeTemplate> resolve(Domain domain, Entity entity) {
      List<CodeTemplate> templates = new ArrayList<CodeTemplate>();
      EntityType type = entity.getType();
      
      if(type.isStruct()) {
         templates.add(new StructBuilder(domain, entity));
         templates.add(new StructOption(domain, entity));         
         templates.add(new StructArray(domain, entity));   
         templates.add(new StructArrayBuilder(domain, entity));         
         templates.add(new StructInterface(domain, entity));
         templates.add(new StructCodec(domain, entity));  
      }
      if(type.isEnum()) {
         templates.add(new EnumClass(domain, entity));
      }
      if(type.isUnion()) {
         templates.add(new UnionInterface(domain, entity));
      }
      return templates;
   }
}
