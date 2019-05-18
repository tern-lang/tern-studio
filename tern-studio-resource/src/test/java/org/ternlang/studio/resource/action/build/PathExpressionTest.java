package org.ternlang.studio.resource.action.build;

import java.util.Map;

import org.ternlang.studio.resource.action.annotation.GET;
import org.ternlang.studio.resource.action.build.MethodMatcher;

import junit.framework.TestCase;

public class PathExpressionTest extends TestCase {

   public void testSlash() throws Exception {
      MethodMatcher parser = new MethodMatcher(GET.class, null, "/");
      Map<String, String> parameters = parser.evaluate("/path/1/path.2");

      assertTrue(parameters.isEmpty());
   }

   public void testSimpleExpressions() throws Exception {
      MethodMatcher parser = new MethodMatcher(GET.class, null, "/{param}/.*");
      Map<String, String> parameters = parser.evaluate("/path/1/path.2");

      assertEquals(parameters.get("param"), "path");

   }

   public void testExpressions() throws Exception {
      MethodMatcher parser = new MethodMatcher(GET.class, null, "/path/{param1}/path.{param2}");
      Map<String, String> parameters = parser.evaluate("/path/1/path.2");

      assertEquals(parameters.get("param1"), "1");
      assertEquals(parameters.get("param2"), "2");

   }

   public void testMultipleParts() throws Exception {
      MethodMatcher parser = new MethodMatcher(GET.class, null, "/rootPath/{x}", "/path/{param1}/path.{param2}");
      Map<String, String> parameters = parser.evaluate("/rootPath/test/path/1/path.2");

      assertEquals(parameters.get("x"), "test");
      assertEquals(parameters.get("param1"), "1");
      assertEquals(parameters.get("param2"), "2");

   }

   public void testMultiplePartsThatMayNotConnectWell() throws Exception {
      MethodMatcher parser = new MethodMatcher(GET.class, null, "/rootPath/{x}/", "path/{param1}/path.{param2}");
      Map<String, String> parameters = parser.evaluate("/rootPath/test/path/1/path.2");

      assertEquals(parameters.get("x"), "test");
      assertEquals(parameters.get("param1"), "1");
      assertEquals(parameters.get("param2"), "2");
   }

   public void testMoreMultiplePartsThatMayNotConnectWell() throws Exception {
      MethodMatcher parser = new MethodMatcher(GET.class, null, "/rootPath/{x}", "path/{param1}/path.{param2}");
      Map<String, String> parameters = parser.evaluate("/rootPath/test/path/1/path.2");

      assertEquals(parameters.get("x"), "test");
      assertEquals(parameters.get("param1"), "1");
      assertEquals(parameters.get("param2"), "2");

   }
}
