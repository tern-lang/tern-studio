package org.ternlang.studio.index.complete;

import junit.framework.TestCase;

public class SourceCodeCleanerTest extends TestCase {

   private static final String SOURCE =
   "class TextBuffer {\n"+
   "   var stringBuilder: StringBuilder;\n"+
   "   append(source: String, offset: Integer, length: Integer) { // some comment\n"+
   "      var blah: String = 'xx';\n"+
   "      try { /* this is a comment */ \n"+
   "         var y: Long = 11l;\n"+
   "         var z = 12;\n"+
   "         // replace me\n"+
   "         source = source.substring(1);\n"+
   "      } catch(e) {\n"+
   "         var m = e.getMessage();\n"+
   "         e.printStackTrace();\n"+
   "      }\n"+
   "   }\n"+
   "}\n";
   
   public void testCodeCleaner() {
      char[] array = SOURCE.toCharArray();
      CommentStripper cleaner = new CommentStripper(array);
      String source = cleaner.clean();
      
      System.err.println(source);
      System.err.println(SOURCE);
   }
}
