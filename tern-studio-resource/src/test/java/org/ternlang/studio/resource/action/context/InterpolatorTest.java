package org.ternlang.studio.resource.action.context;

import junit.framework.TestCase;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.HashContext;
import org.ternlang.studio.resource.action.Interpolator;
import org.ternlang.studio.resource.action.Model;
import org.ternlang.studio.resource.action.build.MockRequest;
import org.ternlang.studio.resource.action.build.MockResponse;

public class InterpolatorTest extends TestCase {

   public void testInterpolation() {
      MockRequest request = new MockRequest("GET", "/a/b/c/showA?x=X&y=Y", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new HashContext(request, response);
      Interpolator interpolator = new Interpolator(context);

      assertEquals(interpolator.interpolate("The value of y is '${y}'"), "The value of y is 'Y'");
      assertEquals(interpolator.interpolate("x=${x} y=${y}"), "x=X y=Y");
      assertEquals(interpolator.interpolate("x=${x} y=${y} z=${Z}"), "x=X y=Y z=${Z}");

      Model model = context.getModel();
      model.setAttribute("z", "Z");

      assertEquals(interpolator.interpolate("x=${x} y=${y} z=${z}"), "x=X y=Y z=Z");

      model.setAttribute("x", "X overridden by model");

      assertEquals(interpolator.interpolate("x=${x} y=${y} z=${z}"), "x=X overridden by model y=Y z=Z");
   }

   public void testPartialToken() {
      MockRequest request = new MockRequest("GET", "/?x=X&y=Y", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new HashContext(request, response);
      Interpolator interpolator = new Interpolator(context);

      assertEquals(interpolator.interpolate("$"), "$");
      assertEquals(interpolator.interpolate("$x"), "$x");
      assertEquals(interpolator.interpolate("${y"), "${y");
      assertEquals(interpolator.interpolate("${y}"), "Y");

   }

}
