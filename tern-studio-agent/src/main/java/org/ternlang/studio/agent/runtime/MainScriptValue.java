package org.ternlang.studio.agent.runtime;

import static org.ternlang.studio.agent.runtime.RuntimeAttribute.MAIN_SCRIPT;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class MainScriptValue extends ManifestValue {

   public static final String APPLICATION_SCRIPT = "Main-Script";

   @Override
   public String getName() {
      return MAIN_SCRIPT.name;
   }

   @Override
   public String getValue() {
      Attributes.Name key = new Attributes.Name(APPLICATION_SCRIPT);
      Manifest manifest = getManifest();

      return (String)manifest.getMainAttributes().get(key);
   }
}
