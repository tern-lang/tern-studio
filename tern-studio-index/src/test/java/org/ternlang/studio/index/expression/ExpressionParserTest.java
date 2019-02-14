package org.ternlang.studio.index.expression;

import junit.framework.TestCase;

public class ExpressionParserTest extends TestCase {
   
   public void testBoolean() {
      ExpressionParser parser = new ExpressionParser("type.method(true, false)");
      
      assertEquals(parser.getTokens().size(), 2);
      assertEquals(parser.getTokens().get(0).getName(), "type");
      assertEquals(parser.getTokens().get(0).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(1).getName(), "method");
      assertEquals(parser.getTokens().get(1).getArguments().size(), 2);
      assertEquals(parser.getTokens().get(1).getArguments().get(0).getValue(), Boolean.TRUE);
      assertFalse(parser.getTokens().get(1).getArguments().get(0).isExpression());  
      assertEquals(parser.getTokens().get(1).getArguments().get(1).getValue(), Boolean.FALSE);
      assertFalse(parser.getTokens().get(1).getArguments().get(1).isExpression());  
   }
   
   public void testIntegerIndex() {
      ExpressionParser parser = new ExpressionParser("this.example.value[12]");
      
      assertEquals(parser.getTokens().size(), 3);
      assertEquals(parser.getTokens().get(0).getName(), "this");
      assertEquals(parser.getTokens().get(0).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(1).getName(), "example");
      assertEquals(parser.getTokens().get(1).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(2).getName(), "value");
      assertEquals(parser.getTokens().get(2).getArguments().size(), 1);
      assertEquals(parser.getTokens().get(2).getArguments().get(0).getValue(), 12);
      assertFalse(parser.getTokens().get(2).getArguments().get(0).isExpression());  
   }
   
   public void testMultipleIntegersIndex() {
      ExpressionParser parser = new ExpressionParser("this.example.callMethod(1, 2,3,4 ,5, 6 ,7)");
      
      assertEquals(parser.getTokens().size(), 3);
      assertEquals(parser.getTokens().get(0).getName(), "this");
      assertEquals(parser.getTokens().get(0).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(1).getName(), "example");
      assertEquals(parser.getTokens().get(1).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(2).getName(), "callMethod");
      assertEquals(parser.getTokens().get(2).getArguments().size(), 7);
      assertEquals(parser.getTokens().get(2).getArguments().get(0).getValue(), 1);
      assertFalse(parser.getTokens().get(2).getArguments().get(0).isExpression());  
      assertEquals(parser.getTokens().get(2).getArguments().get(1).getValue(), 2);
      assertFalse(parser.getTokens().get(2).getArguments().get(1).isExpression());  
      assertEquals(parser.getTokens().get(2).getArguments().get(2).getValue(), 3);
      assertFalse(parser.getTokens().get(2).getArguments().get(2).isExpression());  
      assertEquals(parser.getTokens().get(2).getArguments().get(3).getValue(), 4);
      assertFalse(parser.getTokens().get(2).getArguments().get(3).isExpression());  
      assertEquals(parser.getTokens().get(2).getArguments().get(4).getValue(), 5);
      assertFalse(parser.getTokens().get(2).getArguments().get(4).isExpression());  
      assertEquals(parser.getTokens().get(2).getArguments().get(5).getValue(), 6);
      assertFalse(parser.getTokens().get(2).getArguments().get(5).isExpression());  
      assertEquals(parser.getTokens().get(2).getArguments().get(6).getValue(), 7);
      assertFalse(parser.getTokens().get(2).getArguments().get(6).isExpression());  
   }
   
   public void testNumbersAndDigitsMixed() {
      ExpressionParser parser = new ExpressionParser("this.example.callMethod(1, 12.003d,\"some text 'inner quote'\",4f ,555L,0xff,-1)");
      
      assertEquals(parser.getTokens().size(), 3);
      assertEquals(parser.getTokens().get(0).getName(), "this");
      assertEquals(parser.getTokens().get(0).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(1).getName(), "example");
      assertEquals(parser.getTokens().get(1).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(2).getName(), "callMethod");
      assertEquals(parser.getTokens().get(2).getArguments().size(), 7);
      assertEquals(parser.getTokens().get(2).getArguments().get(0).getValue(), new Integer(1));
      assertFalse(parser.getTokens().get(2).getArguments().get(0).isExpression());  
      assertEquals(parser.getTokens().get(2).getArguments().get(1).getValue(), new Double(12.003));
      assertFalse(parser.getTokens().get(2).getArguments().get(1).isExpression());  
      assertEquals(parser.getTokens().get(2).getArguments().get(2).getValue(), "some text 'inner quote'");
      assertFalse(parser.getTokens().get(2).getArguments().get(2).isExpression());  
      assertEquals(parser.getTokens().get(2).getArguments().get(3).getValue(), new Float(4));
      assertFalse(parser.getTokens().get(2).getArguments().get(3).isExpression());  
      assertEquals(parser.getTokens().get(2).getArguments().get(4).getValue(), new Long(555));
      assertFalse(parser.getTokens().get(2).getArguments().get(4).isExpression());  
      assertEquals(parser.getTokens().get(2).getArguments().get(5).getValue(), new Integer(255));
      assertFalse(parser.getTokens().get(2).getArguments().get(5).isExpression());  
      assertEquals(parser.getTokens().get(2).getArguments().get(6).getValue(), new Integer(-1));
      assertFalse(parser.getTokens().get(2).getArguments().get(6).isExpression());  
   }
   
   public void testStringAsIndex() {
      ExpressionParser parser = new ExpressionParser("this.example.value['key1']");
      
      assertEquals(parser.getTokens().size(), 3);
      assertEquals(parser.getTokens().get(0).getName(), "this");
      assertEquals(parser.getTokens().get(0).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(1).getName(), "example");
      assertEquals(parser.getTokens().get(1).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(2).getName(), "value");
      assertEquals(parser.getTokens().get(2).getArguments().size(), 1);
      assertEquals(parser.getTokens().get(2).getArguments().get(0).getValue(), "key1");
      assertFalse(parser.getTokens().get(2).getArguments().get(0).isExpression());
   }
   
   public void testMethodsInMethodsInMethodsWithTrickyStrings1() {
      ExpressionParser parser = new ExpressionParser("o(\"some text\",E)");
    
      assertEquals(parser.getTokens().size(), 1);
      assertEquals(parser.getTokens().get(0).getName(), "o");
      assertEquals(parser.getTokens().get(0).getArguments().size(), 2);
      assertEquals(parser.getTokens().get(0).getArguments().get(0).getValue(), "some text");
      assertFalse(parser.getTokens().get(0).getArguments().get(0).isExpression());
      assertEquals(parser.getTokens().get(0).getArguments().get(1).getValue(), "E");
      assertTrue(parser.getTokens().get(0).getArguments().get(1).isExpression());
   }
   
   public void testMethodsInMethodsInMethodsWithTrickyStringsRererences() {
      ExpressionParser parser = new ExpressionParser("a.b(C.D(\" This is a [] trickey index 'ok'\"), \"some text\",E.F.G(H.I('4')))");
    
      assertEquals(parser.getTokens().size(), 2);
      assertEquals(parser.getTokens().get(0).getName(), "a");
      assertEquals(parser.getTokens().get(0).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(1).getName(), "b");
      assertEquals(parser.getTokens().get(1).getArguments().size(), 3);
      assertEquals(parser.getTokens().get(1).getArguments().get(0).getValue(), "C.D(\" This is a [] trickey index 'ok'\")");
      assertTrue(parser.getTokens().get(1).getArguments().get(0).isExpression());
      assertEquals(parser.getTokens().get(1).getArguments().get(1).getValue(), "some text");
      assertFalse(parser.getTokens().get(1).getArguments().get(1).isExpression());
      assertEquals(parser.getTokens().get(1).getArguments().get(2).getValue(), "E.F.G(H.I('4'))");
   }
   
   public void testMethodsInMethodsInMethodsRererences() {
      ExpressionParser parser = new ExpressionParser("a.b(C.D('3'),E.F.G(H.I('4')))");
    
      assertEquals(parser.getTokens().size(), 2);
      assertEquals(parser.getTokens().get(0).getName(), "a");
      assertEquals(parser.getTokens().get(0).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(1).getName(), "b");
      assertEquals(parser.getTokens().get(1).getArguments().size(), 2);
      assertEquals(parser.getTokens().get(1).getArguments().get(0).getValue(), "C.D('3')");
      assertEquals(parser.getTokens().get(1).getArguments().get(1).getValue(), "E.F.G(H.I('4'))");
   }
   
   
   public void testMethodsInMethodsRererences() {
      ExpressionParser parser = new ExpressionParser("a.b(A.get(1),B.get(2),C.D('3'))");
    
      assertEquals(parser.getTokens().size(), 2);
      assertEquals(parser.getTokens().get(0).getName(), "a");
      assertEquals(parser.getTokens().get(0).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(1).getName(), "b");
      assertEquals(parser.getTokens().get(1).getArguments().size(), 3);
      assertEquals(parser.getTokens().get(1).getArguments().get(0).getValue(), "A.get(1)");
      assertEquals(parser.getTokens().get(1).getArguments().get(1).getValue(), "B.get(2)");
      assertEquals(parser.getTokens().get(1).getArguments().get(2).getValue(), "C.D('3')");
   }
   
   public void testSimpleRererences() {
      ExpressionParser parser = new ExpressionParser("this.panel.blah");
    
      assertEquals(parser.getTokens().size(), 3);
      assertEquals(parser.getTokens().get(0).getName(), "this");
      assertEquals(parser.getTokens().get(0).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(1).getName(), "panel");
      assertEquals(parser.getTokens().get(1).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(2).getName(), "blah");
      assertEquals(parser.getTokens().get(2).getArguments().size(), 0);
   }
   
   public void testIndexedRererences() {
      ExpressionParser parser = new ExpressionParser("this['some index'].panel.blah[\" something bigger 'in quote'\"]");
    
      assertEquals(parser.getTokens().size(), 3);
      assertEquals(parser.getTokens().get(0).getName(), "this");
      assertEquals(parser.getTokens().get(0).getArguments().get(0).getValue(), "some index");
      assertFalse(parser.getTokens().get(0).getArguments().get(0).isExpression());
      assertEquals(parser.getTokens().get(1).getName(), "panel");
      assertEquals(parser.getTokens().get(1).getArguments().size(), 0);
      assertEquals(parser.getTokens().get(2).getName(), "blah");
      assertEquals(parser.getTokens().get(2).getArguments().get(0).getValue(), " something bigger 'in quote'");
   }
}
