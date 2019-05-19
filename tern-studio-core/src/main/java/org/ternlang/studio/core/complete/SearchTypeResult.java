package org.ternlang.studio.core.complete;

public class SearchTypeResult {

   private final String module;
   private final String name;
   private final String type;
   private final String resource;
   private final String extra;
   
   public SearchTypeResult(String name, String module, String type, String resource, String extra) {
      this.resource = resource;
      this.module = module;
      this.name = name;
      this.type = type;
      this.extra = extra;
   }

   public String getName() {
      return name;
   }

   public String getType() {
      return type;
   }

   public String getResource() {
      return resource;
   }

   public String getModule() {
      return module;
   }

   public String getExtra() {
      return extra;
   }
}
