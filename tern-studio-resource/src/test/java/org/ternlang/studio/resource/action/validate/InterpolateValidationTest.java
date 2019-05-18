package org.ternlang.studio.resource.action.validate;

import java.util.Iterator;

import junit.framework.TestCase;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.HashContext;
import org.ternlang.studio.resource.action.build.MockRequest;
import org.ternlang.studio.resource.action.build.MockResponse;
import org.ternlang.studio.resource.action.validate.ContextValidation;
import org.ternlang.studio.resource.action.validate.Validation;

public class InterpolateValidationTest extends TestCase {

   public void testValidation() throws Exception {
      MockRequest request = new MockRequest("GET", "/a/b/c/showA?x=X&y=Y", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new HashContext(request, response);
      Validation validation = new ContextValidation(context);

      validation.addError("The value of ${x} is wrong");
      validation.addError("x=${x} y=${y} z=${z}");

      Iterator<String> iterator = validation.iterator();

      assertEquals(iterator.next(), "The value of X is wrong");
      assertEquals(iterator.next(), "x=X y=Y z=${z}");
   }
}
