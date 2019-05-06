package org.ternlang.studio.agent.runtime;

import java.util.jar.Manifest;

public abstract class ManifestValue implements RuntimeValue {

   protected Manifest getManifest() {
      try {
         return ManifestLocator.getManifestFile();
      } catch (Exception e) {
         throw new IllegalStateException("Could not read manifest file", e);
      }
   }
}

