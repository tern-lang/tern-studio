package org.ternlang.studio.agent.runtime;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class ManifestLocator {

   public static Manifest getManifestFile(String... required) {
      List<Callable<Manifest>> sources = ManifestSearcher.getSearchPath();

      for(Callable<Manifest> source : sources) {
         try {
            Manifest manifest = source.call();

            if(manifest != null) {
               int count = 0;

               for(int i = 0; i < required.length; i++) {
                  if (isAttributePresent(manifest, required[i])) {
                     count++;
                  }
               }
               if(count == required.length) {
                  return manifest;
               }
            }
         } catch(Exception e) {}
      }
      return null;
    }

    private static boolean isAttributePresent(Manifest manifest, String name) {
       if(name != null) {
          Attributes attributes = manifest.getMainAttributes();

          if(!attributes.isEmpty()) {
             Attributes.Name key = new Attributes.Name(name);
             Object value = attributes.get(key);

             if(value != null) {
               return true;
             }
          }
       }
       return false;
    }
}
