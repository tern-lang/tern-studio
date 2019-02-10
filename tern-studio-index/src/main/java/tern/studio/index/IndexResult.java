package tern.studio.index;

import tern.core.module.Path;

public class IndexResult implements Index {

   private IndexType type;
   private Object operation;
   private String constraint;
   private String module;
   private String name;
   private Path path;
   private int line;
   
   public IndexResult(IndexType type, Object operation, String constraint, String module, String name, Path path, int line) {
      this.constraint = constraint;
      this.operation = operation;
      this.module = module;
      this.type = type;
      this.name = name;
      this.path = path;
      this.line = line;
   }
   
   @Override
   public IndexType getType() {
      return type;
   }
   
   @Override
   public String getConstraint() {
      return constraint;
   }

   @Override
   public Object getOperation() {
      return operation;
   }
   
   public void setOperation(Object operation) {
      this.operation = operation;
   }

   @Override
   public String getName() {
      return name;
   }
   
   @Override
   public String getModule() {
      return module;
   }

   @Override
   public Path getPath() {
      return path;
   }

   @Override
   public int getLine() {
      return line;
   }
   
   @Override
   public String toString() {
      return String.format("%s -> %s", name, type);
   }
   
}
