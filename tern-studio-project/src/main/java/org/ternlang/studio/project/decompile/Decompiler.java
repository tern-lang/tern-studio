package org.ternlang.studio.project.decompile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;

public class Decompiler {
   
   private static final String DECOMPILE_DIRECTORY = "decompile";
   
   private final File outputDir;
   private final boolean includeComment;

   public Decompiler(File outputDir) {
      this(outputDir, false);
   }
   
   public Decompiler(File outputDir, boolean includeComment) {
      this.outputDir = new File(outputDir, DECOMPILE_DIRECTORY);
      this.includeComment = includeComment;
   }
   
   public String decompile(String jarFile, String className) throws Exception{
      File extractedClassFile = extractClassFromJar(jarFile, className);
      
      if(extractedClassFile != null) {
         decompileClasses(jarFile, extractedClassFile);
         String javaFile = getJavaFullPath(className);
         File rootDir = outputDir.getCanonicalFile();
         
         if(!rootDir.exists()) {
            rootDir.mkdirs();
         }
         File decompiledJavaFile = new File(rootDir, javaFile);
         InputStream stream = new FileInputStream(decompiledJavaFile);
         Date time = new Date();
         
         try {
            String source = IOUtils.toString(stream);
            
            if(includeComment) {
               return "// Decompiled on " + time + "\n\n" + source;
            }
            return source;
         }finally {
            try {
               stream.close();
            } finally {
               decompiledJavaFile.delete();
               extractedClassFile.delete();
            }
         }
      }
      return "// could not decompile " + className;
   }
   
   private void decompileClasses(String jarFile, File extractedClassFile) throws Exception {  
      File file = new File(jarFile);
      
      if(file.exists() && extractedClassFile.exists()) {
         Map<String, Object> options = new HashMap<String, Object>();
         options.put("dgs", "1");
         File out = outputDir.getCanonicalFile();
         ConsoleDecompiler decompiler = new ConsoleDecompiler(out, options);
         decompiler.addSpace(out, true);
         decompiler.decompileContext();
      }
   }
   
   private File extractClassFromJar(String jarFile, String className) throws Exception {
      File file = new File(jarFile);
      
      if(file.exists()) {
         URL[] classPaths = new URL[]{file.toURI().toURL()};
         ClassLoader loader = new URLClassLoader(classPaths,
               ClassLoader.getSystemClassLoader().getParent());
         String path = getClassFullPath(className);
         File outputFile = new File(outputDir, path).getCanonicalFile();
         InputStream stream = loader.getResourceAsStream(path);
         
         if(stream != null) {
            outputFile.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(outputFile);
            
            try {
               byte[] b = IOUtils.toByteArray(stream);
               out.write(b);
            }finally {
               out.close();
            }
            return outputFile;
         }
      }
      return null;
   }
   
   private static String getJavaFullPath(String className) {
      return className.replace('.', '/') + ".java";
   }
   
   private static String getClassFullPath(String className) {
      return className.replace('.', '/') + ".class";
   }
}
