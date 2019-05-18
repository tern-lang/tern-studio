package org.ternlang.studio.resource.action.extract;

import java.lang.annotation.Annotation;
import java.util.Collections;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.Context;
import org.ternlang.studio.resource.action.HashContext;
import org.ternlang.studio.resource.action.build.MockRequest;
import org.ternlang.studio.resource.action.build.MockResponse;

import junit.framework.TestCase;

public class StringConverterExtractorTest extends TestCase {
   
   public void testStringConverterExtractor() throws Exception {
//      QueryExtractor extractor = new QueryExtractor();
//      Parameter arrayOfStringParameter = new Parameter(String[].class, null, Collections.<Class, Annotation>emptyMap(), true);
//      Parameter arrayOfIntegerParameter = new Parameter(int[].class, null, Collections.<Class, Annotation>emptyMap(), true);
//      Parameter arrayOfLongParameter = new Parameter(Long[].class, null, Collections.<Class, Annotation>emptyMap(), true);
//      Parameter arrayOfBooleanParameter = new Parameter(boolean[].class, null, Collections.<Class, Annotation>emptyMap(), true);
//      Parameter arrayOfDoubleParameter = new Parameter(double[].class, null, Collections.<Class, Annotation>emptyMap(), true);
//      
//      assertTrue(extractor.accept(arrayOfStringParameter));
//      assertTrue(extractor.accept(arrayOfIntegerParameter));
//      assertTrue(extractor.accept(arrayOfLongParameter));
//      assertTrue(extractor.accept(arrayOfBooleanParameter));
//      assertTrue(extractor.accept(arrayOfDoubleParameter));
//      
//      Request request = new MockRequest("GET", "/blah?x=1&x=2&x=3", "");
//      Response response = new MockResponse();
//      Context context = new HashContext(request, response);
//      
//      Object stringArray = extractor.extract(arrayOfStringParameter, context);
//      
//      assertEquals(stringArray.getClass(), String[].class);
//      assertEquals(((String[])stringArray).length, 3);
//      assertEquals(((String[])stringArray)[0], "1");
//      assertEquals(((String[])stringArray)[1], "2");  
//      assertEquals(((String[])stringArray)[2], "3");
//      
//      Object integerArray = extractor.extract(arrayOfIntegerParameter, context);
//      
//      assertEquals(integerArray.getClass(), int[].class);
//      assertEquals(((int[])integerArray).length, 3);
//      assertEquals(((int[])integerArray)[0], 1);
//      assertEquals(((int[])integerArray)[1], 2);  
//      assertEquals(((int[])integerArray)[2], 3);  
//      
//      Object longArray = extractor.extract(arrayOfLongParameter, context);
//      
//      assertEquals(longArray.getClass(), Long[].class);
//      assertEquals(((Long[])longArray).length, 3);
//      assertEquals(((Long[])longArray)[0], new Long(1L));
//      assertEquals(((Long[])longArray)[1], new Long(2L));  
//      assertEquals(((Long[])longArray)[2], new Long(3L));  
//      
//      Object doubleArray = extractor.extract(arrayOfDoubleParameter, context);
//      
//      assertEquals(doubleArray.getClass(), double[].class);
//      assertEquals(((double[])doubleArray).length, 3);
//      assertEquals(((double[])doubleArray)[0], 1.0);
//      assertEquals(((double[])doubleArray)[1], 2.0);  
//      assertEquals(((double[])doubleArray)[2], 3.0);        
   }

}
