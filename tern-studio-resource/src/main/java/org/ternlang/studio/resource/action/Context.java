package org.ternlang.studio.resource.action;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.validate.Validation;

public interface Context {   
   Object getResult();
   void setResult(Object result);
   Validation getValidation();
   void setValidation(Validation validation);
   Throwable getError();
   void setError(Throwable cause);
   Response getResponse();
   void setResponse(Response response);
   Request getRequest();
   void setRequest(Request request);
   Model getModel();
   void setModel(Model model);
}
