package org.ternlang.studio.common.find.text;

public class TextMatch implements Comparable<TextMatch> {

   private final String resource;
   private final String project;
   private final String text;
   private final int line;
   
   public TextMatch(String project, String resource, String text, int line) {
      this.resource = resource;
      this.project = project;
      this.line = line;
      this.text = text;
   }

   @Override
   public int compareTo(TextMatch other) {
      if(!resource.equals(other.resource)) {
         return resource.compareTo(other.resource);
      }
      if(other.line != line) {
         return line < other.line ? -1 : 1;
      }
      return text.compareTo(other.text);
   }

   public int getLine() {
      return line;
   }

   public String getText() {
      return text;
   }

   public String getProject() {
      return project;
   }
   
   public String getResource() {
      return resource;
   }
}