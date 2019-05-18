package org.ternlang.studio.resource.action.build;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.HashContext;
import org.ternlang.studio.resource.action.Model;
import org.ternlang.studio.resource.action.annotation.Component;
import org.ternlang.studio.resource.action.annotation.Path;
import org.ternlang.studio.resource.action.annotation.QueryParam;
import org.ternlang.studio.resource.action.annotation.Required;
import org.ternlang.studio.resource.action.build.ComponentFinder;
import org.ternlang.studio.resource.action.build.DependencySystem;
import org.ternlang.studio.resource.action.build.MapSystem;
import org.ternlang.studio.resource.action.build.MethodDispatcher;
import org.ternlang.studio.resource.action.build.MethodScanner;
import org.ternlang.studio.resource.action.build.MethodScannerResolver;
import org.ternlang.studio.resource.action.extract.CookieExtractor;
import org.ternlang.studio.resource.action.extract.Extractor;
import org.ternlang.studio.resource.action.extract.HeaderExtractor;
import org.ternlang.studio.resource.action.extract.ModelExtractor;
import org.ternlang.studio.resource.action.extract.PartExtractor;
import org.ternlang.studio.resource.action.extract.QueryExtractor;
import org.ternlang.studio.resource.action.extract.RequestExtractor;
import org.ternlang.studio.resource.action.extract.ResponseExtractor;

import junit.framework.TestCase;

public class ValidationTest extends TestCase {

   @Component
   public static class SomeComponent {

      @Required
      @QueryParam("a")
      String a;

      @Required
      @QueryParam("b")
      String b;

      @Required
      @QueryParam("int")
      int value;

      @QueryParam("long")
      int num;

      String someFieldWithNoAnnotation = "X";

      public SomeComponent() {
         super();
      }

      public boolean isValid() {
         return a != null && b != null;
      }
   }

   @Component
   public static class InvalidComponent {

      @Required
      @QueryParam("a")
      public String x;

      @Required
      @QueryParam("b")
      public String y;

   }

   @Path("/somePath")
   public static class ControllerThatTakesComponent {

      @Path
      public void updateComponent(InvalidComponent component, Model model) {
         model.setAttribute("x", "x");
      }

   }

   public void testControllerWithValidation() throws Throwable {
      List<Extractor> extractors = new LinkedList<Extractor>();
      extractors.add(new RequestExtractor());
      extractors.add(new ResponseExtractor());
      extractors.add(new ModelExtractor());
      extractors.add(new QueryExtractor());
      extractors.add(new CookieExtractor());
      extractors.add(new HeaderExtractor());
      extractors.add(new PartExtractor());
      DependencySystem dependencySystem = new MapSystem(Collections.EMPTY_MAP);
      ComponentFinder finder = new ComponentFinder(ControllerThatTakesComponent.class);
      MethodScanner scanner = new MethodScanner(dependencySystem, extractors);
      MethodScannerResolver resolver = new MethodScannerResolver(scanner, finder);
      MockRequest request = new MockRequest("GET", "/some-path/update-component?a=niall.gallagher@rbs.com&enum=X", "");
      MockResponse response = new MockResponse(System.out);
      Context context = new HashContext(request, response);
      MethodDispatcher dispatcher = resolver.resolveBest(context);
      dispatcher.execute(context);

      assertFalse(context.getValidation().isValid());
      assertEquals(context.getValidation().getErrors().size(), 1);
   }
   /*
    * public void testDataBinder() throws Exception { ValidatorFactory factory =
    * Validation.buildDefaultValidatorFactory(); Validator validator =
    * factory.getValidator(); ConstructorScanner scanner = createScanner();
    * List<ComponentBuilder> builders = scanner.scan(InvalidComponent.class);
    * 
    * MockRequest request = new MockRequest("GET", "/?a=Some+value", "");
    * MockResponse response = new MockResponse(); Model model = new HashModel();
    * ComponentBuilder builder = builders.iterator().next(); Object value =
    * builder.build(request, response, model); InvalidComponent component =
    * (InvalidComponent)value;
    * 
    * assertNotNull(value); assertEquals(value.getClass(),
    * InvalidComponent.class);
    * 
    * DataBinder binder = new DataBinder(value); SpringValidatorAdapter adapter
    * = new SpringValidatorAdapter(validator);
    * 
    * binder.addValidators(adapter); binder.validate();
    * 
    * BindingResult result = binder.getBindingResult();
    * 
    * assertNotNull(result); assertEquals(component.x, "Some value");
    * assertEquals(component.y, null); assertEquals(result.getErrorCount(), 1);
    * 
    * List<ObjectError> errors = result.getAllErrors();
    * 
    * for(ObjectError error : errors) {
    * System.err.println(error.getDefaultMessage()); } }
    * 
    * public void testValidator() throws Exception { InvalidComponent component
    * = new InvalidComponent(); ValidatorFactory factory =
    * Validation.buildDefaultValidatorFactory(); Validator validator =
    * factory.getValidator();
    * 
    * Set<ConstraintViolation<InvalidComponent>> violations =
    * validator.validate(component);
    * 
    * assertEquals(violations.size(), 2);
    * 
    * for(ConstraintViolation<InvalidComponent> violation : violations) {
    * System.err.println(violation.getMessage()); }
    * System.err.println(violations); }
    * 
    * public void testComponentBuilder() throws Exception { ConstructorScanner
    * scanner = createScanner(); List<ComponentBuilder> builders =
    * scanner.scan(SomeComponent.class); MockRequest request = new
    * MockRequest("GET", "/?a=A&b=B&int=5", ""); MockResponse response = new
    * MockResponse(); Model model = new HashModel(); ComponentBuilder builder =
    * builders.iterator().next(); Object value = builder.build(request,
    * response, model); SomeComponent component = (SomeComponent)value;
    * 
    * assertNotNull(value); assertEquals(component.a, "A");
    * assertEquals(component.b, "B"); assertEquals(component.value, 5);
    * assertEquals(component.someFieldWithNoAnnotation, "X");
    * 
    * 
    * ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    * Validator validator = factory.getValidator();
    * 
    * Set<ConstraintViolation<SomeComponent>> violations =
    * validator.validate(component);
    * 
    * assertEquals(violations.size(), 0);
    * 
    * }
    * 
    * private static ConstructorScanner createScanner() { List<Extractor>
    * extractors = new LinkedList<Extractor>(); extractors.add(new
    * RequestExtractor()); extractors.add(new ResponseExtractor());
    * extractors.add(new ModelExtractor()); extractors.add(new
    * ParameterExtractor()); extractors.add(new QueryExtractor());
    * extractors.add(new CookieExtractor()); extractors.add(new
    * HeaderExtractor()); extractors.add(new PartExtractor()); return new
    * ConstructorScanner(extractors); }
    */
}
