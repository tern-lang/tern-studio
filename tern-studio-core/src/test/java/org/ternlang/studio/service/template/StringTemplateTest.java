package org.ternlang.studio.service.template;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.ternlang.service.template.PropertyBinder;
import org.ternlang.service.template.PropertyTemplateFilter;
import org.ternlang.service.template.StringTemplate;
import org.ternlang.service.template.TemplateModel;

import junit.framework.TestCase;

public class StringTemplateTest extends TestCase {

   public void testTemplateEngine() throws Exception {
      StringTemplate template = new StringTemplate(null, "path", "foo ${project} blah ${fancytree-css} bar", -1);
      PropertyBinder binder = new PropertyBinder();
      Map<String, Object> variables = new HashMap<String, Object>();
      TemplateModel model = new TemplateModel(variables);
      PropertyTemplateFilter filter = new PropertyTemplateFilter(model, binder);
      StringWriter writer = new StringWriter();
      
      model.setAttribute("project", "demo");
      template.render(filter, writer);
      
      System.err.println(writer);
   }
   
}
