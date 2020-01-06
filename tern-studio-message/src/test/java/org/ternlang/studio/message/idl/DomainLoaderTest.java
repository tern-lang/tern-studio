package org.ternlang.studio.message.idl;

import java.net.URL;
import java.util.Iterator;

import org.ternlang.studio.common.ClassPathPatternScanner;

import junit.framework.TestCase;

public class DomainLoaderTest extends TestCase {

   public static void main(String[] args) throws Exception {
      Iterator<URL> resources = ClassPathPatternScanner.scan("**/*.idl");
      Domain domain = DomainLoader.load(resources);
      
      assertFalse(domain.getPackages().isEmpty());
   }
}
