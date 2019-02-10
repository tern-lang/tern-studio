package tern.studio.common.find.file;

import java.io.File;

public class FileMatch implements Comparable<FileMatch> {

   private final String resource;
   private final String project;
   private final File file;
   private final String text;
   private final String name;
   private final String path;
   
   public FileMatch(String project, String resource, String path, String name, File file, String text) {
      this.resource = resource;
      this.project = project;
      this.file = file;
      this.text = text;
      this.name = name;
      this.path = path;
   }

   @Override
   public int compareTo(FileMatch other) {
      return resource.compareTo(other.resource);
   }

   public String getName() {
      return name;
   }

   public String getPath() {
      return path;
   }

   public File getFile(){
      return file;
   }
   
   public String getText(){
      return text;
   }

   public String getProject() {
      return project;
   }
   
   public String getResource() {
      return resource;
   }

}