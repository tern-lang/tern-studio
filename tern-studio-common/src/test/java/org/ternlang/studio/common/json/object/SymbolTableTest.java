package org.ternlang.studio.common.json.object;

import org.ternlang.studio.common.json.PerfTestCase;

public class SymbolTableTest extends PerfTestCase {

   public void testSymbolTable() throws Exception {
      final SymbolTable<String> table = new SymbolTable<String>();
      
      table.index("t", "t");
      table.index("te", "te");

      assertEquals(table.match("t"), "t");
      assertEquals(table.match("te"), "te");
      
      table.index("text", "text");
      table.index("texts", "texts");
      
      assertEquals(table.match("text"), "text");
      assertEquals(table.match("texts"), "texts");
      
      table.index("texted", "texted");
      
      assertEquals(table.match("text"), "text");
      assertEquals(table.match("texts"), "texts");
      assertEquals(table.match("texted"), "texted");
      
      table.index("name", "name");
      table.index("age", "age");
      table.index("attrs", "attrs");
      table.index("address", "address");
      
      assertEquals(table.match("text"), "text");
      assertEquals(table.match("texts"), "texts");
      assertEquals(table.match("texted"), "texted");
      assertEquals(table.match("name"), "name");
      assertEquals(table.match("age"), "age");
      assertEquals(table.match("attrs"), "attrs");
      assertEquals(table.match("address"), "address");
      
      final Runnable task = new Runnable() {
         
         @Override
         public void run() {
            for(int i = 0; i < 10000000; i++) {
               assertEquals(table.match("texted"), "texted");
            }
         }
      };
      timeRun("resolve from true", task);
   }
}
