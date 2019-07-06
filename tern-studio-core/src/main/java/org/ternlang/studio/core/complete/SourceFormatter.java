package org.ternlang.studio.core.complete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simpleframework.module.annotation.Component;
import org.ternlang.core.Reserved;
import org.ternlang.core.link.ImportPathResolver;
import org.ternlang.studio.project.Project;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SourceFormatter {

   private static final String JSON_EXTENSION = ".json";
   private static final String XML_EXTENSION = ".xml";

   private final ImportPathResolver resolver;
   private final PrettyPrinter printer;
   private final ObjectMapper mapper;

   public SourceFormatter(){
      this.resolver = new ImportPathResolver(Reserved.IMPORT_FILE); 
      this.printer = new DefaultPrettyPrinter();
      this.mapper = new ObjectMapper();
   }
   
   public String format(Project project, String path, String source, int indent) throws Exception {
      String resource = path.toLowerCase();
      
      if(resource.endsWith(JSON_EXTENSION)) {
         Object object = mapper.readValue(source, Object.class);
         return mapper.writer(printer).writeValueAsString(object);
      }
      return formatSource(project, path, source, indent);
   }
   
   private String formatSource(Project project, String path, String source, int indent) throws Exception {
      List<SourceLine> lines = resolveSourceLines(source);
      String imports = resolveImports(source);
      String pad = "";
      
      for(int i = 0; i < indent; i++) {
         pad += " ";
      }
      StringBuilder builder = new StringBuilder(imports);
      
      if(!lines.isEmpty()){
         String previous = "";
         
         builder.append("\n");
         
         for(SourceLine line : lines) {
            String prefix = line.getIndent();
            String remainder = line.getText();
            
            if(!remainder.isEmpty()) {
               float length = prefix.length();
               float factor = length / indent;
               
               if(length > 0) {
                  int count = Math.round(factor);
                  
                  for(int i = 0; i < count; i++) {
                     builder.append(pad);
                  }
                  builder.append(remainder);
               } else {
                  builder.append(remainder);
               }
               builder.append("\n");
            } else {
               if(!previous.isEmpty()) {
                  builder.append("\n");
               }
            }
            previous = remainder.trim();
         }
         return builder.toString();
      }
      return source;
   }
   
   private List<SourceLine> resolveSourceLines(String source) {
      Pattern ignore = Pattern.compile("import\\s+(.*)\\s*;\\s*$");
      Pattern indents = Pattern.compile("^(\\s+)(.*)$");
      String lines[] = source.split("\\r?\\n");

      if(lines.length > 0){
         List<SourceLine> sourceLines = new ArrayList<SourceLine>();
         
         for(String line : lines) {
            if(!ignore.matcher(line).matches()) {
               Matcher matcher = indents.matcher(line);
               
               if(matcher.matches()) {
                  String prefix = matcher.group(1);
                  String remainder = matcher.group(2); 
                  SourceLine sourceLine = new SourceLine(prefix, remainder);
                  
                  sourceLines.add(sourceLine);
               } else {
                  SourceLine sourceLine = new SourceLine("", line);
                  
                  sourceLines.add(sourceLine);
               }
            }
         }
         return sourceLines;
      }
      return Collections.emptyList();
   }
   
   private SourceImport resolveImport(String type) {
      int length = type.length();
      int dotIndex = type.lastIndexOf(".");
      int asIndex = type.lastIndexOf(" as ");
      
      if(asIndex != -1) {
         String alias = type.substring(asIndex + 1, length).trim();
         String qualified = type.substring(0, asIndex).trim();
         String resolved = resolver.resolveName(qualified);
         
         return new SourceImport(resolved, alias, true);
      }
      if(dotIndex != -1) {
         String alias = type.substring(dotIndex + 1, length).trim();
         String qualified = type.trim();
         String resolved = resolver.resolveName(qualified);
         
         return new SourceImport(resolved, alias, false);
      }
      return null;
   }
   
   private String resolveImports(String source) throws Exception {
      Pattern imports = Pattern.compile("import\\s+(.*)\\s*;\\s*$");
      String lines[] = source.split("\\r?\\n");

      if(lines.length > 0){
         Set<String> imported = new TreeSet<String>();
         StringBuilder builder = new StringBuilder();

         for(String line : lines) {
            Matcher matcher = imports.matcher(line);
            
            if(matcher.matches()) {
               String type = matcher.group(1);
               SourceImport sourceImport = resolveImport(type);
               
               if(sourceImport == null) {
                  imported.add(line);
               } else {
                  for(String other : lines) {
                     String real = sourceImport.getType();
                     String alias = sourceImport.getAlias();
                     
                     if(other.contains(alias) && !imports.matcher(other).matches()) {
                        if(sourceImport.isAliased()) {
                           imported.add("import " + real + " as " + alias + ";");
                        } else {
                           imported.add("import " + real + ";");
                        }
                     }
                  }
               }
            } 
         }
         for(String line : imported) {
            builder.append(line);
            builder.append("\n");
         }
         return builder.toString();
      }
      return "";
   }
   
   private static class SourceImport {
      
      private final String type;
      private final String alias;
      private final boolean aliased;
      
      public SourceImport(String type, String alias, boolean aliased) {
         this.aliased = aliased;
         this.type = type;
         this.alias = alias;
      }
      
      public boolean isAliased() {
         return aliased;
      }

      public String getType() {
         return type;
      }

      public String getAlias() {
         return alias;
      }
   }
   
   private static class SourceLine {
      
      private final String indent;
      private final String text;
      
      public SourceLine(String indent, String text) {
         this.indent = indent;
         this.text = text;
      }

      public String getIndent() {
         return indent;
      }

      public String getText() {
         return text;
      }
   }
}