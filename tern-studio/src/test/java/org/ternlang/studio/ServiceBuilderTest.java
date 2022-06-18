package org.ternlang.studio;

import junit.framework.TestCase;

import java.util.Arrays;

public class ServiceBuilderTest extends TestCase {

   public void testPath() {
      System.getProperties().setProperty("install.home", "c:/Program Files/Tern/app");
      System.err.println(Arrays.toString(StudioServiceBuilder.ServiceType.IDE.getFiles()));
   }
}
