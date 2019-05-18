package org.ternlang.studio.resource.action;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.ternlang.studio.resource.action.validate.ContextValidation;
import org.ternlang.studio.resource.action.validate.Validation;

public class HashContext implements Context {

   private Validation validation;
   private Throwable error;
   private Response response;
   private Request request;
   private Object result;
   private Model model;

   public HashContext(Request request, Response response) {
      this.validation = new ContextValidation(this);
      this.model = new HashModel();
      this.request = request;
      this.response = response;
   }

   @Override
   public Object getResult() {
      return result;
   }

   @Override
   public void setResult(Object result) {
      this.result = result;
   }

   @Override
   public Validation getValidation() {
      return validation;
   }

   @Override
   public void setValidation(Validation validation) {
      this.validation = validation;
   }

   @Override
   public Throwable getError() {
      return error;
   }

   @Override
   public void setError(Throwable error) {
      this.error = error;
   }

   @Override
   public Response getResponse() {
      return response;
   }

   @Override
   public void setResponse(Response response) {
      this.response = response;
   }

   @Override
   public Request getRequest() {
      return request;
   }

   @Override
   public void setRequest(Request request) {
      this.request = request;
   }

   @Override
   public Model getModel() {
      return model;
   }

   @Override
   public void setModel(Model model) {
      this.model = model;
   }
}
