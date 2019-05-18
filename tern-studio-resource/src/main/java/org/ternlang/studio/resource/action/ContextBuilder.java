package org.ternlang.studio.resource.action;

import static java.util.Collections.EMPTY_LIST;

import java.util.List;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

public class ContextBuilder {

   private final List<Variable> variables;

   public ContextBuilder() {
      this(EMPTY_LIST);
   }

   public ContextBuilder(List<Variable> variables) {
      this.variables = variables;
   }

   public Context build(Request request, Response response) throws Exception {
      Context context = new HashContext(request, response);

      for (Variable variable : variables) {
         variable.update(request, response, context);
      }
      return context;
   }
}
