package org.ternlang.studio.message.idl.codegen;

public class GeneratedFile {

   private final String source;
   private final String path;
   
   public GeneratedFile(String path, String source) {
      this.source = source;
      this.path = path;
   }
   
   public String getSource() {
      return source;
   }
   
   public String getPath() {
      return path;
   }
}
