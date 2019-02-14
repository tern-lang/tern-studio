package org.ternlang.studio.common.find;

import org.ternlang.studio.common.find.PatternMatchEvaluator;

import junit.framework.TestCase;

public class PatternMatchEvaluatorTest extends TestCase {
   
   public void testPatternMatchEvaluator() throws Exception {
      PatternMatchEvaluator caseSensitive = new PatternMatchEvaluator("he\\w+o", true);
      PatternMatchEvaluator notCaseSensitive = new PatternMatchEvaluator("he\\w+o", false);
      String caseSensitiveResult = caseSensitive.replace("one Hello foo hefo hello heo", "[XXX]");
      String notCaseSensitiveResult = notCaseSensitive.replace("one Hello foo hefo hello heo", "[XXX]");
      
      assertEquals("one Hello foo [XXX] [XXX] heo", caseSensitiveResult);
      assertEquals("one [XXX] foo [XXX] [XXX] heo", notCaseSensitiveResult);
   }

}
