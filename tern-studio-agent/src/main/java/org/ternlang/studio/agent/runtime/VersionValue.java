package org.ternlang.studio.agent.runtime;

import static org.ternlang.studio.agent.runtime.RuntimeAttribute.VERSION;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class VersionValue extends ManifestValue {

   private static final String IMPLEMENTATION_VERSION = "Implementation-Version";
   private static final String DEFAULT_VERSION = "1.0";

   @Override
   public String getName() {
      return VERSION.name;
   }

   @Override
   public String getValue() {
      Manifest manifest = getManifest();
      return getValue(manifest);
   }

   public static String getValue(Manifest manifest) {
      Attributes.Name key = new Attributes.Name(IMPLEMENTATION_VERSION);

      if(manifest != null) {
         Attributes attributes = manifest.getMainAttributes();
         String version = (String) attributes.get(key);

         return version == null ? DEFAULT_VERSION : version;
      }
      return DEFAULT_VERSION;
   }
}
