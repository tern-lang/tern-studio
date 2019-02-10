package tern.studio.index.complete;

import junit.framework.TestCase;

public class InputExpressionParserTest extends TestCase {

   private static final String SOURCE_1 =
   "var list: List = new ArrayList();\n"+
   "list.stream()\n"+
   "   .filter(x -> { return x > 0; })\n"+
   "   .map(x -> x.y)\n"+
   "   .collect(\n";

   private static final String SOURCE_2 =
   "var list: List = ArrayList();\n"+
   "list.add(\"this is\n"+
   "   text to append 'blah'\n"+
   "   foo\").stream()\n"+
   "   .filter(x -> x > 0)\n"+
   "   .co";
   
   private static final String SOURCE_3 =
   "var list: List = new ArrayList();\n"+
   "list.stream()\n"+
   "   .filter(x -> { return x > 0; })\n"+
   "   .map(x -> x.";
   
   private static final String SOURCE_4 =
   "array[builder.toStri";
   
   private static final String SOURCE_5 =
   "   shape.getBounds().draw(";
   
   private static final String SOURCE_6 =
   "  value + array[index].g";
   
   private static final String SOURCE_7 =
   "module Constants {\n"+
   "   const MAX_VAL = 11;\n"+
   "   const MIN_VAL = 0;\n"+      
   "}\n"+
   "\n"+
   "Constants.M";
     
   private static final String SOURCE_8 =
   "  return list.stream().filter(x -> x.bool).comp";
   
   private static final String SOURCE_9 =
   "  map?.entrySet()?.stream()";
   
   public void testMultiLineExpression() throws Exception {
      String expression = InputExpressionParser.parseLine(SOURCE_1, 5);
      assertEquals(expression, "list.stream().filter(x -> { return x > 0; }).map(x -> x.y).collect(");
   }
   
   public void testMultiLineStringInExpression() throws Exception {
      String expression = InputExpressionParser.parseLine(SOURCE_2, 6);
      assertEquals(expression, "list.add(\"this istext to append 'blah'foo\").stream().filter(x -> x > 0).co");
   }
   
   public void testExpressionInExpression() throws Exception {
      String expression = InputExpressionParser.parseLine(SOURCE_3, 4);
      assertEquals(expression, "x.");
   }
   
   public void testExpressionInArrayIndex() throws Exception {
      String expression = InputExpressionParser.parseLine(SOURCE_4, 1);
      assertEquals(expression, "builder.toStri");
   }
   
   public void testBasicExpression() throws Exception {
      String expression = InputExpressionParser.parseLine(SOURCE_5, 1);
      assertEquals(expression, "shape.getBounds().draw(");
   }
   
   public void testExpressionInCalculation() throws Exception {
      String expression = InputExpressionParser.parseLine(SOURCE_6, 1);
      assertEquals(expression, "array[index].g");
   }
   
   public void testExpressionBeforeClosingBrace() throws Exception {
      String expression = InputExpressionParser.parseLine(SOURCE_7, 6);
      assertEquals(expression, "Constants.M");
   }
   
   public void testReturnExpression() throws Exception {
      String expression = InputExpressionParser.parseLine(SOURCE_8, 1);
      assertEquals(expression, "list.stream().filter(x -> x.bool).comp");
   }
   
   public void testSafeNavigationExpression() throws Exception {
      String expression = InputExpressionParser.parseLine(SOURCE_9, 1);
      assertEquals(expression, "map.entrySet().stream()");
   }
}
