package tern.studio.service.tree;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TreeNode {

   private final Comparator<File> comparator;
   private final File file; 
   private final String project;
   private final String path; 
   private final String indent;
   private final String prefix;
   private final String id; 
   private final int depth; 
   private final boolean expand;
   private final boolean root;
   
   private TreeNode(Builder builder) {
      this.comparator = new TreeFileComparator();
      this.project = builder.project;
      this.file = builder.file;
      this.path = builder.path;
      this.indent = builder.indent;
      this.prefix = builder.prefix;
      this.id = builder.id;
      this.depth = builder.depth;
      this.expand = builder.expand;
      this.root = builder.root;
   }
   
   public List<File> getFiles() {
      File[] list = file.listFiles();
      
      if(list != null) {
         Arrays.sort(list, comparator);
         return Arrays.asList(list);
      }
      return Collections.emptyList();
   }
   
   public File getFile() {
      return file;
   }
   
   public String getName() {
      return file.getName();
   }
   
   public String getProject() {
      return project;
   }
   
   public String getPath() {
      return path;
   }

   public String getIndent() {
      return indent;
   }

   public String getPrefix() {
      return prefix;
   }

   public String getId() {
      return id;
   }

   public int getDepth() {
      return depth;
   }
   
   public boolean isDirectory(){
      return file.isDirectory();
   }

   public boolean isExpand() {
      return expand;
   }
   
   public boolean isRoot() {
      return root;
   }

   public static class Builder {
      
      private File file; 
      private String project;
      private String path; 
      private String indent;
      private String prefix;
      private String id; 
      private int depth; 
      private boolean expand;
      private boolean root;
      
      public Builder(File file) {
         this.file = file;
      }

      public Builder withFile(File file) {
         this.file = file;
         return this;
      }

      public Builder withPath(String path) {
         this.path = path;
         return this;
      }
      
      public Builder withProject(String project) {
         this.project = project;
         return this;
      }

      public Builder withIndent(String indent) {
         this.indent = indent;
         return this;
      }

      public Builder withPrefix(String prefix) {
         this.prefix = prefix;
         return this;
      }

      public Builder withId(String id) {
         this.id = id;
         return this;
      }

      public Builder withDepth(int depth) {
         this.depth = depth;
         return this;
      }

      public Builder withExpand(boolean expand) {
         this.expand = expand;
         return this;
      }
      
      public Builder withRoot(boolean root) {
         this.root = root;
         return this;
      }
      
      public TreeNode build() {
         return new TreeNode(this);
      }
      
   }
}