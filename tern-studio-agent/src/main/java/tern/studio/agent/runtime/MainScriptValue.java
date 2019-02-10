package tern.studio.agent.runtime;

import static tern.studio.agent.runtime.RuntimeAttribute.SCRIPT;

import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class MainScriptValue extends ManifestValue {

   public static final String MAIN_SCRIPT = "Main-Script";

   @Override
   public String getName() {
      return SCRIPT.name;
   }

   @Override
   public String getValue() {
      Attributes.Name key = new Attributes.Name(MAIN_SCRIPT);
      Manifest manifest = getManifest();

      return (String)manifest.getMainAttributes().get(key);
   }
}
