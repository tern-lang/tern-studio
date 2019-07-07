package org.ternlang.studio.core.complete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simpleframework.module.annotation.Component;
import org.ternlang.core.Reserved;
import org.ternlang.core.link.ImportPathResolver;
import org.ternlang.studio.project.Project;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
      
      try {
         if(resource.endsWith(JSON_EXTENSION)) {
            Object object = mapper.readValue(source, Object.class);
            return mapper.writer(printer).writeValueAsString(object);
         }
         return formatSource(project, path, source, indent);
      } catch(Exception e) {
         log.info("Could not format source for {}", path, e);
      }
      return source;
   }
   
   private String formatSource(Project project, String path, String source, int indent) throws Exception {
      List<SourceLine> lines = resolveSourceLines(source);
      String imports = formatImports(source);
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
      int starIndex = type.lastIndexOf("*");
      
      if(starIndex != -1) {
         String imported = String.format("import %s;", type);
         return new SourceImport(imported, imported, imported, false, true);
      }
      if(asIndex != -1) {
         String alias = type.substring(asIndex + 4, length).trim();
         String qualified = type.substring(0, asIndex).trim();
         String resolved = resolver.resolveName(qualified);
         
         if(dotIndex != -1) {
            String module = type.substring(0, dotIndex).trim();
            return new SourceImport(resolved, alias, module, true, false);
         }
         return new SourceImport(resolved, alias, "", true, false);
      }
      if(dotIndex != -1) {
         String module = type.substring(0, dotIndex).trim();
         String alias = type.substring(dotIndex + 1, length).trim();
         String qualified = type.trim();
         String resolved = resolver.resolveName(qualified);
         
         return new SourceImport(resolved, alias, module, false, false);
      }
      return new SourceImport(type, type, type, false, true);
   }

   private String formatImports(String source) throws Exception {
      Set<SourceImport> imports = resolveImports(source);
      
      if(!imports.isEmpty()) {
         Set<String> done = new TreeSet<String>();
         StringBuilder builder = new StringBuilder();
         
         for(SourceImport imported : imports) {
            String type = imported.getType();
            String alias = imported.getAlias();
            
            if(imported.isVerbatim()) {
               builder.append(type);
            } else {
               if(done.add(alias)) {
                  builder.append("import ");
                  builder.append(type);
                  
                  if(imported.isAliased()) {
                     builder.append(" as ");
                     builder.append(alias);
                  } 
                  builder.append(";");
               }
            }
            builder.append("\n");
         }
         return builder.toString();
      }
      return "";
   }
   
   private Set<SourceImport> combineImports(Set<SourceImport> imports) throws Exception {
      Set<SourceImport> combined = new LinkedHashSet<SourceImport>();
      Map<String, Set<SourceImport>> groups = new TreeMap<String, Set<SourceImport>>();
      Function<String, Set<SourceImport>> builder = (name) -> new LinkedHashSet<SourceImport>();
      
      for(SourceImport imported : imports) {
         String key = imported.getModule();
         Set<SourceImport> group = groups.computeIfAbsent(key, builder);
      
         group.add(imported);
      }
      Set<String> keys = groups.keySet();
      
      for(String key : keys) {
         Set<SourceImport> group = groups.get(key);
         int count = group.size();
         
         if(count > 1) {
            StringBuilder list = new StringBuilder();
            
            for(SourceImport entry : group) {
               String type = entry.getType();
               String alias = entry.getAlias();
               
               if(entry.isAliased()) {
                  list.append("import ");
                  list.append(type);
                  list.append(" as ");
                  list.append(alias);
                  list.append(";");
               
                  String value = list.toString();
                  SourceImport groupImport = new SourceImport(value, value, value, false, true);
                  
                  combined.add(groupImport);
               }
            }
            list.setLength(0);
            
            for(SourceImport entry : group) {
               String module = entry.getModule();
               String alias = entry.getAlias();
               int length = list.length();
               
               if(!entry.isAliased()) {
                  if(length == 0) {
                     list.append("import ");
                     list.append(module);
                     list.append(".{");
                  } else {
                     list.append(", ");
                  }
                  list.append(alias);
               }
            }
            list.append("};");
            
            String value = list.toString();
            SourceImport groupImport = new SourceImport(value, value, value, false, true);
            
            combined.add(groupImport);
         } else {
            combined.addAll(group);
         }
      }
      return combined;
   }
   
   private Set<SourceImport> resolveImports(String source) throws Exception {
      Pattern imports = Pattern.compile("import\\s+(.*)\\s*;\\s*$");
      Pattern importGroups = Pattern.compile("import\\s+([a-zA-Z\\.\\_]+)\\.\\{(.*)\\}\\s*;\\s*$");
      String lines[] = source.split("\\r?\\n");

      if(lines.length > 0){
         Set<SourceImport> referenced = new TreeSet<SourceImport>();

         for(String line : lines) {
            Matcher importMatcher = imports.matcher(line);
            
            if(importMatcher.matches()) {
               Matcher importGroupMatcher = importGroups.matcher(line);
               
               if(importGroupMatcher.matches()) {
                  String module = importGroupMatcher.group(1);
                  String list = importGroupMatcher.group(2);
                  String[] aliases = list.split(",");
                  
                  for(String alias : aliases) {
                     String token = alias.trim();
                     String type = module.trim() + "." +  token;
                     SourceImport sourceImport = resolveImport(type);
                     
                     if(sourceImport.isVerbatim()) {
                        referenced.add(sourceImport);
                     } else {
                        for(String other : lines) {
                           if(other.contains(token) && !imports.matcher(other).matches()) {
                              referenced.add(sourceImport);
                           }
                        }
                     }
                  }
               } else {
                  String type = importMatcher.group(1);
                  SourceImport sourceImport = resolveImport(type);
                  
                  if(sourceImport.isVerbatim()) {
                     referenced.add(sourceImport);
                  } else {
                     for(String other : lines) {
                        String alias = sourceImport.getAlias();
                        
                        if(other.contains(alias) && !imports.matcher(other).matches()) {
                           referenced.add(sourceImport);
                        }
                     }
                  }
               }
            }
         }
         return combineImports(referenced);
      }
      return Collections.emptySet();
   }
   
   private static class SourceImport implements Comparable<SourceImport> {
      
      private final String module;
      private final String type;
      private final String alias;
      private final boolean aliased;
      private final boolean verbatim;
      
      public SourceImport(String type, String alias, String module, boolean aliased, boolean verbatim) {
         this.module = module;
         this.aliased = aliased;
         this.verbatim = verbatim;
         this.type = type;
         this.alias = alias;
      }
      
      @Override
      public int compareTo(SourceImport other) {
         return type.compareTo(other.type);
      }
      
      public boolean isVerbatim() {
         return verbatim;
      }
      
      public boolean isAliased() {
         return aliased;
      }
      
      public String getModule() {
         return module;
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