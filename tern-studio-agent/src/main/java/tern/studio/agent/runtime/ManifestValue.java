package tern.studio.agent.runtime;

import java.io.InputStream;
import java.util.jar.Manifest;

public abstract class ManifestValue implements RuntimeValue {

   private static final String MANIFEST_FILE = "META-INF/MANIFEST.MF";

   protected Manifest getManifest() {
      try {
         InputStream resource = VersionValue.class.getResourceAsStream(MANIFEST_FILE);

         if(resource == null) {
            resource = VersionValue.class.getResourceAsStream("/" + MANIFEST_FILE);
         }
         return new Manifest(resource);
      } catch(Exception e) {
         throw new IllegalStateException("Could not read manifest file", e);
      }
   }
}
