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

   public String decompile(String className) throws Exception{
      String classResource = getClassFullPath(className);
      URL classUrl = ClassLoader.getSystemResource(classResource);

      if(classUrl != null) {
         File extractedClassFile = extractClassFromURL(classUrl, className);
         return decompile(extractedClassFile, className);
      }
      return "// could not decompile " + className;
   }

   public String decompile(String jarFile, String className) throws Exception {
      File sourceJarFile = new File(jarFile);
      File extractedClassFile = extractClassFromJar(sourceJarFile, className);

      if (extractedClassFile != null) {
         return decompile(extractedClassFile, className);
      }
      return "// could not decompile " + className;
   }

   private String decompile(File extractedClassFile, String className) throws Exception{
      if(extractedClassFile != null) {
         decompileClasses(extractedClassFile);
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
   
   private void decompileClasses(File extractedClassFile) throws Exception {
      if(extractedClassFile.exists()) {
         Map<String, Object> options = new HashMap<String, Object>();
         options.put("dgs", "1");
         File out = outputDir.getCanonicalFile();
         ConsoleDecompiler decompiler = new ConsoleDecompiler(out, options);
         decompiler.addSpace(out, true);
         decompiler.decompileContext();
      }
   }

   private File extractClassFromURL(URL jarUrl, String className) throws Exception {
      if(jarUrl != null) {
         URL[] classPaths = new URL[]{jarUrl};
         ClassLoader loader = new URLClassLoader(classPaths,
                 ClassLoader.getSystemClassLoader().getParent());
         String path = getClassFullPath(className);
         File outputFile = new File(outputDir, path).getCanonicalFile();
         InputStream stream = loader.getResourceAsStream(path);

         if (stream != null) {
            outputFile.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(outputFile);

            try {
               byte[] b = IOUtils.toByteArray(stream);
               out.write(b);
            } finally {
               out.close();
            }
            return outputFile;
         }
      }
      return null;
   }
   
   private File extractClassFromJar(File jarFile, String className) throws Exception {
      if(jarFile.exists()) {
         return extractClassFromURL(jarFile.toURI().toURL(), className);
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
