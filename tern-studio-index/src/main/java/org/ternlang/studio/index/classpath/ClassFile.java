package org.ternlang.studio.index.classpath;

public interface ClassFile {
   ClassOrigin getOrigin();
   ClassCategory getCategory();
   String getLibraryPath();
   String getLibrary();
   String getResource();
   String getFullName();
   String getTypeName();   
   String getShortName();
   String getModule();
   Class loadClass();
   int getModifiers();
}
