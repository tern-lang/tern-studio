package org.ternlang.studio.index.classpath.node;

import org.ternlang.studio.index.IndexNode;
import org.ternlang.studio.index.classpath.ClassFile;

public abstract class ClassFileNode implements IndexNode {
   
   protected final ClassFile file;
   protected String absolute;
   protected String resource;
   protected String module;
   
   public ClassFileNode(ClassFile file) {
      this.file = file;
   }
   
   @Override
   public final int getLine() {
      return -1;
   }

   @Override
   public final String getResource(){
      if(resource == null) {
         resource = file.getLibrary();
      }
      return resource;
   }
   
   @Override
   public final String getAbsolutePath(){
      if(absolute == null) {
         absolute = file.getLibraryPath();
      }
      return absolute;
   }
   
   @Override
   public final String getModule() {
      if(module == null) {
         module = file.getModule();
      }
      return module;
   }
   
   @Override
   public final boolean isNative(){
      return true;
   }
   
   @Override
   public String toString(){
      return getFullName();
   }
}

