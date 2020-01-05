package org.ternlang.studio.message.idl.codegen;

import org.ternlang.studio.common.ClassPathPatternScanner;

public class CodeGen {

   public static void main(String[] list) throws Exception {
      ClassPathPatternScanner.scan("**/*.idl").forEachRemaining(System.err::println);
   }
}
