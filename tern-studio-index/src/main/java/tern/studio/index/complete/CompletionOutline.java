package tern.studio.index.complete;

import tern.studio.index.IndexType;

public class CompletionOutline {

   private String libraryPath; // JAR file defined in
   private String resource; // script file defined in
   private String constraint; // constraint for the method or property
   private String declaringClass; // what class was this defined in
   private IndexType type; // function or property?
   private int line; // line in the file
   
   public CompletionOutline(){
      super();
   }
   
   public CompletionOutline(IndexType type, String constraint, String libraryPath, String declaringClass, String resource,int line) {
      this.libraryPath = libraryPath;
      this.type = type;
      this.declaringClass = declaringClass;
      this.constraint = constraint;
      this.resource = resource;
      this.line = line;
   }

   public String getDeclaringClass() {
      return declaringClass;
   }

   public void setDeclaringClass(String declaringClass) {
      this.declaringClass = declaringClass;
   }

   public String getLibraryPath() {
      return libraryPath;
   }

   public void setLibraryPath(String libraryPath) {
      this.libraryPath = libraryPath;
   }

   public String getResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public String getConstraint() {
      return constraint;
   }

   public void setConstraint(String constraint) {
      this.constraint = constraint;
   }

   public IndexType getType() {
      return type;
   }

   public void setType(IndexType type) {
      this.type = type;
   }

   public int getLine() {
      return line;
   }

   public void setLine(int line) {
      this.line = line;
   }

}
