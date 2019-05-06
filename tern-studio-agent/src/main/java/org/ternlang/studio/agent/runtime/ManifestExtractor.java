package org.ternlang.studio.agent.runtime;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class ManifestExtractor {

   public static final String MANIFEST_FILE = "META-INF/MANIFEST.MF";

   public static Manifest extractFromJar(String path) throws Exception {
      File file = new File(path);

      if(file.exists()) {
         JarFile jarFile = new JarFile(path);
         Enumeration<JarEntry> entries = jarFile.entries();

         while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();

            if (entry.getName().equals(MANIFEST_FILE)) {
               InputStream input = jarFile.getInputStream(entry);

               try {
                  return new Manifest(input);
               } finally {
                  input.close();
               }
            }
         }
      }
      return null;
   }

   public static Manifest extractFromLoader(ClassLoader loader) throws Exception {
      URL resource = loader.getResource(MANIFEST_FILE);

      if(resource == null) {
         resource = loader.getResource("/" + MANIFEST_FILE);
      }
      if(resource != null) {
         InputStream source = resource.openStream();

         try {
            return new Manifest(source);
         } finally {
            source.close();
         }
      }
      return null;
   }

   public static void main(String[] name) throws Exception {
      Manifest m = extractFromJar("/Users/niallg/Work/development/tern-lang/tern-studio/tern-studio/target/ternd.jar");
      String s = MainClassValue.getValue(m);
      System.err.println(s);
   }
}
